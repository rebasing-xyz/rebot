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

package it.rebase.rebot.service.urbandictionary.helper;

import it.rebase.rebot.service.cache.qualifier.UrbanDictionaryCache;
import it.rebase.rebot.service.urbandictionary.client.UrbanDictionaryClient;
import it.rebase.rebot.service.urbandictionary.client.builder.UrbanDictionaryClientBuilder;
import it.rebase.rebot.service.urbandictionary.client.pojo.CustomTermResponse;
import org.infinispan.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class Helper {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private UrbanDictionaryClient client;

    @Inject
    @UrbanDictionaryCache
    private Cache<Object, List<CustomTermResponse>> cache;

    public String query(String param) {
        String term = "";
        int numberOfResults = 1;
        boolean showExample = false;
        String[] parameters = param.split(" ");

        // prepare the parameters
        for (int i = 0; i < parameters.length; i++) {

            if (parameters[i].equals("-c")) {
                i += 1;
                try {
                    numberOfResults = Integer.parseInt(parameters[i]);
                    if (numberOfResults < 0) {
                        return "Only positive values are valid: " + numberOfResults;
                    }
                } catch (NumberFormatException e) {
                    return "Parameter " + parameters[i] + " is not valid.";
                }

            } else if (parameters[i].equals("-e")) {
                showExample = true;
            } else {
                if (parameters.length > i + 1) {
                    term += parameters[i] + " ";
                } else {
                    term += parameters[i];
                }
            }
        }

        List<CustomTermResponse> ubResponse = new ArrayList<>();
        StringBuilder response = new StringBuilder();
        try {
            ubResponse = processRequest(term, numberOfResults, showExample);
        } catch (UnsupportedEncodingException e) {
            response.append("Failure to retrieve information from Urban Dictionary: " + e.getMessage());
            e.printStackTrace();
        }

        if (ubResponse.size() == 0) {
            response.append("Term not found");
        } else {
            ubResponse.stream().forEach(item -> {
                response.append("\n<b>Definition:</b> " + normalize(item.getDefinition()) + "\n");
                if (item.getExample() != null) {
                    response.append("\n<b>Example:</b> " + normalize(item.getExample()) + "\n");
                }
                response.append("" + normalize(item.getPermalink()) + "\n");
            });
        }

        return response.toString();
    }

    /**
     * Replace the html tags to avoid enconding problems, instead use html tags.
     * @param message
     * @return Mensage formatada.
     */
    private String normalize(String message) {
        return message.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * Make a new request against urban dictionary api.
     * for every request verify if the term is on cache, if not, make a new request and put it on the cache.
     * Also, if the entry on cache have only 1 definition, but user has requested more than 1, make a new
     * request and update the cache entry.
     *
     * @param term
     * @param numberOfResults
     * @param showExample
     * @return {@link List<CustomTermResponse>}
     */
    private List<CustomTermResponse> processRequest(String term, int numberOfResults, boolean showExample) throws UnsupportedEncodingException {
        UrbanDictionaryClient client;
        UrbanDictionaryClientBuilder builder = new UrbanDictionaryClientBuilder();
        if (cache.containsKey(term.trim())) {
            List<CustomTermResponse> cacheItems = cache.get(term.trim());
            if (showExample || cacheItems.size() != numberOfResults) {
                List<CustomTermResponse> itemsWithNoExample = cacheItems.stream().filter(item -> item.getExample() == null).collect(Collectors.toList());
                if (itemsWithNoExample.size() > 0 || cacheItems.size() != numberOfResults) {
                    log.fine(term + " is available on cache but some information is missing.");
                    if (showExample)
                        builder.showExample();
                    client = builder.term(term).numberOfResults(numberOfResults).build();
                    List<CustomTermResponse> ubResult = client.execute();
                    cache.replace(term.trim(), ubResult, 1, TimeUnit.DAYS);
                    return ubResult;
                }
            } else {
                log.fine(term + " is available on cache, returning it from cache.");
                return cacheItems;
            }
        }
        log.fine(term + " is not available on cache, making a new request.");
        if (showExample)
            builder.showExample();
        client = builder.term(term).numberOfResults(numberOfResults).build();
        List<CustomTermResponse> ubResult = client.execute();
        cache.putIfAbsent(term.trim(), ubResult, 1, TimeUnit.DAYS);
        return ubResult;
    }

}
