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

package it.rebase.rebot.plugin.notifier;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.object.Chat;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.service.cache.qualifier.DefaultCache;
import it.rebase.rebot.plugin.pojo.PacktBook;
import it.rebase.rebot.service.persistence.pojo.PacktNotification;
import it.rebase.rebot.service.persistence.repository.PacktRepository;
import it.rebase.rebot.telegram.api.message.sender.MessageSender;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.infinispan.Cache;

import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.logging.Logger;

@javax.ejb.Singleton
public class PacktNotifier {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final String FREE_LEARNING_URL = "https://www.packtpub.com/packt/offers/free-learning";
    private static final String PACKT_HOME_URL = "https://www.packtpub.com%s";

    @Inject
    @DefaultCache
    Cache<String, PacktBook> cache;

    @Inject
    private MessageSender messageSender;

    @Inject
    private PacktRepository repository;

    @Inject
    PacktBook packtBook;

    @Resource
    TimerService timerService;

    @Inject
    @BotProperty(name = "it.rebase.rebot.scheduler.timezone")
    String timezone;

    public String get() {
        StringBuilder builder = new StringBuilder("<i> Packt Free Learning - daily book</i>\n");
        builder.append("<b>Book name:</b> <code>" + packtBook.getBookName() + "</code>\n");
        builder.append("<b>Claim URL:</b> ");
        builder.append(packtBook.getClaimUrl());
        return builder.toString();
    }

    synchronized public void startTimer() {
        ScheduleExpression schedule = new ScheduleExpression();
        if (null == timezone) {
            log.warning("Timezone not set, using default: America/Sao_Paulo");
            schedule.timezone("America/Sao_Paulo");
        } else {
            schedule.timezone(timezone);
        }
        schedule.hour("23");
        schedule.minute("00");
        timerService.createCalendarTimer(schedule);
    }

    @Timeout
    public void scheduler(Timer timer) {
        populate(true);
        log.fine("Timer executed, next timeout [" + timer.getNextTimeout() + "]");
    }

    synchronized public void populate(boolean notify) {
        log.fine("Retrieving information about the daily free ebook.");
        try {
            HttpGet request = new HttpGet(FREE_LEARNING_URL);
            request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 Firefox/26.0");
            HttpResponse response = client().execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String inputLine, previousLine = "";
            int lines = 0;
            while ((inputLine = reader.readLine()) != null) {
                lines += 1;
                // get the book name
                if (previousLine.contains("dotd-main-book-image")) {
                    inputLine = reader.readLine();
                    packtBook.setBookName(inputLine.split("/")[2].replace("\">", ""));
                }

                // get the claim URI
                if (inputLine.contains("/freelearning-claim/")) {
                    packtBook.setClaimUrl(String.format(PACKT_HOME_URL, inputLine.trim().replace("\"", "").replace(" id", "").split("=")[1]));
                }
                previousLine = inputLine;
            }

            if (null == packtBook.getClaimUrl()) {
                packtBook.setClaimUrl(FREE_LEARNING_URL);
            }

            log.fine(packtBook.toString());
            if (cache.containsKey("book")) {
                cache.replace("book", packtBook);
            } else {
                cache.put("book", packtBook);
            }

            if (notify) {
                repository.get().stream().forEach(chatId -> this.notify(chatId));
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Failed to obtain the ebook information: " + e.getMessage());
        }
    }

    public String registerNotification(MessageUpdate message) {
        String channel = null;
        if (message.getMessage().getChat().getType().equals("group") || message.getMessage().getChat().getType().equals("supergroup")) {
            channel = message.getMessage().getChat().getTitle();
        } else {
            channel = message.getMessage().getFrom().getFirstName();
        }
        return repository.register(new PacktNotification(message.getMessage().getChat().getId(), channel));
    }

    public String unregisterNotification(MessageUpdate message) {
        String channel = null;
        if (message.getMessage().getChat().getType().equals("group") || message.getMessage().getChat().getType().equals("supergroup")) {
            channel = message.getMessage().getChat().getTitle();
        } else {
            channel = message.getMessage().getFrom().getFirstName();
        }
        return repository.unregister(new PacktNotification(message.getMessage().getChat().getId(), channel));
    }

    private void notify(BigInteger chatId) {
        Chat chat = new Chat();
        chat.setId(chatId.longValue());
        Message message = new Message();
        message.setChat(chat);
        message.setText(this.get());
        messageSender.processOutgoingMessage(message);
    }

    private CloseableHttpClient client() {
        return HttpClients.createDefault();
    }

}
