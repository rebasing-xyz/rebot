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

package it.rebase.rebot.plugin.provider.ecb;

public class CurrencyObject {

    private String query;
    private String firstParameter;
    private int exchangeValue;

    public CurrencyObject(String query) {
        query = query.trim().replaceAll("\\s{2,}", " ");
        this.firstParameter = query.split(" ")[0].trim();
        this.exchangeValue = 1;
        this.query = query;
    }

    public String firstParameter() {
        return firstParameter;
    }

    public String[] symbols() {
        String[] symbols = query.replaceAll("\\s*,\\s*",",").split(" ");
        int pos = firstParameter.equalsIgnoreCase("base") ? 2 : 0;
        try {
            symbols = symbols[pos].split(",");
        } catch (final Exception e) {
            symbols = ECBHelper.DEFAULT_SYMBOLS.split(",");
        }

        if (isInteger(symbols[0])) {
            symbols = ECBHelper.DEFAULT_SYMBOLS.split(",");
        }

        return symbols;
    }

    public String symbol() {
        return query.split(" ")[1];
    }

    public String baseCurrency() {
        return this.firstParameter.equalsIgnoreCase("base") ? query.split(" ")[1] : "";
    }

    public int exchangeValue() {

        for (String value : query.split(" ")) {
            try {
                exchangeValue = Integer.parseInt(value);
            } catch (Exception ignore) {
                // do nothing
            }
        }
        return exchangeValue;
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}