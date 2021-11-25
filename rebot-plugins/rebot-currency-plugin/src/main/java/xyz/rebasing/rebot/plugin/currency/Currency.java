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

package xyz.rebasing.rebot.plugin.currency;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.plugin.currency.ecb.AvailableCurrencies;
import xyz.rebasing.rebot.plugin.currency.ecb.CurrencyObject;
import xyz.rebasing.rebot.plugin.currency.ecb.ECBClient;
import xyz.rebasing.rebot.plugin.currency.ecb.ECBHelper;
import xyz.rebasing.rebot.service.persistence.domain.Cube;

@ApplicationScoped
public class Currency implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    ECBClient ecbClient;

    @Override
    public void load() {
        new Thread(() -> {
            // on startup defaults to en
            log.debugv("Loading command {0}", this.name());
            ecbClient.getAndPersistDailyCurrencies();
        }).start();
    }

    @Override
    public String name() {
        return "/currency";
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        if (key.get().length() < 1) {
            return I18nHelper.resource("CurrencyMessages", locale, "required.parameter");
        }

        StringBuilder response = new StringBuilder();
        CurrencyObject currency = new CurrencyObject(key.get().toUpperCase(Locale.forLanguageTag(locale)));

        if (canProcess()) {

            switch (currency.firstParameter()) {

                case "BASE":
                    try {
                        response.append(String.format(
                                I18nHelper.resource("CurrencyMessages", locale, "base.response"),
                                currency.baseCurrency()));

                        for (String symbol : currency.symbols()) {
                            if (!currency.baseCurrency().equals(symbol)) {
                                response.append(String.format(
                                        I18nHelper.resource("CurrencyMessages", locale, "base.response.append"),
                                        currency.exchangeValue(),
                                        currency.baseCurrency(),
                                        getCurrencyValue(currency.baseCurrency(), symbol, currency.exchangeValue(), locale),
                                        symbol.toUpperCase()));
                            }
                        }
                    } catch (final Exception e) {
                        response.append(String.format(
                                I18nHelper.resource("CurrencyMessages", locale, "symbol.not.supported"),
                                currency.baseCurrency()));
                    }
                    response.append(String.format(
                            I18nHelper.resource("CurrencyMessages", locale, "currency.date"),
                            ecbClient.cache().getIfPresent("time")));
                    break;

                case "GET":
                    response.append(Arrays.asList(AvailableCurrencies.class.getEnumConstants()));
                    break;

                case "NAME":
                    try {
                        response.append(AvailableCurrencies.valueOf(currency.symbol()).fullName());
                    } catch (final Exception e) {
                        response.append(String.format(
                                I18nHelper.resource("CurrencyMessages", locale, "not.found"),
                                currency.symbol()));
                    }
                    break;

                default:
                    for (String symb : currency.symbols()) {
                        if (ECBHelper.DEFAULT_BASE_CURRENCY.equals(symb)) {
                            response.append(String.format(
                                    I18nHelper.resource("CurrencyMessages", locale, "base.currency"),
                                    ECBHelper.DEFAULT_BASE_CURRENCY));
                        } else {
                            response.append(String.format(
                                    I18nHelper.resource("CurrencyMessages", locale, "exrate.response"),
                                    currency.exchangeValue(),
                                    ECBHelper.DEFAULT_BASE_CURRENCY,
                                    getCurrencyValue(ECBHelper.DEFAULT_BASE_CURRENCY, symb, currency.exchangeValue(), locale),
                                    symb.toUpperCase()));
                        }
                    }
                    response.append(String.format(
                            I18nHelper.resource("CurrencyMessages", locale, "currency.date"),
                            ecbClient.cache().getIfPresent("time")));
                    break;
            }
            return response.toString();
        } else {
            return I18nHelper.resource("CurrencyMessages", locale, "error.state");
        }
    }

    @Override
    public String help(String locale) {
        StringBuilder response = new StringBuilder(this.name() + " - " + this.description(locale));
        response.append(I18nHelper.resource("CurrencyMessages", locale, "currency.help"));
        return response.toString();
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("CurrencyMessages", locale, "description");
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }

    private Object getCurrencyValue(String baseCurrencyId, String currencyID, double targetExrate, String locale) {
        try {
            if (currencyID.equalsIgnoreCase("EUR")) {
                return ECBHelper.calculateRateConversion((Cube) ecbClient.cache().getIfPresent(baseCurrencyId),
                                                         Optional.empty(),
                                                         targetExrate);
            }

            Cube cube = (Cube) ecbClient.cache().getIfPresent(String.valueOf(AvailableCurrencies.valueOf(currencyID)));
            return ECBHelper.calculateRateConversion((Cube) ecbClient.cache().getIfPresent(baseCurrencyId),
                                                     Optional.of(cube),
                                                     targetExrate);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return I18nHelper.resource("CurrencyMessages", locale, "not.supported");
    }

    /**
     * Before process the command, verifies if the cache is working
     *
     * @return true if the cache is functional
     */
    private boolean canProcess() {
        try {
            log.debug("Verifying if the cache is functional");
            Cube cube = (Cube) ecbClient.cache().getIfPresent("USD");
            if (null != cube.getCurrency()) {
                return true;
            } else {
                return false;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            log.debugv("Currency Plugin is not functional at this moment [{0}]", e.getMessage());
            return false;
        }
    }
}