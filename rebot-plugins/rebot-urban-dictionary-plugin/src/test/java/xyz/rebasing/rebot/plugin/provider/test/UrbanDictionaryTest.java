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

package xyz.rebasing.rebot.plugin.provider.test;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import xyz.rebasing.rebot.plugin.client.UrbanDictionaryClient;
import xyz.rebasing.rebot.plugin.client.builder.UrbanDictionaryClientBuilder;

public class UrbanDictionaryTest {

    @Test
    public void testUbClientBuilderDefaultValues() {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").build();
        Assert.assertEquals("lol", client.getTerm());
        Assert.assertEquals(1, client.getNumberOfResults());
    }

    @Test
    public void testUbClientBuilder() {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("rofl").numberOfResults(2).showExample().build();
        Assert.assertEquals("rofl", client.getTerm());
        Assert.assertEquals(2, client.getNumberOfResults());
        Assert.assertEquals(true, client.isShowExample());
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testNullTerm() {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().build();
    }

    @Test
    public void testDefaultNumberOfresults() throws UnsupportedEncodingException {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").build();
        Assert.assertEquals(1, client.execute().size());
    }

    @Test
    public void testNumberOfresultsTo1() throws UnsupportedEncodingException {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").numberOfResults(1).build();
        Assert.assertEquals(1, client.execute().size());
    }

    @Test
    public void testNumberOfresultsTo2() throws UnsupportedEncodingException {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").numberOfResults(2).build();
        Assert.assertEquals(2, client.execute().size());
    }

    @Test
    public void testShowExample() throws UnsupportedEncodingException {
        UrbanDictionaryClient client = new UrbanDictionaryClientBuilder().term("lol").numberOfResults(1).showExample().build();
        client.execute().stream().forEach(e -> {
            Assert.assertTrue(e.getExample() != null);
        });
    }
}