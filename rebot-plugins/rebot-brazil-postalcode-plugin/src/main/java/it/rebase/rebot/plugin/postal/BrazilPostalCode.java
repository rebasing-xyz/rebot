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

package it.rebase.rebot.plugin.postal;

import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.plugin.postal.utils.BrazilPostalCodeUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class BrazilPostalCode implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private Long DEFAULT_RESULT_LIMIT = 2L;
    private boolean RETURN_ONLY_UF = false;

    @Inject
    private BrazilPostalCodeUtils service;

    @Override
    public void load() {
        new Thread(() -> {
            service.processCSVFile();
            log.info("Plugin Brazil Postal Code enabled.");
        }).start();
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        String locale = messageUpdate.getMessage().getFrom().getLanguageCode();
        long limitResult = DEFAULT_RESULT_LIMIT;
        boolean returnOnlyUf = RETURN_ONLY_UF;
        String query = key.get();
        for (String str : key.get().split(" ")) {
            if (str.contains("--limit=")) {
                try {
                    limitResult = Long.parseLong(str.split("=")[1]);
                    log.fine("Result limit is " + limitResult);
                } catch (final Exception e) {
                    log.warning(String.format("Failed to parse %s, error message: %s",
                            str.split("="),
                            e.getMessage()));
                    log.warning(String.format("Defaulting to %d", limitResult));
                }
                query = key.get().replace(str, "");
            }
            if (str.contains("--uf")) {
                returnOnlyUf = true;
                limitResult = 1;
                query = key.get().replace(str, "");
            }
        }

        return key.get().length() > 0 ? service.query(query, limitResult, returnOnlyUf, locale) :
                String.format(I18nHelper.resource("PostalCodeMessages",
                        messageUpdate.getMessage().getFrom().getLanguageCode(), "usage"), this.name(locale));

    }

    @Override
    public String name(String locale) {
        return "/ddd";
    }

    @Override
    public String help(String locale) {
        StringBuilder strBuilder = new StringBuilder(this.name(locale) + " - ");
        strBuilder.append(I18nHelper.resource("PostalCodeMessages", locale, "ddd.help"));
        return strBuilder.toString();
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("PostalCodeMessages", locale, "description");
    }
}