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

package it.rebase.rebot.telegram.api.chat.admin;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import it.rebase.rebot.api.object.ChatAdministrator;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.telegram.api.filter.ReBotPredicate;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@ApplicationScoped
public class ChatAdministratorsImpl implements ChatAdministrators {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_CHAT_ADMINISTRATORS_ENDPOINT = "https://api.telegram.org/bot%s/getChatAdministrators";

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Override
    public boolean isAdministrator(MessageUpdate messageUpdate) {
        Predicate chat = ReBotPredicate.isPrivateChat();

        if (chat.test(messageUpdate)) {
            return true;
        } else {
            String user2test = null != messageUpdate.getMessage().getFrom().getUsername() ?
                    messageUpdate.getMessage().getFrom().getUsername() :
                    messageUpdate.getMessage().getFrom().getFirstName();

            Optional<ChatAdministrator> user = getChatAdministrators(messageUpdate.getMessage().getChat().getId())
                    .stream().filter(ReBotPredicate.isUserAdmin(user2test)).findFirst();

            if (user.isPresent()) {
                log.fine("User " + user.get().getUser() + " is admin.");
                return true;
            } else {
                log.fine("User " + messageUpdate.getMessage().getFrom().getUsername() + " is not admin.");
                return false;
            }
        }
    }

    /**
     * Retrieves all Administrators for the given chatId.
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
