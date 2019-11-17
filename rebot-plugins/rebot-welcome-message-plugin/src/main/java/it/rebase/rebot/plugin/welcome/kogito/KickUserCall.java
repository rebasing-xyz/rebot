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

package it.rebase.rebot.plugin.welcome.kogito;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.message.sender.MessageSender;
import it.rebase.rebot.api.object.Chat;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.user.management.UserManagement;

@ApplicationScoped
public class KickUserCall {

    @Inject
    UserManagement userManagement;

    @Inject
    MessageSender sender;

    public WelcomeChallenge kickUser(WelcomeChallenge welcomeChallenge) {
        System.out.println("kicking user " + welcomeChallenge.toString());
        userManagement.kickUser(welcomeChallenge.getUser_id(), welcomeChallenge.getChat_id(), 20L);

        // send kicked message
        String messageText = String.format(I18nHelper.resource("Welcome", welcomeChallenge.getLocale(), "challenge.timeout"),
                                       welcomeChallenge.getUser(),
                                       Emoji.ALARM_CLOCK);
        sender.processOutgoingMessage(buildMessage(welcomeChallenge.getChat_id(), messageText));

        return welcomeChallenge;
    }


    private Message buildMessage(Long target, String txt) {
        Chat chat = new Chat();
        Message message = new Message();
        chat.setId(target);
        message.setChat(chat);
        message.setText(txt);
        return message;
    }

}

