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

package it.rebase.rebot.service.currency;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.service.currency.provider.fixer.io.AvailableCurrencies;
import it.rebase.rebot.service.currency.provider.fixer.io.FixerIO;
import it.rebase.rebot.service.currency.provider.fixer.io.pojo.Rates;
import it.rebase.rebot.service.currency.provider.fixer.io.pojo.ResponseBase;
import org.apache.commons.logging.impl.AvalonLogger;
import org.apache.http.client.utils.URIBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@ApplicationScoped
public class CurrencyCommand implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
    }

    @Override
    public String name() {
        return "/currency";
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        if (key.get().length() < 1) return "Parameter is required.";

        StringBuilder response = new StringBuilder();
        String query = key.get().toUpperCase();
        try {
            URIBuilder builder = new URIBuilder(FixerIO.FIXER_IO_BASE_URL).setPath(FixerIO.LATEST);
            String firstParameter =query.split(" ")[0].trim();

            switch (firstParameter) {

                case "base":
                    String base = query.split(" ")[1];
                    // try to get Symbols
                    String basesymbols = "";
                    try {
                        basesymbols = query.split(" ")[2];
                    } catch (IndexOutOfBoundsException e) {
                        basesymbols = FixerIO.DEFAULT_SYMBOLS;
                    }
                    builder.setParameter(FixerIO.BASE, base).setParameter(FixerIO.SYMBOLS, basesymbols);
                    ResponseBase responseBase = (ResponseBase) new FixerIO().execute(builder.build().toString());
                    response.append("Base: <b>" + base + "</b>\n");
                    for (String symbol : basesymbols.split(",")) {
                        if (!base.equals(symbol)) {
                            response.append("   - 1 <b>" + base + "</b> = <code>" + getFieldValue(responseBase.getRates(), symbol) + "</code> ");
                            response.append("<b>" + symbol + "</b>\n");
                        }
                    }

                    break;

                case "exrate":
                    Pattern EXRATE_PATTERN = Pattern.compile("(^\\d+)\\w{3}-\\w{3}$");
                    //exrate 1 0 0 U S D - G B P
                    String value = "";
                    try {
                        value = query.split(" ")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        response.append("Parameter for exrate nor found.");
                    }

                    if (EXRATE_PATTERN.matcher(value).find()) {
                        int amount = Integer.parseInt(value.substring(0, value.length() - 7 ));
                        String fromCurrency = value.substring( value.length() - 7, value.length() - 4);
                        String toCurrency = value.substring( value.length() - 3, value.length());
                        builder.setParameter(FixerIO.BASE, fromCurrency).setParameter(FixerIO.SYMBOLS, toCurrency);
                        ResponseBase exrateResponseBase = (ResponseBase) new FixerIO().execute(builder.build().toString());
                        response.append(amount + " " + fromCurrency);
                        response.append(" = <b>");
                        response.append(new DecimalFormat("#.##").
                                format(amount * Double.parseDouble(getFieldValue(exrateResponseBase.getRates(), toCurrency))));
                        response.append(toCurrency + "</b>");
                    } else {
                        response.append("Parameter " + value + " is not valid.");
                    }
                    break;

                case "get":
                    response.append(Arrays.asList(AvailableCurrencies.class.getEnumConstants()));
                    break;

                case "name":
                    String currency = query.split(" ")[1];
                    try {
                        response.append(AvailableCurrencies.valueOf(currency).fullName());
                    } catch (final Exception e) {
                        response.append("Currency not found: " + currency);
                    }
                    break;

                default:
                    String[] sym = firstParameter.split(",");
                    builder.setParameter(FixerIO.BASE, FixerIO.DEFAULT_BASE_CURRENCY);
                    builder.setParameter(FixerIO.SYMBOLS, firstParameter);
                    ResponseBase defaultResponseBase = (ResponseBase) new FixerIO().execute(builder.build().toString());
                    response.append("Base: <b>" + FixerIO.DEFAULT_BASE_CURRENCY + "</b>\n");
                    for (String symbol : sym) {
                        if (!FixerIO.DEFAULT_BASE_CURRENCY.equals(symbol)) {
                            response.append("   - 1 <b>" + FixerIO.DEFAULT_BASE_CURRENCY + "</b> = <code>" + getFieldValue(defaultResponseBase.getRates(), symbol) + "</code> ");
                            response.append("<b>" + symbol.toUpperCase() + "</b>\n");
                        }
                    }
                    break;

            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return response.toString();

    }

    @Override
    public String help() {
        StringBuilder response = new StringBuilder(this.name() + " - " + this.description() + ", base currency is USD.\n ");
        response.append("Ex: " + this.name() + " BRL - returns the BRL value based on USD, Also accepts more than one like BRL, EUR, GBP\n");
        response.append("Ex: " + this.name() + " base BRL USD,GBP,AUD - returns the values of USD, GBP and AUD based on BRL\n");
        response.append("Ex: " + this.name() + " exrate 100USD-GBP - calculate the exchange rate of 100 USD to GPB\n");
        response.append("Ex: " + this.name() + " get - returns all supported currencies");
        response.append("Ex: " + this.name() + " name EUR - returns the EUR currency name");
        return response.toString();
    }

    @Override
    public String description() {
        return "currency rates + exchange rate";
    }

    private String getFieldValue(Rates rates, String fieldName) {
        try {
            Field field = rates.getClass().getDeclaredField(fieldName.toUpperCase());
            field.setAccessible(true);
            return field.get(rates).toString();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        }
        return "Currency not supported";
    }

}