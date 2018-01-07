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

package it.rebase.rebot.service.jbossbooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.object.Chat;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.service.cache.qualifier.JBossBooksCache;
import it.rebase.rebot.service.jbossbooks.pojo.Books;
import it.rebase.rebot.service.jbossbooks.pojo.JSONResponse;
import it.rebase.rebot.service.persistence.pojo.AmountOfBooks;
import it.rebase.rebot.service.persistence.pojo.BookUpdates;
import it.rebase.rebot.service.persistence.repository.JBossBooksRepository;
import it.rebase.rebot.telegram.api.message.sender.MessageSender;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.infinispan.Cache;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class JBossBooksService {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final String GIT_BOOK_ENDPOINT = "https://api.gitbook.com/books";
    private final String GIT_BOOKS_PROJECT_HOME = "https://gitbook.com/@jboss-books";

    @Inject
    @BotProperty(name = "it.rebase.rebot.gitbook.token", required = true)
    private String gitBookToken;

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.chatId", required = true)
    private String chatId;

    @Inject
    private JBossBooksRepository repository;

    @Inject
    @JBossBooksCache
    private Cache<String, JSONResponse> cache;

    @Inject
    private MessageSender messageSender;

    private Message message = new Message();

    /**
     * Scheduler default that get the available books and sabe it on cache.
     * Persists on database if necessary
     */
    @Schedule(minute = "0/20", hour = "*", persistent = false)
    public synchronized void initialize() {
        if (cache.containsKey("jsonResponse")) {
            log.fine("Cache already populated, returning.");
            return;
        }
        try {
            log.fine("Retrieving books information from [" + GIT_BOOK_ENDPOINT + "]");
            HttpGet request = new HttpGet(GIT_BOOK_ENDPOINT);
            request.setHeader("Authorization", "Bearer " + gitBookToken);
            ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            ObjectMapper mapper = new ObjectMapper();
            JSONResponse jsonResponse = mapper.readValue(client().execute(request, responseHandler), JSONResponse.class);
            cache.put("jsonResponse", jsonResponse, 60, TimeUnit.MINUTES);
            verifyBookUpdates(jsonResponse);
            verifyNewBook(jsonResponse.getTotal());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Failure retrieving information from [" + GIT_BOOK_ENDPOINT + "]: " + e.getMessage());
        }
    }

    /**
     * Returns the list of books
     *
     * @return {@link JSONResponse}
     * @throws IOException
     */
    public List<Books> getBooks() throws IOException {
        initialize();
        JSONResponse jsonResponse = cache.get("jsonResponse");
        return jsonResponse.getList();
    }

    /**
     * Verifies if a new book was added on JBoss Books
     *
     * @param amount - amount of books on JBoss Books
     */
    private void verifyNewBook(int amount) {
        log.fine("Verifying if new book was added.");
        int booksAmount = repository.getAmount();
        if (amount > booksAmount) {
            notify("A new book was added, check this out: " + GIT_BOOKS_PROJECT_HOME);
            repository.persistAmountOfBooks(new AmountOfBooks(amount));
        } else if (amount < booksAmount) {
            log.fine("Bood removed, updating database.");
            repository.persistAmountOfBooks(new AmountOfBooks(amount));
        }
    }

    /**
     * Verifies if a book was updated
     *
     * @param books {@link JSONResponse}
     */
    private void verifyBookUpdates(JSONResponse books) {
        books.getList().stream()
                .filter(book -> book.isPublic())
                .forEach(book -> {
                    log.fine("Verifying if the book " + book.getName() + " was updated.");
                    if (book.getCounts().getUpdates() > repository.getBookUpdates(book.getName())) {
                        repository.bookUpdate(new BookUpdates(book.getName(), book.getCounts().getUpdates()));
                        notify("The following book was updated: <a href=\"" + book.getUrls().getRead() + "\">" + book.getName() + "</a> " + Emoji.OPEN_BOOK);
                    }
                });

    }

    /**
     * Send notification messages to the Telegram group that this service is active.
     *
     * @param msg
     */
    private void notify(String msg) {
        Chat chat = new Chat();
        chat.setId(Long.parseLong(chatId));
        message.setChat(chat);
        message.setText(msg);
        messageSender.processOutgoingMessage(message);
    }

    private CloseableHttpClient client() {
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }
}