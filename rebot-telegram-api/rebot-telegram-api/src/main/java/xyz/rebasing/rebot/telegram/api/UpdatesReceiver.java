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

package xyz.rebasing.rebot.telegram.api;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import xyz.rebasing.rebot.api.domain.GetUpdatesConfProducer;
import xyz.rebasing.rebot.api.domain.Message;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.domain.TelegramResponse;
import xyz.rebasing.rebot.api.shared.components.httpclient.IRebotOkHttpClient;
import xyz.rebasing.rebot.service.persistence.domain.BotStatus;
import xyz.rebasing.rebot.service.persistence.repository.ApiRepository;
import xyz.rebasing.rebot.telegram.api.polling.ReBotLongPoolingBot;

@ApplicationScoped
public class UpdatesReceiver implements Runnable {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    ApiRepository apiRepository;

    @Inject
    IRebotOkHttpClient okclient;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    ReBotLongPoolingBot callback;

    private Long lastUpdateId = 0L;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Method responsible to configure the HttpClient and start the receiver by calling the method <b>run</b>
     * Persists the Bot status on the database
     */
    @SuppressWarnings("FutureReturnValueIgnored")
    public void start() {
        executorService.scheduleAtFixedRate(this, 0, 800, TimeUnit.MILLISECONDS);
    }

    /**
     * When called interrupt the current thread.
     */
    public void interrupt() {
        executorService.shutdown();
    }

    /**
     * When the bot is starting the receiver it will persist its state to survive restarts.
     *
     * @param chatId where the api will verify if the bot is or not enabled
     * @return the bot status, true for enabled or false for disabled
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
     * The interval between the updates' verification is <b>800ms</b>
     */
    @Override
    public synchronized void run() {

        GetUpdatesConfProducer getUpdates = new GetUpdatesConfProducer().setLimit(100).setTimeout(10 * 1000).setOffset(lastUpdateId + 1);
        log.tracev("receiver config -> {0}", getUpdates.toString());

        Request request = null;
        try {
            request = new Request.Builder()
                    .url(String.format("https://api.telegram.org/bot%s/getUpdates", config.botTokenId()))
                    .addHeader("charset", StandardCharsets.UTF_8.name())
                    .post(RequestBody.create(objectMapper.writeValueAsString(getUpdates),
                                             okclient.mediaTypeJson()))
                    .build();
        } catch (JsonProcessingException e) {
            log.errorv("failed to write json as string: {0}", e);
        }

        try (Response response = okclient.get().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warnv("Error received from Telegram API, status code is {0}", response.code());
            }
            TelegramResponse<ArrayList<MessageUpdate>> updates = objectMapper.
                    readValue(response.body().string(), new TypeReference<>() {
                    });

            updates.getResult().removeIf(n -> n.getUpdateId() < lastUpdateId);
            lastUpdateId = updates.getResult().parallelStream().map(MessageUpdate::getUpdateId).max(Long::compareTo).orElse(0L);
            updates.getResult()
                    .forEach(u -> {
                        // make sure that even edited messages will be intercepted.
                        if (null != u.getEditedMessage()) {
                            log.trace("is updated message? true");
                            Message msg = new Message(u.getEditedMessage().getMessageId(),
                                                      u.getEditedMessage().getChat(),
                                                      u.getEditedMessage().getText());
                            msg.setDate(u.getEditedMessage().getDate());
                            msg.setEntities(u.getEditedMessage().getEntities());
                            msg.setFrom(u.getEditedMessage().getFrom());
                            u.setEdited(true);
                            u.setMessage(msg);
                        } else {
                            u.setEdited(false);
                        }
                        log.tracev("Message is [{0}]", u.toString());
                        // notify the implementations of ReBotLongPoolingBot about the received messages.
                        callback.onUpdateReceived(u);
                    });
        } catch (final Exception e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            log.warnv("Error {0}", e.getMessage());
        }
    }
}
