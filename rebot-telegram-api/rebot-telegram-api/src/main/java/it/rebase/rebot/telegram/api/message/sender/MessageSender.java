/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.telegram.api.message.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.telegram.api.httpclient.BotCloseableHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class MessageSender implements Sender {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_API_SENDER_ENDPOINT = "https://api.telegram.org/bot%s/sendMessage";

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Override
    public void processOutgoingMessage(Message message) {
        try {
            if (message.getText().length() > 1 && !message.getText().equals(null)) {
                log.fine("Sending message: [" + message.getText() + "]");
                send(message);
            }
        } catch (final Exception e) {
            log.finest("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Prepare the request that sends a message to the Telegra API
     * Parameters
     *  - chat_id = chat or group
     *  - parse_mode = default HTML
     *  - reply_to_message_id = Id of a sent message, if present the message will be sent to its original sender
     *  - disable_web_page_preview = disables the link preview
     * @param message Message to be sent
     */
    private void send(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String url = String.format(TELEGRAM_API_SENDER_ENDPOINT, botTokenId);
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
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Something goes wrong " + e.getMessage());
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Something goes wrong " + e.getMessage());
        }
    }
}