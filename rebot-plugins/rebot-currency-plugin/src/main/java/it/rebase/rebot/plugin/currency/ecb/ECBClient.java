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

package it.rebase.rebot.plugin.currency.ecb;

import io.quarkus.scheduler.Scheduled;
import it.rebase.rebot.service.cache.qualifier.CurrencyCache;
import it.rebase.rebot.service.persistence.repository.EcbRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@ApplicationScoped
public class ECBClient {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static final String ECB_XML_ADDRESS = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    @Inject
    EcbSaxHandler handler;

    @Inject
    EcbRepository repository;

    @Inject
    @CurrencyCache
    Cache<String, Object> cache;

    @Scheduled(every = "12h", delay = 60)
    public void getAndPersistDailyCurrencies() {
        try {
            log.fine("Parsing currencies from " + ECB_XML_ADDRESS);
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            HttpGet httpReq = new HttpGet(ECB_XML_ADDRESS);
            HttpResponse response = client().execute(httpReq);
            saxParser.parse(response.getEntity().getContent(), handler);
            repository.persist(handler.cubes());
            cache.clear();
            handler.cubes().getCubes().forEach(cube -> {
                cache.put(cube.getCurrency(), cube);
                cache.put("time", handler.cubes().getTime());
            });
        } catch (final Exception e) {
            log.severe("Error to retrieve currency rates from " + ECB_XML_ADDRESS + " - message: " + e.getMessage());
        } finally {
            handler.clean();
        }
    }

    private CloseableHttpClient client() {
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }

}