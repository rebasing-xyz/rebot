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

package xyz.rebasing.rebot.plugin.urbandictionary.client.builder;

import xyz.rebasing.rebot.plugin.urbandictionary.client.UrbanDictionaryClient;

/**
 * The client builder
 * It should have at least the term parameter
 *  term - term to be searched
 *  numberOfResults - the number of term definitions that will be shown
 *      in its absence, the number should be 1
 *  showExample - if specified, return a example of the term
 */
public class UrbanDictionaryClientBuilder {

    public String term;
    public int numberOfResults = 1;
    public boolean showExample;

    public UrbanDictionaryClientBuilder numberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
        return this;
    }

    public UrbanDictionaryClientBuilder term(String term) {
        this.term = term;
        return this;
    }

    public UrbanDictionaryClientBuilder showExample() {
        this.showExample = true;
        return this;
    }

    public UrbanDictionaryClient build() {
        if (null == term || "".equals(term)) {
            throw new IllegalArgumentException("The term parameter cannot be null.");
        }
        return new UrbanDictionaryClient(this);
    }
}