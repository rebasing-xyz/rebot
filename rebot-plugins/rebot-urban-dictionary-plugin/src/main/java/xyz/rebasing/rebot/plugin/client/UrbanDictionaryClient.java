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

package xyz.rebasing.rebot.plugin.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import xyz.rebasing.rebot.plugin.client.builder.UrbanDictionaryClientBuilder;
import xyz.rebasing.rebot.service.cache.pojo.urban.CustomTermResponse;
import xyz.rebasing.rebot.service.cache.pojo.urban.Term;

public class UrbanDictionaryClient implements IUrbanDictionaryClient {

    private static final String URBAN_DICTIONARY_ENDPOINT = "http://api.urbandictionary.com/v0/define";

    private String term;
    private int numberOfResults;
    private boolean showExample;

    public UrbanDictionaryClient(UrbanDictionaryClientBuilder builder) {
        this.term = builder.term;
        this.numberOfResults = builder.numberOfResults;
        this.showExample = builder.showExample;
    }

    public String getTerm() {
        return term;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public boolean isShowExample() {
        return showExample;
    }

    @Override
    public String toString() {
        return "UrbanDictionaryClient{" +
                "term='" + term + '\'' +
                ", numberOfResults=" + numberOfResults +
                ", showExample=" + showExample +
                '}';
    }

    /**
     * Execute the request
     *
     * @return {@link CustomTermResponse}
     */
    public List<CustomTermResponse> execute() throws UnsupportedEncodingException {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(this.URBAN_DICTIONARY_ENDPOINT + "?term=" + encode(this.term));

        Response response = target.request().get();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to connect in the urban dictionary endpoint " + this.URBAN_DICTIONARY_ENDPOINT + ", status code is: " + response.getStatus());
        }

        List<CustomTermResponse> c = new ArrayList<>();

        response.readEntity(Term.class)
                .getList()
                .stream().limit(this.numberOfResults)
                .forEach(entry -> {
                    if (this.showExample) {
                        c.add(new CustomTermResponse(entry.getWord(), entry.getDefinition(), entry.getExample(), entry.getPermalink()));
                    } else {
                        c.add(new CustomTermResponse(entry.getWord(), entry.getDefinition(), entry.getPermalink()));
                    }
                });

        return c;
    }

    /**
     * Encode the term
     *
     * @param term
     * @return encoded term
     * @throws UnsupportedEncodingException
     */
    private String encode(String term) throws UnsupportedEncodingException {
        return URLEncoder.encode(term, "UTF-8");
    }
}
