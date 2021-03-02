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


package it.rebase.rebot.api.message.sender;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.BotConfig;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import it.rebase.rebot.api.management.message.MessageManagement;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.TelegramResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

@ApplicationScoped
public class MessageSender implements Sender {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_API_SENDER_ENDPOINT = "https://api.telegram.org/bot%s/sendMessage";
    private final int TELEGRAM_MESSAGE_CHARACTERS_LIMIT = 4000;

    @Inject
    BotConfig config;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Inject
    private MessageManagement messageManagement;

    // TODO do not return nothing here, messages will be delete in this method.
    @Override
    public OptionalLong processOutgoingMessage(Message message, boolean deleteMessage, long timeout) {
        BufferedReader reader = new BufferedReader(new StringReader(message.getText()));
        StringBuilder temporaryMessage = new StringBuilder();
        OptionalLong sentMessageID = OptionalLong.empty();
        try {
            if (message.getText().length() > 1 && !message.getText().equals(null)) {
                if (message.getText().length() > TELEGRAM_MESSAGE_CHARACTERS_LIMIT) {
                    reader.lines().forEach(line -> {
                        if (temporaryMessage.toString().length() < TELEGRAM_MESSAGE_CHARACTERS_LIMIT) {
                            temporaryMessage.append(line + "\n");
                        } else {
                            temporaryMessage.append(line + "\n");
                            log.fine("Message exceeded " + TELEGRAM_MESSAGE_CHARACTERS_LIMIT + " ending partial message.");
                            message.setText(temporaryMessage.toString());
                            send(message);
                            temporaryMessage.setLength(0);
                        }
                    });

                    log.fine("Sending next part of message.");
                    message.setText(temporaryMessage.toString());
                    sentMessageID = send(message);
                    temporaryMessage.setLength(0);
                } else {
                    log.fine("Sending message: [" + message.getText() + "]");
                    sentMessageID = send(message);
                }
            }
        } catch (final Exception e) {
            log.warning("Failed to send message: " + e.getMessage());
            return OptionalLong.of(0);
        }

        if (deleteMessage) {
            messageManagement.deleteMessage(message.getChat().getId(), sentMessageID.getAsLong(), timeout);
        }

        return sentMessageID;
    }

    /**
     * Prepare the request that sends a message to the Telegram API
     * Parameters
     * - chat_id = chat or group
     * - parse_mode = default HTML
     * - reply_to_message_id = Id of a sent message, if present the message will be sent to its original sender
     * - disable_web_page_preview = disables the link preview
     *
     * @param message Message to be sent
     */
    private OptionalLong send(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = String.format(TELEGRAM_API_SENDER_ENDPOINT, config.botTokenId());
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
            httpPost.addHeader("content-type", "application/json");
            Map<String, String> body = new HashMap<>();
            body.put("chat_id", message.getChat().getId() + "");
            body.put("parse_mode", "HTML");
            body.put("reply_to_message_id", message.getMessageId() + "");
            body.put("text", message.getText());
            body.put("disable_web_page_preview", "true");
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = httpClient.get().execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);
                log.fine("Telegram API response: [" + responseContent + "]");

                TelegramResponse<Message> telegramResponse = objectMapper.readValue(responseContent,
                                                                                    new TypeReference<TelegramResponse<Message>>() {
                                                                                    });
                if (telegramResponse.hasError() && telegramResponse.getErrorCode() == 404) {
                    log.warning("Failed to send message: " + telegramResponse.getErrorDescription());
                    return OptionalLong.of(0);
                }

                return OptionalLong.of(telegramResponse.getResult().getMessageId());
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Something goes wrong " + e.getMessage());
                return OptionalLong.of(0);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Something goes wrong " + e.getMessage());
            return OptionalLong.of(0);
        }
    }
}