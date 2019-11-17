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

import io.quarkus.scheduler.Scheduled;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.Chat;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.plugin.pojo.DailyOffer;
import it.rebase.rebot.plugin.pojo.LoadDailyOffer;
import it.rebase.rebot.service.cache.qualifier.DefaultCache;
import it.rebase.rebot.service.persistence.pojo.PacktNotification;
import it.rebase.rebot.service.persistence.repository.PacktRepository;
import it.rebase.rebot.api.message.sender.MessageSender;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

@ApplicationScoped
public class PacktNotifier {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final String FREE_LEARNING_URL = "https://www.packtpub.com/packt/offers/free-learning";
    private static final String PACKT_LOAD_OFFER_ENDPOINT = "https://services.packtpub.com/free-learning-v1/offers?dateFrom=%sT00:00:00.000Z&dateTo=%sT00:00:00.000Z";
    private static final String PACKT_DAILY_OFFER_ENDPOINT = "https://static.packt-cdn.com/products/%s/summary";

    @Inject
    @DefaultCache
    Cache<String, Object> cache;

    @Inject
    private MessageSender messageSender;

    @Inject
    private PacktRepository repository;

    public String get(String locale) {
        DailyOffer dailyOffer = (DailyOffer) cache.get("book");
        String pages = null != dailyOffer.getPages() ? dailyOffer.getPages().toString() : "N/A";
        return String.format(
                I18nHelper.resource("Packt", locale, "book"),
                dailyOffer.getTitle(),
                dailyOffer.getOneLiner(),
                pages,
                FREE_LEARNING_URL);
    }

    @Scheduled(cron = "0 30 05 * * ?")
    public void populate() {
        log.fine("Retrieving information about the daily free ebook.");
        try {
            Date today = new Date();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Client client = ClientBuilder.newClient();
            WebTarget loadDailyOfferWebTarget = client.target(String.format(PACKT_LOAD_OFFER_ENDPOINT, localDate.format(formatter),
                    localDate.plus(1, ChronoUnit.DAYS).format(formatter)));

            log.fine("Loading daily pack offer with URI: " + loadDailyOfferWebTarget.getUri());

            Response loadDailyOfferResponse = loadDailyOfferWebTarget.request().get();
            if (loadDailyOfferResponse.getStatus() != 200) {
                log.warning("Failed to connect in the endpoint " + loadDailyOfferWebTarget.getUri() + ", status code is: " + loadDailyOfferResponse.getStatus());
            }

            LoadDailyOffer loadDailyOffer = loadDailyOfferResponse.readEntity(LoadDailyOffer.class);
            log.fine(loadDailyOffer.toString());

            WebTarget getDailyOffer = client.target(String.format(PACKT_DAILY_OFFER_ENDPOINT, loadDailyOffer.getData().get(0).getProductId()));
            log.finest("Getting daily pack offer with URI: " + getDailyOffer.getUri());

            Response getDailyOfferResponse = getDailyOffer.request().get();
            if (getDailyOfferResponse.getStatus() != 200) {
                log.warning("Failed to connect in the endpoint " + getDailyOffer.getUri() + ", status code is: " + getDailyOfferResponse.getStatus());
            }

            DailyOffer dailyOffer = getDailyOfferResponse.readEntity(DailyOffer.class);
            log.fine(dailyOffer.toString());

            cache.clear();
            cache.put("book", dailyOffer);

            repository.get().stream().forEach(packtNotification ->
                    this.notify(packtNotification.getChatId(),
                        packtNotification.getLocale())
            );

        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Failed to obtain the ebook information: " + e.getMessage());
        }
    }

    public String registerNotification(MessageUpdate message) {
        String channel;
        if (message.getMessage().getChat().getType().equals("group") || message.getMessage().getChat().getType().equals("supergroup")) {
            channel = message.getMessage().getChat().getTitle();
        } else {
            channel = message.getMessage().getFrom().getFirstName();
        }
        return repository.register(new PacktNotification(message.getMessage().getChat().getId(),
                channel,
                message.getMessage().getFrom().getLanguageCode()));
    }

    public String unregisterNotification(MessageUpdate message) {
        String channel;
        if (message.getMessage().getChat().getType().equals("group") || message.getMessage().getChat().getType().equals("supergroup")) {
            channel = message.getMessage().getChat().getTitle();
        } else {
            channel = message.getMessage().getFrom().getFirstName();
        }
        return repository.unregister(new PacktNotification(message.getMessage().getChat().getId(),
                channel,
                message.getMessage().getFrom().getLanguageCode()));
    }

    private void notify(Long chatId, String locale) {
        Chat chat = new Chat();
        chat.setId(chatId.longValue());
        Message message = new Message();
        message.setChat(chat);
        message.setText(this.get(locale));
        messageSender.processOutgoingMessage(message);
    }
}