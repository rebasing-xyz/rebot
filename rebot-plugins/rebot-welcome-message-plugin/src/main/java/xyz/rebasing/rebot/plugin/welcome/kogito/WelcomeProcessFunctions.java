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

package xyz.rebasing.rebot.plugin.welcome.kogito;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import xyz.rebasing.rebot.api.emojis.Emoji;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.management.message.MessageManagement;
import xyz.rebasing.rebot.api.management.user.UserManagement;
import xyz.rebasing.rebot.api.message.sender.MessageSender;
import xyz.rebasing.rebot.api.object.Chat;
import xyz.rebasing.rebot.api.object.Message;

@ApplicationScoped
public class WelcomeProcessFunctions {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @Named("welcome.challenge")
    Process<? extends Model> welcomeProcess;

    @Inject
    UserManagement userManagement;

    @Inject
    MessageManagement messageManagement;

    @Inject
    MessageSender reply;

    public WelcomeChallenge kickUser(WelcomeChallenge welcomeChallenge) {
        String response = String.format(I18nHelper.resource("Welcome", welcomeChallenge.getLocale(), "challenge.wrong.answer"),
                                        welcomeChallenge.getName(),
                                        welcomeChallenge.getAnswer(),
                                        welcomeChallenge.result(),
                                        Emoji.DISAPPOINTED_FACE);

        // get the sent message's id to be deleted.
        welcomeChallenge.addMessadeIdToDelete(
                reply.processOutgoingMessage(new Message(welcomeChallenge.getMessageId(),
                                                         new Chat(welcomeChallenge.getChatId(), welcomeChallenge.getChatTitle()),
                                                         response), false, 0).getAsLong()
        );

        // lazy kicker, wait 20 seconds before kick user out so he can read the message
        log.debugv("Kicking user {0} ----- From Chat {1} ---- With Challenge {2}",
                   welcomeChallenge.getUser(),
                   welcomeChallenge.getChatTitle(),
                   welcomeChallenge.toString());
        userManagement.kickUser(welcomeChallenge.getUserId(), welcomeChallenge.getChatId(), 20L);
        return welcomeChallenge;
    }

    public WelcomeChallenge kickUserTimeout(WelcomeChallenge welcomeChallenge) {
        log.debugv("kicking user with Challenge: {0}", welcomeChallenge.toString());
        // send kicked message
        String response = String.format(I18nHelper.resource("Welcome", welcomeChallenge.getLocale(), "challenge.timeout"),
                                        welcomeChallenge.getName(),
                                        Emoji.ALARM_CLOCK);

        // get the sent message's id to be deleted.
        welcomeChallenge.addMessadeIdToDelete(
                reply.processOutgoingMessage(new Message(welcomeChallenge.getMessageId(),
                                                         new Chat(welcomeChallenge.getChatId(),
                                                                  welcomeChallenge.getChatTitle()),
                                                         response), false, 0).getAsLong()
        );
        userManagement.kickUser(welcomeChallenge.getUserId(), welcomeChallenge.getChatId(), 20L);
        return welcomeChallenge;
    }

    public WelcomeChallenge sendChallengeMessage(WelcomeChallenge welcomeChallenge) {
        log.debugv("Starting Challenge {0}", welcomeChallenge.toString());
        String response = (String.format(I18nHelper.resource("Welcome", welcomeChallenge.getLocale(), "challenge.start"),
                                         welcomeChallenge.getName(),
                                         welcomeChallenge.getChatTitle(),
                                         welcomeChallenge.showMathOperation()));

        // get the sent message's id to be deleted.
        welcomeChallenge.addMessadeIdToDelete(
                reply.processOutgoingMessage(new Message(welcomeChallenge.getMessageId(),
                                                         new Chat(welcomeChallenge.getChatId(), welcomeChallenge.getChatTitle()),
                                                         response), false, 0).getAsLong()
        );
        return welcomeChallenge;
    }

    public WelcomeChallenge sendWelcomeMessage(WelcomeChallenge welcomeChallenge) {
        log.debugv("Welcoming user {0} to group id[{1}] -name [{2}]",
                   welcomeChallenge.getUser(), welcomeChallenge.getChatId(), welcomeChallenge.getChatTitle());
        String response = (String.format(I18nHelper.resource("Welcome", welcomeChallenge.getLocale(), "welcome"),
                                         welcomeChallenge.getName(),
                                         welcomeChallenge.getChatTitle(),
                                         Emoji.FACE_WITH_STUCK_OUT_TONGUE_AND_TIGHTLY_CLOSED_EYES));

        // get the sent message's id to be deleted.
        welcomeChallenge.addMessadeIdToDelete(
                reply.processOutgoingMessage(new Message(welcomeChallenge.getMessageId(),
                                                         new Chat(welcomeChallenge.getChatId(), welcomeChallenge.getChatTitle()),
                                                         response), false, 0).getAsLong()
        );
        return welcomeChallenge;
    }

    public void deleteMessage(WelcomeChallenge welcomeChallenge) {
        log.info("Welcome Process - Deleting message id [" + Arrays.asList(welcomeChallenge.getMessadeIdToDelete()) + "] from [" + welcomeChallenge.getChatId() + "].");
        welcomeChallenge.getMessadeIdToDelete().stream().forEach(messageId ->
                                                                         messageManagement.deleteMessage(welcomeChallenge.getChatId(), messageId, 30)
        );
    }
}

