/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package it.rebase.rebot.api.management.message;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@ApplicationScoped
public class MessageManagementImpl implements MessageManagement {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_DELETE_MESSAGE_ENDPOINT = "https://api.telegram.org/bot%s/deleteMessage";

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Override
    public void deleteMessage(long chatId, long messageId, long timeout) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_DELETE_MESSAGE_ENDPOINT, botTokenId);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("chat_id", chatId + ""));
                params.add(new BasicNameValuePair("message_id", messageId + ""));

                log.fine("Performing http post request against " + url + " with parameters " +params.toString());

                try (CloseableHttpResponse response = httpClient.get().execute(httpClient.httpPost(url, params))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API (deleteMessage), status code is " + response.getStatusLine().getStatusCode());
                        log.warning("Response is " + responseContent);
                    } else {
                        log.fine("Message deleted.");
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Error " + e.getMessage());
            }
        };
        scheduler.schedule(task, timeout, TimeUnit.SECONDS);
        scheduler.shutdown();
    }
}
