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

import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.object.LeftChatMember;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.NewChatMember;
import it.rebase.rebot.api.spi.PluginProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class WelcomeMessagePlugin implements PluginProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String WELCOME_MESSAGE = "Hello <b>%s</b>, welcome to the %s group. Do you know me? Try to type /help and see what I can do. " + Emoji.SMILING_FACE_WITH_OPEN_MOUTH;
    private final String GOODBYE_MESSAGE = "There were a traitor among us, <b>%s</b> left us." + Emoji.ANGRY_FACE;

    @Override
    public String process(MessageUpdate update) {
        return chatMember(update);
    }

    @Override
    public void load() {
        log.fine("Enabling plugin welcome-message-plugin.");
    }


    /**
     * When a member join, left or gets excluded from an Telegram group a msg will be sent to the target group.
     * If the member added or removed is a bot, no message is sent.
     * @param update {@link MessageUpdate}
     * @return true if the message is to inform a new member or if a member left the chat
     */
    private String chatMember(MessageUpdate update) {
        ObjectMapper mapper = new ObjectMapper();
        final Message message = new Message();
        for (Map.Entry<String, Object> entry : update.getMessage().getAdditionalProperties().entrySet()) {
            log.fine("Additional Properties: KEY + " + entry.getKey() + " - VALUE " + entry.getValue().toString());
            if (entry.getKey().equals("new_chat_member") && !update.getMessage().getFrom().isIsBot()) {
                NewChatMember member = mapper.convertValue(entry.getValue(), NewChatMember.class);
                message.setText(String.format(WELCOME_MESSAGE, member.getFirst_name(), update.getMessage().getChat().getTitle()));

            } else if (entry.getKey().equals("left_chat_participant") && !update.getMessage().getFrom().isIsBot()) {
                LeftChatMember member = mapper.convertValue(entry.getValue(), LeftChatMember.class);
                message.setText(String.format(GOODBYE_MESSAGE, member.getFirst_name()));
            }
        }
        return message.getText();
    }
}