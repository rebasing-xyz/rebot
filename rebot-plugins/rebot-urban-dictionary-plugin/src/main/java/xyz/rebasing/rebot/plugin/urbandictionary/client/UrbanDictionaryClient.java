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

package xyz.rebasing.rebot.plugin.urbandictionary.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.rebasing.rebot.plugin.urbandictionary.client.builder.UrbanDictionaryClientBuilder;
import xyz.rebasing.rebot.plugin.urbandictionary.domain.CustomTermResponse;
import xyz.rebasing.rebot.plugin.urbandictionary.domain.Term;

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

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Request request = new Request.Builder()
                .url(this.URBAN_DICTIONARY_ENDPOINT + "?term=" + encode(this.term))
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.code() == 404) {
                throw new RuntimeException("Failed to connect in the urban dictionary endpoint " +
                                                   this.URBAN_DICTIONARY_ENDPOINT + ", status code is: " + response.code());
            }

            List<CustomTermResponse> c = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            Term t = objectMapper.readValue(response.body().string(), Term.class);
            t.getList()
                    .stream().limit(this.numberOfResults)
                    .forEach(entry -> {
                        if (this.showExample) {
                            c.add(new CustomTermResponse(entry.getWord(), entry.getDefinition(), entry.getExample(), entry.getPermalink()));
                        } else {
                            c.add(new CustomTermResponse(entry.getWord(), entry.getDefinition(), entry.getPermalink()));
                        }
                    });
            return c;
        } catch (final Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
