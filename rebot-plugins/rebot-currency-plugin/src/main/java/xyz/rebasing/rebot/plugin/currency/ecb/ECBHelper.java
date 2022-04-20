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

package xyz.rebasing.rebot.plugin.currency.ecb;

import java.text.DecimalFormat;
import java.util.Optional;

import xyz.rebasing.rebot.service.persistence.domain.Cube;

public abstract class ECBHelper {

    public static final String DEFAULT_BASE_CURRENCY = "USD";
    public static final String DEFAULT_SYMBOLS = "BRL,USD,GBP,EUR";

    public static double calculateRateConversion(Cube baseCurrency, Optional<Cube> targetCurrency, double targetExrate) {
        double baseRate = 0;
        if (null == baseCurrency) {
            return formatNumber(targetCurrency.get().getRate() * targetExrate);
        } else {
            baseRate = (1 * targetExrate) / baseCurrency.getRate();
        }

        if (!targetCurrency.isPresent()) {
            return formatNumber(baseRate);
        } else {
            Double base = formatNumber(baseRate);
            return formatNumber(targetCurrency.get().getRate() * base);
        }
    }

    private static double formatNumber(Double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return Double.parseDouble(decimalFormat.format(number));
    }
}