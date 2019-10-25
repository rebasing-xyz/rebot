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

package it.rebase.rebot.api.user.management;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@ApplicationScoped
public class UserManagementImpl implements UserManagement {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_KICKMEMBER_ENDPOINT = "https://api.telegram.org/bot%s/kickChatMember";
    private final String TELEGRAM_UNBANMEMBER_ENDPOINT = "https://api.telegram.org/bot%s/unbanChatMember";

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Override
    public void kickUser(long userId, long chatId) {
        doKickUser(userId, chatId, 0L);

        doUnbanUser(userId, chatId, 1800L);
    }

    @Override
    public void kickUser(long userId, long chatId, long waitBeforeStart) {
        doKickUser(userId, chatId, waitBeforeStart);
        doUnbanUser(userId, chatId, 1800L);
    }

    @Override
    public void unbanUser(long userId, long chatId) {
        doUnbanUser(userId, chatId, 0L);
    }

    @Override
    public void unbanUser(long userId, long chatId, long waitBeforeBan) {
        doUnbanUser(userId, chatId, waitBeforeBan);
    }

    /**
     * Unban the user from the given group, if a delay is specified, the user will be unbanned
     * after the seconds specified is reached.
     * @param userId
     * @param chatId
     * @param waitBeforeStart
     */
    private void doKickUser(long userId, long chatId, long waitBeforeStart) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_UNBANMEMBER_ENDPOINT, botTokenId);

                try (CloseableHttpResponse response = httpClient.get().execute(httpPost(url, chatId, userId))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API (unbanChatMember), status code is " + response.getStatusLine().getStatusCode());
                        log.warning("Response is " + responseContent);
                    } else {
                        log.fine("User unbanned from group " + responseContent);
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Error " + e.getMessage());
            }
        };
        scheduler.schedule(task, waitBeforeStart, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private void doUnbanUser (long userId, long chatId, long waitBeforeBan) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_KICKMEMBER_ENDPOINT, botTokenId);

                try (CloseableHttpResponse response = httpClient.get().execute(httpPost(url, chatId, userId))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API (kickChatMember), status code is " + response.getStatusLine().getStatusCode());
                        log.warning("Response is " + responseContent);
                    } else {
                        log.fine("User removed from group " + responseContent);
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Error " + e.getMessage());
            }
        };
        scheduler.schedule(task, waitBeforeBan, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    /**
     * build the http post request
     * @param url
     * @param chatId
     * @param userId
     * @return a ready to consume http post payload.
     */
    private HttpPost httpPost(String url, long chatId, long userId) {
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("chat_id", chatId + ""));
            params.add(new BasicNameValuePair("user_id", userId + ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            return httpPost;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
