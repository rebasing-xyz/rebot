/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.plugin.welcome;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.message.sender.MessageSender;
import it.rebase.rebot.api.object.ChatMember;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.api.user.management.UserManagement;
import it.rebase.rebot.plugin.welcome.kogito.WelcomeChallenge;
import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;

import static it.rebase.rebot.plugin.welcome.filter.WelcomePluginPredicate.hasMemberLeft;
import static it.rebase.rebot.plugin.welcome.filter.WelcomePluginPredicate.hasNewMember;
import static it.rebase.rebot.plugin.welcome.filter.WelcomePluginPredicate.senderIsNotBot;

@ApplicationScoped
public class WelcomeMessagePlugin implements PluginProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @Named("welcome.challenge")
    Process<? extends Model> welcomeProcess;

    @Inject
    UserManagement userManagement;

    @Inject
    MessageSender reply;

    private Predicate newMember = hasNewMember();
    private Predicate leftMember = hasMemberLeft().and(senderIsNotBot());

    /**
     * Decide if the new member will be kicked from group or not.
     * When a new user join the chat group, a new challenge will be set to him
     * if the challenge is not correctly answered or if a answer is not provided at all
     * the new member will be kicker out from the group.
     * @param update message to be processed
     * @return the welcome or goodbye message when new member joins
     * will also return the challenge in case of new comers.
     */
    @Override
    public String process(MessageUpdate update) {
        String locale = update.getMessage().getFrom().getLanguageCode();

        if (leftMember.test(update)) {
            return leftChatMemberMessage(update, locale);
        } else {

            Collection<? extends ProcessInstance<? extends Model>> instances = welcomeProcess.instances().values();

            // if new member and if there is not started process, start a new challenge
            //instances.isEmpty() &&
            if (newMember.test(update)) {
                for (ChatMember member : chatMember(update)) {
                    String username = null != member.getUsername() ? member.getUsername() : member.getFirst_name();

                    WelcomeChallenge challenge = new WelcomeChallenge(username);
                    challenge.setUser_id(member.getId());
                    challenge.setChat_id(update.getMessage().getChat().getId());
                    challenge.setLocale(update.getMessage().getFrom().getLanguageCode());

                    Model model = welcomeProcess.createModel();
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("challenge", challenge);
                    model.fromMap(parameters);

                    ProcessInstance<?> processInstance = welcomeProcess.createInstance(model);
                    processInstance.start();

                    SecurityPolicy policy = securityProviderForUser(username);
                    processInstance.workItems(policy);

                    List<WorkItem> workItems = processInstance.workItems(policy);
                    processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Claim.ID, parameters, policy));

                    update.getMessage().setText(String.format(I18nHelper.resource("Welcome", locale, "challenge.start"),
                                                              username,
                                                              update.getMessage().getChat().getTitle(),
                                                              challenge.showMathOperation()));

                    reply.processOutgoingMessage(update.getMessage());
                }
                // msgs will be handled internally if there is more than 1 new member at the same time.
                // so return null.
                return null;
            } else if (instances.size() > 0) {
                String username = null != update.getMessage().getFrom().getUsername()
                        ? update.getMessage().getFrom().getUsername() : update.getMessage().getFrom().getFirstName();
                SecurityPolicy policy = securityProviderForUser(username);
                StringBuilder response = new StringBuilder();

                instances.stream().forEach(instance -> {
                    List<WorkItem> workItems = instance.workItems(policy);

                    if (workItems.size() > 0) {
                        String userAnswer = update.getMessage().getText().trim();

                        Map<String, Object> results = new HashMap<>();
                        Model result = instance.variables();
                        WelcomeChallenge challenge = ((WelcomeChallenge) result.toMap().get("challenge"));
                        challenge.setAnswer(Integer.parseInt(userAnswer));

                        instance.transitionWorkItem(workItems.get(0).getId(),
                                                    new HumanTaskTransition(Complete.ID, results, policy));

                        if (challenge.isKickUser()) {
                            response.append(String.format(I18nHelper.resource("Welcome", challenge.getLocale(), "challenge.wrong.answer"),
                                                          username,
                                                          userAnswer,
                                                          challenge.result(),
                                                          Emoji.DISAPPOINTED_FACE));

                            // lazy kicker, wait 20 seconds before kick user out so he can read the message
                            userManagement.kickUser(update.getMessage().getFrom().getId(), update.getMessage().getChat().getId(), 20L);
                        } else {
                            response.append(String.format(I18nHelper.resource("Welcome", challenge.getLocale(), "welcome"),
                                                          update.getMessage().getFrom().getFirstName(),
                                                          update.getMessage().getChat().getTitle(),
                                                          Emoji.FACE_WITH_STUCK_OUT_TONGUE_AND_TIGHTLY_CLOSED_EYES));
                        }
                    }
                });

                return response.toString();
            }
        }
        //not expected to reach this point
        return null;
    }

    @Override
    public void load() {
        log.fine("Enabling welcome-message-plugin plugin.");
    }

    /**
     * When a member left or gets excluded from an Telegram group a msg will be sent to the target group.
     * If the member removed is a bot, no message is sent.
     * @param update {@link MessageUpdate}
     * @return true if the message if a member left the chat
     */
    private String leftChatMemberMessage(MessageUpdate update, String locale) {
        final Message message = new Message();
        message.setText(String.format(I18nHelper.resource("Welcome", locale, "traitor"),
                                      chatMember(update).get(0).getFirst_name(),
                                      Emoji.ANGRY_FACE));
        return message.getText();
    }

    /**
     * @param update
     * @return a parsed list of ChatMember Object
     */
    private List<ChatMember> chatMember(MessageUpdate update) {
        ObjectMapper mapper = new ObjectMapper();
        List<ChatMember> members = new ArrayList<>();
        for (Map.Entry<String, Object> entry : update.getMessage().getAdditionalProperties().entrySet()) {
            log.fine("Additional Properties: KEY + " + entry.getKey() + " - VALUE " + entry.getValue().toString());
            if (entry.getKey().equals("new_chat_members")) {
                return mapper.convertValue(entry.getValue(), mapper.getTypeFactory().constructCollectionType(List.class, ChatMember.class));
            } else if (entry.getKey().equals("left_chat_participant")) {
                members.add(mapper.convertValue(entry.getValue(), ChatMember.class));
            }
        }
        // not expected to achieve this point.
        return members;
    }

    /**
     * Create a Security Policy for the new member so the task can be claimed and completed when
     * the new member answers the challenge.
     * @param username
     * @return Kogito Security Policy
     */
    public SecurityPolicy securityProviderForUser(String username) {
        StaticIdentityProvider identity = new StaticIdentityProvider(username, Collections.singletonList("users"));
        return SecurityPolicy.of(identity);
    }
}