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

package xyz.rebasing.rebot.plugin.test;

import org.junit.Assert;
import org.junit.Test;
import xyz.rebasing.rebot.plugin.currency.ecb.CurrencyObject;
import xyz.rebasing.rebot.plugin.currency.ecb.ECBHelper;

public class CurrencyObjectTest {

    @Test
    public void testCurrencyDefault() {
        // this will simulate a full command coming from a chat without command prefix
        CurrencyObject currency = new CurrencyObject("brl,gbp,usd");
        String[] expected = {"brl", "gbp", "usd"};
        Assert.assertArrayEquals(expected, currency.symbols());
    }

    @Test
    public void testCurrencyObjectFirstParameter() {
        // this will simulate a full command coming from a chat without command prefix
        CurrencyObject currency = new CurrencyObject("base eur brl,gbp,usd");
        String[] expected = {"brl", "gbp", "usd"};
        Assert.assertEquals("base", currency.firstParameter());
        Assert.assertEquals("eur", currency.baseCurrency());
        Assert.assertArrayEquals(expected, currency.symbols());
    }

    @Test
    public void testCurrencyNameParameter() {
        CurrencyObject currency = new CurrencyObject("name gbp");
        Assert.assertEquals("name", currency.firstParameter());
        Assert.assertEquals("gbp", currency.symbol());
    }

    @Test
    public void testCurrencyWithAmount() {
        CurrencyObject currency = new CurrencyObject("brl,gbp,usd 25");
        String[] expected = {"brl", "gbp", "usd"};
        Assert.assertArrayEquals(expected, currency.symbols());
        Assert.assertEquals(25, currency.exchangeValue());
    }

    @Test
    public void testCurrencyWithDefaultAmount() {
        CurrencyObject currency = new CurrencyObject("brl,gbp,usd");
        String[] expected = {"brl", "gbp", "usd"};
        Assert.assertArrayEquals(expected, currency.symbols());
        Assert.assertEquals(1, currency.exchangeValue());
    }

    @Test
    public void testCurrencyAmount() {
        CurrencyObject currency = new CurrencyObject("brl, gbp 5");
        String[] expected = {"brl", "gbp"};
        Assert.assertArrayEquals(expected, currency.symbols());
        Assert.assertEquals(5, currency.exchangeValue());
    }

    @Test
    public void testCurrencyAmountMultipleSpacesOnCurrencies() {
        CurrencyObject currency = new CurrencyObject("brl, gbp , zar 5");
        String[] expected = {"brl", "gbp", "zar"};
        Assert.assertArrayEquals(expected, currency.symbols());
        Assert.assertEquals(5, currency.exchangeValue());
    }

    @Test
    public void testCurrencyBaseWithAmount() {
        // this will simulate a full command coming from a chat without command prefix
        CurrencyObject currency = new CurrencyObject("base eur brl,gbp,usd 48");
        String[] expected = {"brl", "gbp", "usd"};
        Assert.assertEquals("base", currency.firstParameter());
        Assert.assertEquals("eur", currency.baseCurrency());
        Assert.assertArrayEquals(expected, currency.symbols());
        Assert.assertEquals(48, currency.exchangeValue());
    }

    @Test
    public void testCurrencyBaseDefaultSymbols() {
        // this will simulate a full command coming from a chat without command prefix
        CurrencyObject currency = new CurrencyObject("base eur");
        Assert.assertEquals("base", currency.firstParameter());
        Assert.assertEquals("eur", currency.baseCurrency());
        Assert.assertArrayEquals(ECBHelper.DEFAULT_SYMBOLS.split(","), currency.symbols());
    }

    @Test
    public void testCurrencyBaseDefaultSymbolswithAmount() {
        // this will simulate a full command coming from a chat without command prefix
        CurrencyObject currency = new CurrencyObject("base brl 45");
        Assert.assertEquals("base", currency.firstParameter());
        Assert.assertEquals("brl", currency.baseCurrency());
        Assert.assertArrayEquals(ECBHelper.DEFAULT_SYMBOLS.split(","), currency.symbols());
        Assert.assertEquals(45, currency.exchangeValue());
    }
}

