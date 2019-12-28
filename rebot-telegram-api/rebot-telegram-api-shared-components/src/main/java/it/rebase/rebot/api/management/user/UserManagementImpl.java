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

package it.rebase.rebot.api.management.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import it.rebase.rebot.api.object.ChatAdministrator;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.api.object.User;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static it.rebase.rebot.api.shared.filter.RebotSharedFilter.isPrivateChat;
import static it.rebase.rebot.api.shared.filter.RebotSharedFilter.isUserAdmin;

@ApplicationScoped
public class UserManagementImpl implements UserManagement {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_CHAT_ADMINISTRATORS_ENDPOINT = "https://api.telegram.org/bot%s/getChatAdministrators";
    private final String TELEGRAM_KICKMEMBER_ENDPOINT = "https://api.telegram.org/bot%s/kickChatMember";
    private final String TELEGRAM_UNBANMEMBER_ENDPOINT = "https://api.telegram.org/bot%s/unbanChatMember";
    private final String TELEGRAM_GET_ME_ENDPOINT = "https://api.telegram.org/bot%s/getMe";

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

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

    @Override
    public User getMe() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            HttpGet httpGet = new HttpGet(String.format(TELEGRAM_GET_ME_ENDPOINT, botTokenId));
            try (CloseableHttpResponse response = httpClient.get().execute(httpGet)) {
                HttpEntity responseEntity = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                if (response.getStatusLine().getStatusCode() != 200) {
                    log.warning("Error received from Telegram API (getMe), status code is " + response.getStatusLine().getStatusCode());
                    log.warning("Response is " + responseContent);
                }

                TelegramResponse<User> me = objectMapper.readValue(responseContent, new TypeReference<TelegramResponse<User>>() {
                });
                log.fine("WhoAmi: " + me.toString());
                return me.getResult();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Error " + e.getMessage());
            return null;
        }
    }

    /**
     * Unban the user from the given group, if a delay is specified, the user will be unbanned
     * after the seconds specified is reached.
     *
     * @param userId
     * @param chatId
     * @param waitBeforeStart
     */
    private void doKickUser(long userId, long chatId, long waitBeforeStart) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_KICKMEMBER_ENDPOINT, botTokenId);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("chat_id", chatId + ""));
                params.add(new BasicNameValuePair("user_id", userId + ""));

                try (CloseableHttpResponse response = httpClient.get().execute(httpClient.httpPost(url, params))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API (doKickUser), status code is " + response.getStatusLine().getStatusCode());
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
        scheduler.schedule(task, waitBeforeStart, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private void doUnbanUser(long userId, long chatId, long waitBeforeBan) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                String url = String.format(TELEGRAM_UNBANMEMBER_ENDPOINT, botTokenId);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("chat_id", chatId + ""));
                params.add(new BasicNameValuePair("user_id", userId + ""));

                try (CloseableHttpResponse response = httpClient.get().execute(httpClient.httpPost(url, params))) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API (doUnbanUser), status code is " + response.getStatusLine().getStatusCode());
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
        scheduler.schedule(task, waitBeforeBan, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    @Override
    public boolean isBotAdministrator(MessageUpdate messageUpdate) {
        // override userId and username as Telegram does not expose a endpoint to verify if
        // the bot is or not a group admin.
        User bot = this.getMe();
        MessageUpdate localMessage = messageUpdate;
        localMessage.getMessage().getFrom().setId(bot.getId());
        localMessage.getMessage().getFrom().setUsername(bot.getUsername());
        return isAdministrator(localMessage);
    }

    @Override
    public boolean isAdministrator(MessageUpdate messageUpdate) {
        Predicate chat = isPrivateChat();

        if (chat.test(messageUpdate)) {
            return true;
        } else {
            long user2test = messageUpdate.getMessage().getFrom().getId();
            String username = messageUpdate.getMessage().getFrom().getUsername() != null ?
                    messageUpdate.getMessage().getFrom().getUsername() : messageUpdate.getMessage().getFrom().getFirstName();

            Optional<ChatAdministrator> user = getChatAdministrators(messageUpdate.getMessage().getChat().getId())
                    .stream().filter(isUserAdmin(user2test)).findFirst();

            if (user.isPresent()) {
                log.fine("User " + user.get().getUser() + " is admin.");
                return true;
            } else {
                log.fine("User " + username + " is not admin.");
                return false;
            }
        }
    }

    /**
     * Retrieves all Administrators for the given chatId.
     *
     * @param chatId
     * @return List {@Link ChatAdministrator}
     */
    private List<ChatAdministrator> getChatAdministrators(long chatId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String url = String.format(TELEGRAM_CHAT_ADMINISTRATORS_ENDPOINT, botTokenId);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("chat_id", chatId + ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.get().execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                if (response.getStatusLine().getStatusCode() != 200) {
                    log.warning("Error received from Telegram API (getChatAdministrators), status code is " + response.getStatusLine().getStatusCode());
                }

                TelegramResponse<ArrayList<ChatAdministrator>> chatAdministrators = objectMapper.readValue(responseContent,
                        new TypeReference<TelegramResponse<ArrayList<ChatAdministrator>>>() {
                        });

                log.fine(chatAdministrators.toString());
                return chatAdministrators.getResult();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Error " + e.getMessage());
            return new ArrayList<>();
        }
    }


}
