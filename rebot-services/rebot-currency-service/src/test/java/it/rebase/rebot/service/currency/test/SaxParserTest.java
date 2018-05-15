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

package it.rebase.rebot.service.currency.test;

import it.rebase.rebot.service.currency.provider.ecb.AvailableCurrencies;
import it.rebase.rebot.service.currency.provider.ecb.EcbSaxHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class SaxParserTest {

    public static final String ECB_XML_ADDRESS = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private EcbSaxHandler handler = new EcbSaxHandler();

    @Before
    public void populateCubes() throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        HttpGet httpReq = new HttpGet(ECB_XML_ADDRESS);
        HttpResponse response = client().execute(httpReq);
        saxParser.parse(response.getEntity().getContent(), handler);
    }

    @Test
    public void testCubeTime() {
        Assert.assertNotNull(handler.cubes().getTime());
    }

    @Test
    public void testParser() {
        handler.cubes().getCubes().forEach(cube -> {
            Assert.assertNotNull(cube.getCurrency());
            Assert.assertNotNull(cube.getRate());
        });
    }

    @Test
    public void testCurrencySize() {
        // current number of available currencies
        Assert.assertTrue(handler.cubes().getCubes().size() >= 32);
    }

    @Test
    public void testAvailableCurrencies() {
        handler.cubes().getCubes().forEach(cube -> {
            AvailableCurrencies.valueOf(cube.getCurrency());
        });

    }

    private CloseableHttpClient client() {
        RequestConfig config = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        return HttpClients.custom().setDefaultRequestConfig(config).build();
    }
}