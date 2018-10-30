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

package it.rebase.rebot.plugin;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.plugin.provider.ecb.AvailableCurrencies;
import it.rebase.rebot.plugin.provider.ecb.CurrencyObject;
import it.rebase.rebot.plugin.provider.ecb.ECBClient;
import it.rebase.rebot.plugin.provider.ecb.ECBHelper;
import it.rebase.rebot.service.cache.qualifier.CurrencyCache;
import it.rebase.rebot.service.persistence.pojo.Cube;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class Currency implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    private ECBClient ecbClient;

    @Inject
    @CurrencyCache
    private Cache<String, Cube> cache;

    private List<Cube> cubes;

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
        ecbClient.startTimer();
        ecbClient.getAndPersistDailyCurrencies();
    }

    @Override
    public String name() {
        return "/currency";
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        if (key.get().length() < 1) return "Parameter is required.";

        StringBuilder response = new StringBuilder();
        CurrencyObject currency = new CurrencyObject(key.get().toUpperCase());

        if (canProcess()) {

            switch (currency.firstParameter()) {

                case "BASE": // não ok
                    try {
                        response.append("Base: <b>" + currency.baseCurrency() + "</b>\n");
                        for (String symbol : currency.symbols()) {
                            if (!currency.baseCurrency().equals(symbol)) {
                                response.append("<b>#</b> " + currency.exchangeValue() + "<b> " + currency.baseCurrency() + "</b> = ");
                                response.append("<code>" + getCurrencyValue(currency.baseCurrency(), symbol, currency.exchangeValue()) + "</code> ");
                                response.append("<b>" + symbol.toUpperCase() + "</b>\n");
                            }
                        }
                    } catch (final Exception e) {
                        response.append("Symbol <b> " + currency.baseCurrency() + " not supported");
                    }
                    response.append("\n<code>Current rates update: </code><b>" + cache.get("time") + "</b>");
                    break;

                case "GET":
                    response.append(Arrays.asList(AvailableCurrencies.class.getEnumConstants()));
                    break;

                case "NAME":
                    try {
                        response.append(AvailableCurrencies.valueOf(currency.symbol()).fullName());
                    } catch (final Exception e) {
                        response.append("Currency not found: " + currency.symbol());
                    }
                    break;

                default:
                    for (String symb : currency.symbols()) {
                        if (ECBHelper.DEFAULT_BASE_CURRENCY.equals(symb)) {
                            response.append("The default base currency is " + ECBHelper.DEFAULT_BASE_CURRENCY + ", to use a different base currency use /currency base &#60;desired_currency&#62;\n");
                        } else {
                            response.append("<b>#</b> " + currency.exchangeValue() + "<b> " + ECBHelper.DEFAULT_BASE_CURRENCY + "</b> = ");
                            response.append("<code>" + getCurrencyValue(ECBHelper.DEFAULT_BASE_CURRENCY, symb, currency.exchangeValue()) + "</code> ");
                            response.append("<b>" + symb.toUpperCase() + "</b>\n");
                        }
                    }
                    response.append("\n<code>Current rates update: </code><b>" + cache.get("time") + "</b>");
                    break;
            }
            return response.toString();

        } else {
            return "Currency plugin is not functional, contact the administrator";
        }

    }

    @Override
    public String help() {
        StringBuilder response = new StringBuilder(this.name() + " - " + this.description() + ", base currency is USD.\n ");
        response.append("Ex: " + this.name() + " BRL - returns the BRL value based on USD, Also accepts more than one like BRL, EUR, GBP\n");
        response.append("Ex: " + this.name() + " base BRL USD,GBP,AUD - returns the values of USD, GBP and AUD based on BRL\n");
        response.append("Ex: " + this.name() + " For exchange rate use /currency or /currency base BASE_CURRENCY BRL 10\n");
        response.append("Ex: " + this.name() + " get - returns all supported currencies\n");
        response.append("Ex: " + this.name() + " name EUR - returns the EUR currency name");
        return response.toString();
    }

    @Override
    public String description() {
        return "currency rates + exchange rate";
    }

    private Object getCurrencyValue(String baseCurrencyId, String currencyID, double targetExrate) {
        try {
            if (currencyID.equalsIgnoreCase("EUR")) {
                return ECBHelper.calculateRateConversion(cache.get(baseCurrencyId), Optional.empty(), targetExrate);
            }

            Cube cube = cache.get(String.valueOf(AvailableCurrencies.valueOf(currencyID)));
            return ECBHelper.calculateRateConversion(cache.get(baseCurrencyId), Optional.of(cube), targetExrate);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return "Currency not supported";
    }

    /**
     * Before process the command, verifies if the cache is working
     *
     * @return true if the cache is functional
     */
    private boolean canProcess() {
        try {
            log.fine("Verifying if the cache is functional");
            Cube cube = cache.get("USD");
            if (null != cube.getCurrency()) return true;
            else return false;
        } catch (final Exception e) {
            log.fine("Currency Plugin is not functional at this moment [" + e.getMessage() + "]");
            return false;
        }
    }

}