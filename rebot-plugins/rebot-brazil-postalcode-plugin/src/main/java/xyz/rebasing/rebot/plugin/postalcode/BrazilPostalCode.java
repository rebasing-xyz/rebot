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

package xyz.rebasing.rebot.plugin.postalcode;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.plugin.postalcode.utils.BrazilPostalCodeUtils;

@ApplicationScoped
public class BrazilPostalCode implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private Long DEFAULT_RESULT_LIMIT = 2L;
    private boolean RETURN_ONLY_UF = false;

    @Inject
    BotConfig config;

    @Inject
    private BrazilPostalCodeUtils service;

    @Override
    public void load() {
        new Thread(() -> {
            service.processCSVFile();
            log.debugv("Plugin Brazil Postal Code enabled - command {0}", this.name());
        }).start();
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        long limitResult = DEFAULT_RESULT_LIMIT;
        boolean returnOnlyUf = RETURN_ONLY_UF;
        String query = key.get();
        for (String str : key.get().split(" ")) {
            if (str.contains("-limit=")) {
                try {
                    limitResult = Long.parseLong(str.split("=")[1]);
                    log.debugv("Result limit is {0}", limitResult);
                } catch (final Exception e) {
                    log.warnv("Failed to parse {0}, error message: {1}", str.split("="), e.getMessage());
                    log.warnv("Defaulting to {0}", limitResult);
                }
                query = key.get().replace(str, "");
            }
            if (str.contains("-uf")) {
                returnOnlyUf = true;
                limitResult = 1;
                query = key.get().replace(str, "");
            }
        }

        return key.get().length() > 0 ? service.query(query, limitResult, returnOnlyUf, locale) :
                String.format(I18nHelper.resource("PostalCodeMessages",
                                                  locale, "usage"), this.name());
    }

    @Override
    public String name() {
        return "/ddd";
    }

    @Override
    public String help(String locale) {
        StringBuilder strBuilder = new StringBuilder(this.name() + " - ");
        strBuilder.append(I18nHelper.resource("PostalCodeMessages", locale, "ddd.help"));
        return strBuilder.toString();
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("PostalCodeMessages", locale, "description");
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }
}