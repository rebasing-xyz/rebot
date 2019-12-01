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

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.plugin.welcome.WelcomeMessagePlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WelcomeMessageTest {

    @Inject
    WelcomeMessagePlugin welcome;

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
            "\"language_code\": \"en-us\"" +
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

    private String messageUpdate = "{\"ok\":true,\"result\":[{\"update_id\":542669457," +
            "\"message\":{\"message_id\":56, " +
            "\"from\":{\"id\":14289485,\"is_bot\":false," +
            "\"first_name\":\"Mario\",\"last_name\":\"Oliveira\"," +
            "\"username\":\"moliveira\",\"language_code\":\"pt-br\"}," +
            "\"chat\":{\"id\":-1001336143392,\"title\":\"ReBot\"," +
            "\"type\":\"supergroup\"},\"date\":1573225372,\"text\":\"58\"}}]}";

    @BeforeAll
    public static void prepre() {
        System.setProperty("it.rebase.rebot.telegram.token", "faketoken");
        System.setProperty("it.rebase.rebot.telegram.userId", "fakeBotID");
    }

    @Test
    public void testWelcomeMessage() throws IOException {
        welcome.process(processUpdates(newMember).getResult().get(0), "en_US");
        String userResponse = welcome.process(processUpdates(messageUpdate).getResult().get(0), "en_US");
        System.out.println("---> " + userResponse);
        Assertions.assertTrue(userResponse.contains("Sorry <b>moliveira</b>, the challenge answer"));
    }

    @Test
    public void testGoodbyeMessage() throws IOException {
        Assertions.assertEquals(String.format(I18nHelper.resource("Welcome", "pt_BR", "traitor"),
                                              "Mario", Emoji.ANGRY_FACE), welcome.process(processUpdates(leftMember).getResult().get(0), "pt_BR"));
    }

    private TelegramResponse<ArrayList<MessageUpdate>> processUpdates(String update) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(update, new TypeReference<TelegramResponse<ArrayList<MessageUpdate>>>() {
        });
    }
}