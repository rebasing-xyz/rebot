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

package xyz.rebasing.rebot.api.shared.components.message.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.Message;
import xyz.rebasing.rebot.api.domain.TelegramResponse;
import xyz.rebasing.rebot.api.shared.components.httpclient.IRebotOkHttpClient;
import xyz.rebasing.rebot.api.shared.components.management.message.MessageManagement;

@ApplicationScoped
public class OutcomeMessageProcessor implements Sender {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final int TELEGRAM_MESSAGE_CHARACTERS_LIMIT = 4000;

    @Inject
    BotConfig config;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    IRebotOkHttpClient okclient;

    @Inject
    MessageManagement messageManagement;

    // TODO do not return nothing here, messages will be delete in this method.
    @Override
    public OptionalLong processOutgoingMessage(Message message, boolean deleteMessage, long timeout) {
        BufferedReader reader = new BufferedReader(new StringReader(message.getText()));
        StringBuilder temporaryMessage = new StringBuilder();
        OptionalLong sentMessageID = OptionalLong.empty();
        try {
            if (message.getText().length() > 1 && null != message.getText()) {
                if (message.getText().length() > TELEGRAM_MESSAGE_CHARACTERS_LIMIT) {
                    reader.lines().forEach(line -> {
                        if (temporaryMessage.toString().length() < TELEGRAM_MESSAGE_CHARACTERS_LIMIT) {
                            temporaryMessage.append(line + "\n");
                        } else {
                            temporaryMessage.append(line + "\n");
                            log.debugv("Message exceeded {0} ending partial message.", TELEGRAM_MESSAGE_CHARACTERS_LIMIT);
                            message.setText(temporaryMessage.toString());
                            send(message);
                            temporaryMessage.setLength(0);
                        }
                    });

                    log.debug("Sending next part of message.");
                    message.setText(temporaryMessage.toString());
                    sentMessageID = send(message);
                    temporaryMessage.setLength(0);
                } else {
                    log.debugv("Sending message: [{0}]", message.getText());
                    sentMessageID = send(message);
                }
            }
        } catch (final Exception e) {
            log.warnv("Failed to send message: {0}", e.getMessage());
            return OptionalLong.of(0);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        Map<String, String> body = new HashMap<>();
        body.put("chat_id", message.getChat().getId() + "");
        body.put("parse_mode", "HTML");
        body.put("reply_to_message_id", message.getMessageId() + "");
        body.put("text", message.getText());
        body.put("disable_web_page_preview", "true");

        Request request = null;
        try {
            request = new Request.Builder()
                    .url(String.format("https://api.telegram.org/bot%s/sendMessage", config.botTokenId()))
                    .addHeader("charset", StandardCharsets.UTF_8.name())
                    .addHeader("content-type", "application/json")
                    .post(RequestBody.create(objectMapper.writeValueAsString(body), okclient.mediaTypeJson()))
                    .build();
        } catch (JsonProcessingException e) {
            log.errorv("failed to write json as string: {0}", e);
        }

        try (Response response = okclient.get().newCall(request).execute()) {

            TelegramResponse<Message> telegramResponse = objectMapper.
                    readValue(response.body().string(), new TypeReference<>() {
                    });

            if (telegramResponse.hasError() && telegramResponse.getErrorCode() == 404) {
                log.warnv("Failed to send message: {0}", telegramResponse.getErrorDescription());
                return OptionalLong.of(0);
            }
            log.debugv("Telegram API response: [{0}]", telegramResponse.toString());
            return OptionalLong.of(telegramResponse.getResult().getMessageId());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warnv("Something goes wrong {0}", e.getMessage());
            return OptionalLong.of(0);
        }
    }
}