/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2017 Rebasing.xyz ReBot
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

package xyz.rebasing.rebot.api.management.message;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.httpclient.BotCloseableHttpClient;

@ApplicationScoped
public class MessageManagementImpl implements MessageManagement {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_DELETE_MESSAGE_ENDPOINT = "https://api.telegram.org/bot%s/deleteMessage";

    @Inject
    BotConfig config;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Override
    public void deleteMessage(long chatId, long messageId, long timeout) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_DELETE_MESSAGE_ENDPOINT, config.botTokenId());
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("chat_id", chatId + ""));
                params.add(new BasicNameValuePair("message_id", messageId + ""));

                log.debugv("Performing http post request against {0} with parameters {1}", url, params.toString());

                try (CloseableHttpResponse response = httpClient.get().execute(httpClient.httpPost(url, params))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warnv("Error received from Telegram API (deleteMessage), status code is {0}",
                                  response.getStatusLine().getStatusCode());
                        log.warnv("Response is {0}", responseContent);
                    } else {
                        log.debug("Message deleted.");
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                log.warnv("Error {0}", e.getMessage());
            }
        };
        scheduler.schedule(task, timeout, TimeUnit.SECONDS);
        scheduler.shutdown();
    }
}
