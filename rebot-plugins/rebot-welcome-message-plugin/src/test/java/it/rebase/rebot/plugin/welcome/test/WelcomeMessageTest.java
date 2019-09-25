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

package it.rebase.rebot.plugin.welcome.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.plugin.welcome.WelcomeMessagePlugin;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class WelcomeMessageTest {

    private String newMember = "{" +
            "\"ok\": true," +
            "\"result\": [" +
            "{" +
            "\"update_id\": 311105828," +
            "\"message\": {" +
            "\"message_id\": 87073," +
            "\"from\": {" +
            "\"id\": 14289485," +
            "\"is_bot\": false," +
            "\"first_name\": \"Filippe\"," +
            "\"last_name\": \"Spolti\"," +
            "\"username\": \"fspolti\"," +
            "\"language_code\": \"en\"" +
            "}," +
            "\"chat\": {" +
            "\"id\": -234234212," +
            "\"title\": \"ReBot\"," +
            "\"type\": \"group\"," +
            "\"all_members_are_administrators\": true" +
            "}," +
            "\"date\": 1513173785," +
            "\"new_chat_participant\": {" +
            "\"id\": 199267392," +
            "\"is_bot\": false," +
            "\"first_name\": \"Mario\"," +
            "\"last_name\": \"Oliveira\"," +
            "\"username\": \"moliveira\"" +
            "}," +
            "\"new_chat_member\": {" +
            "\"id\": 199267392," +
            "\"is_bot\": false," +
            "\"first_name\": \"Mario\"," +
            "\"last_name\": \"Oliveira\"," +
            "\"username\": \"moliveira\"" +
            "}," +
            "\"new_chat_members\": [" +
            "{" +
            "\"id\": 199267392," +
            "\"is_bot\": false," +
            "\"first_name\": \"Mario\"," +
            "\"last_name\": \"Oliveira\"," +
            "\"username\": \"moliveira\"" +
            "}]}}]}";

    private String leftMember = "{" +
            "\"ok\": true," +
            "\"result\": [" +
            "{" +
            "\"update_id\": 311105829," +
            "\"message\": {" +
            "\"message_id\": 87074," +
            "\"from\": {" +
            "\"id\": 14289485," +
            "\"is_bot\": false," +
            "\"first_name\": \"Filippe\"," +
            "\"last_name\": \"Spolti\"," +
            "\"username\": \"fspolti\"," +
            "\"language_code\": \"pt-br\"" +
            "}," +
            "\"chat\": {" +
            "\"id\": -234234212," +
            "\"title\": \"ReBot\"," +
            "\"type\": \"group\"," +
            "\"all_members_are_administrators\": true" +
            "}," +
            "\"date\": 1513173793," +
            "\"left_chat_participant\": {" +
            "\"id\": 199267392," +
            "\"is_bot\": false," +
            "\"first_name\": \"Mario\"," +
            "\"last_name\": \"Oliveira\"," +
            "\"username\": \"moliveira\"" +
            "}," +
            "\"left_chat_member\": {" +
            "\"id\": 199267392," +
            "\"is_bot\": false," +
            "\"first_name\": \"Mario\"," +
            "\"last_name\": \"Oliveira\"," +
            "\"username\": \"moliveira\"" +
            "}}}]}";

    @Test
    public void testWelcomeMessage() throws IOException {
        WelcomeMessagePlugin welcome = new WelcomeMessagePlugin();
        Assert.assertEquals(String.format(I18nHelper.resource("Welcome", "en", "welcome"),
                "Mario", "ReBot", Emoji.SMILING_FACE_WITH_OPEN_MOUTH), welcome.process(processUpdates(newMember).getResult().get(0)));
    }

    @Test
    public void testGoodbyeMessage() throws IOException{
        WelcomeMessagePlugin left = new WelcomeMessagePlugin();
        Assert.assertEquals(String.format(I18nHelper.resource("Welcome", "pt-br", "traitor"),
                "Mario", Emoji.ANGRY_FACE), left.process(processUpdates(leftMember).getResult().get(0)));
    }

    //private String getMessage(String locale, String target) {
        //return I18nHelper.resource("Welcome", locale, target);
//        Field field = WelcomeMessagePlugin.class.getDeclaredField(fieldName);
//        field.setAccessible(true);
//        return field.get(target).toString();
    //}

    private TelegramResponse<ArrayList<MessageUpdate>> processUpdates(String update) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(update, new TypeReference<TelegramResponse<ArrayList<MessageUpdate>>>() {
        });
    }
}