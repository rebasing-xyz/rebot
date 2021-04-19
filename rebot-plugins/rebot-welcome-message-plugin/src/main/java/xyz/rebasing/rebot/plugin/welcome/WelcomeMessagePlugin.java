/*
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2017 Rebasing.xyz ReBot
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

package xyz.rebasing.rebot.plugin.welcome;

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
import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.emojis.Emoji;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.management.message.MessageManagement;
import xyz.rebasing.rebot.api.management.user.UserManagement;
import xyz.rebasing.rebot.api.message.sender.MessageSender;
import xyz.rebasing.rebot.api.object.Chat;
import xyz.rebasing.rebot.api.object.ChatMember;
import xyz.rebasing.rebot.api.object.Message;
import xyz.rebasing.rebot.api.object.MessageUpdate;
import xyz.rebasing.rebot.api.spi.PluginProvider;
import xyz.rebasing.rebot.plugin.welcome.kogito.WelcomeChallenge;

import static xyz.rebasing.rebot.plugin.welcome.filter.WelcomePluginPredicate.hasMemberLeft;
import static xyz.rebasing.rebot.plugin.welcome.filter.WelcomePluginPredicate.hasNewMember;
import static xyz.rebasing.rebot.plugin.welcome.filter.WelcomePluginPredicate.isNewMemberBot;
import static xyz.rebasing.rebot.plugin.welcome.filter.WelcomePluginPredicate.senderIsNotBot;

@ApplicationScoped
public class WelcomeMessagePlugin implements PluginProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    @Named("welcome.challenge")
    Process<? extends Model> welcomeProcess;

    @Inject
    UserManagement userManagement;

    @Inject
    MessageManagement messageManagement;

    @Inject
    MessageSender reply;

    private Predicate newMember = hasNewMember();
    private Predicate leftMember = hasMemberLeft().and(senderIsNotBot());

    /**
     * Decide if the new member will be kicked from group or not.
     * When a new user join the chat group, a new challenge will be set to him
     * if the challenge is not correctly answered or if a answer is not provided at all
     * the new member will be kicker out from the group.
     *
     * @param messageUpdate message to be processed
     * @return the welcome or goodbye message when new member joins
     * will also return the challenge in case of new comers.
     */
    @Override
    public String process(MessageUpdate messageUpdate, String locale) {

        if (leftMember.test(messageUpdate)) {
            long id = reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(),
                                                               new Chat(messageUpdate.getMessage().getChat().getId(), messageUpdate.getMessage().getChat().getTitle()),
                                                               leftChatMemberMessage(messageUpdate, locale)), false, 0).getAsLong();
            // TODO remove after the delete message stuff is in place.
            messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(), id, 30);
        } else {

            StringBuilder response = new StringBuilder();
            Collection<? extends ProcessInstance<? extends Model>> instances = welcomeProcess.instances().values();

            if (newMember.test(messageUpdate)) {
                boolean isBotAdmin = userManagement.isBotAdministrator(messageUpdate);

                for (ChatMember member : chatMember(messageUpdate)) {

                    String username = null != member.getUsername() ? member.getUsername() : member.getFirst_name();
                    // add user+chatId to make sure that if the user joined two chat rooms at the same time the process will
                    // not get confused and handle the process wrongly
                    username = username + "-" + messageUpdate.getMessage().getChat().getId();

                    WelcomeChallenge challenge = new WelcomeChallenge(username);
                    challenge.setUserId(member.getId());
                    challenge.setChatId(messageUpdate.getMessage().getChat().getId());
                    challenge.setMessageId(messageUpdate.getMessage().getMessageId());
                    challenge.setChatTitle(messageUpdate.getMessage().getChat().getTitle());
                    challenge.setNewComerBot(isNewMemberBot().test(member));
                    challenge.setLocale(locale);

                    Model model = welcomeProcess.createModel();
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("challenge", challenge);
                    parameters.put("isBotAdmin", isBotAdmin);
                    model.fromMap(parameters);

                    ProcessInstance<?> processInstance = welcomeProcess.createInstance(model);
                    processInstance.start();

                    if (processInstance.status() == ProcessInstance.STATE_ACTIVE) {
                        SecurityPolicy policy = securityProviderForUser(username);
                        processInstance.workItems(policy);

                        List<WorkItem> workItems = processInstance.workItems(policy);
                        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Claim.ID, parameters, policy));
                    }
                }
                // msgs will be handled internally if there is more than 1 new member at the same time.
                // so return null.
                return null;
            } else if (instances.size() > 0) {
                String username = null != messageUpdate.getMessage().getFrom().getUsername()
                        ? messageUpdate.getMessage().getFrom().getUsername() : messageUpdate.getMessage().getFrom().getFirstName();
                // add user+chatId to make sure that if the user joined two chat rooms at the same time the process will
                // not get confused and handle the process wrongly
                username = username + "-" + messageUpdate.getMessage().getChat().getId();
                SecurityPolicy policy = securityProviderForUser(username);

                instances.stream().forEach(instance -> {
                    List<WorkItem> workItems = instance.workItems(policy);

                    if (workItems.size() > 0) {
                        String userAnswer = messageUpdate.getMessage().getText().trim();

                        Map<String, Object> results = new HashMap<>();
                        Model result = instance.variables();
                        WelcomeChallenge challenge = ((WelcomeChallenge) result.toMap().get("challenge"));
                        challenge.setAnswer(Integer.parseInt(userAnswer));
                        challenge.setMessageId(messageUpdate.getMessage().getMessageId());
                        challenge.addMessadeIdToDelete(messageUpdate.getMessage().getMessageId());

                        instance.transitionWorkItem(workItems.get(0).getId(),
                                                    new HumanTaskTransition(Complete.ID, results, policy));
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

    @Override
    public String name() {
        return "welcome";
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }

    /**
     * When a member left or gets excluded from an Telegram group a msg will be sent to the target group.
     * If the member removed is a bot, no message is sent.
     *
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
     *
     * @param username
     * @return Kogito Security Policy
     */
    public SecurityPolicy securityProviderForUser(String username) {
        StaticIdentityProvider identity = new StaticIdentityProvider(username, Collections.singletonList("users"));
        return SecurityPolicy.of(identity);
    }
}