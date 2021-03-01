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


package it.rebase.rebot.telegram.api;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.BotConfig;
import it.rebase.rebot.api.httpclient.BotCloseableHttpClient;
import it.rebase.rebot.api.object.GetUpdatesConfProducer;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.object.TelegramResponse;
import it.rebase.rebot.service.persistence.pojo.BotStatus;
import it.rebase.rebot.service.persistence.repository.ApiRepository;
import it.rebase.rebot.telegram.api.polling.ReBotLongPoolingBot;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

@ApplicationScoped
public class UpdatesReceiver implements Runnable {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String TELEGRAM_UPDATE_ENDPOINT = "https://api.telegram.org/bot%s/getUpdates";

    @Inject
    BotConfig config;

    @Inject
    private BotCloseableHttpClient httpClient;

    @Inject
    private ApiRepository apiRepository;

    private Long lastUpdateId = 0L;

    private volatile boolean running = false;

    private RequestConfig requestConfig;

    private Thread currentThread;

    @Inject
    ReBotLongPoolingBot callback;

    /**
     * Method responsible to configure the HttpClient and start the receiver by calling the method <b>run</b>
     * Persists the Bot status on the database
     */
    public synchronized void start() {
        requestConfig = RequestConfig.copy(RequestConfig.custom().build())
                .setSocketTimeout(75 * 1000)
                .setConnectTimeout(75 * 1000)
                .setConnectionRequestTimeout(75 * 1000).build();
        running = true;
        currentThread = new Thread(this);
        currentThread.setDaemon(true);
        currentThread.setName("Telegram-" + config.botUserId());
        currentThread.start();
    }

    /**
     * When called interrupt the current thread.
     */
    public void interrupt() {
        if (running) {
            running = false;
            currentThread.interrupt();
        }
    }

    /**
     * @return the bot status, true for enabled or false for disabled
     * When the bot is starting the receiver it will persist its state to survive restarts.
     */
    public boolean isEnabled(long chatId) {
        return apiRepository.isBotEnabled(chatId);
    }

    /**
     * Disables the bot, it does not stop the updatesReceiver thread, because if it is done, then there is no way
     * to re-enable toe bot through telegram chat, it will be persisted in the persistence layer
     *
     * @param message {@link Message}
     */
    public void disable(Message message) {
        log.info("Disabling bot, requested by " + message.getFrom().toString());
        apiRepository.persist(new BotStatus(false, message.getFrom(),
                                            message.getChat().getId()));
    }

    /**
     * @param message {@link Message}
     */
    public void enable(Message message) {
        log.info("Enabling bot, requested by " + message.getFrom().toString());
        apiRepository.remove(message.getChat().getId());
    }

    /**
     * Starts the receiver, every message sent will be received by all classes that implements {@link ReBotLongPoolingBot} will receive the updates
     * through the in through the call <b>callback.onUpdateReceived(u);</b> on this method.
     * The receiver configuration is done by the class {@link GetUpdatesConfProducer}
     * <p>
     * This thread remains in execution until the bot goes down.
     * The interval between the updates verification is <b>600ms</b>
     */
    @Override
    public synchronized void run() {
        ObjectMapper objectMapper = new ObjectMapper();
        while (running) {
            GetUpdatesConfProducer getUpdates = new GetUpdatesConfProducer().setLimit(100).setTimeout(10 * 1000).setOffset(lastUpdateId + 1);
            log.finest("receiver config -> " + getUpdates.toString());
            try {
                String url = String.format(TELEGRAM_UPDATE_ENDPOINT, config.botTokenId());

                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
                httpPost.setConfig(requestConfig);
                httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(getUpdates), ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.get().execute(httpPost)) {
                    HttpEntity responseEntity = response.getEntity();
                    BufferedHttpEntity buf = new BufferedHttpEntity(responseEntity);
                    String responseContent = EntityUtils.toString(buf, StandardCharsets.UTF_8);

                    if (response.getStatusLine().getStatusCode() != 200) {
                        log.warning("Error received from Telegram API, status code is " + response.getStatusLine().getStatusCode());
                        synchronized (this) {
                            this.wait(600);
                        }
                    }

                    TelegramResponse<ArrayList<MessageUpdate>> updates = objectMapper.readValue(responseContent,
                                                                                                new TypeReference<TelegramResponse<ArrayList<MessageUpdate>>>() {
                                                                                                });

                    if (null == updates.getResult()) {
                        this.wait(1000);
                    }

                    updates.getResult().removeIf(n -> n.getUpdateId() < lastUpdateId);
                    lastUpdateId = updates.getResult().parallelStream().map(MessageUpdate::getUpdateId).max(Long::compareTo).orElse(0L);
                    updates.getResult().stream()
                            .forEach(u -> {
                                // make sure that even edited messages will be intercepted by the rebot.
                                if (null != u.getEditedMessage()) {
                                    log.finest("is updated message? " + true);
                                    Message msg = new Message();
                                    msg.setChat(u.getEditedMessage().getChat());
                                    msg.setDate(u.getEditedMessage().getDate());
                                    msg.setEntities(u.getEditedMessage().getEntities());
                                    msg.setFrom(u.getEditedMessage().getFrom());
                                    msg.setMessageId(u.getEditedMessage().getMessageId());
                                    msg.setText(u.getEditedMessage().getText());
                                    u.setEdited(true);
                                    u.setMessage(msg);
                                } else {
                                    u.setEdited(false);
                                }
                                // notify the implementations of ReBotLongPoolingBot about the received messages.
                                log.finest("Message is [ " + u.toString() + "]");
                                callback.onUpdateReceived(u);
                            });
                    // wait 600ms before check for updates
                    this.wait(600);
                }
            } catch (final Exception e) {
                e.printStackTrace();
                log.warning("Error " + e.getMessage());
            }
        }
    }
}