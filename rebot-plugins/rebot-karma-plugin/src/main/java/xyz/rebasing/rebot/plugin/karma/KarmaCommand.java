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

package xyz.rebasing.rebot.plugin.karma;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.service.persistence.domain.Karma;
import xyz.rebasing.rebot.service.persistence.repository.KarmaRepository;

import static xyz.rebasing.rebot.api.utils.Formatter.normalize;

@ApplicationScoped
public class KarmaCommand implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    private KarmaRepository karma;

    @Override
    public void load() {
        // on startup set the locale to en
        log.debugv("Loading command {0}", this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        if (key.get().length() < 1) {
            return String.format(I18nHelper.resource("KarmaMessages",
                                                     locale, "parameter.is.required"), this.name());
        }

        StringBuilder response = new StringBuilder();

        if (key.get().contains("%")) {
            List<Karma> karmas = karma.list(key.get());
            if (karmas.isEmpty()) {
                response.append(String.format(I18nHelper.resource("KarmaMessages",
                                                                  locale, "response.not.found"), key.get()));
            }
            karmas.stream().forEach(karma -> response.append(String.format(String.format(I18nHelper.resource("KarmaMessages",
                                                                                                             locale, "response.found"), normalize(karma.getUsername()), karma.getPoints()) + "\n")));
        } else {
            response.append(String.format(String.format(I18nHelper.resource("KarmaMessages",
                                                                            locale, "response.found"), normalize(key.get()), key.get().length() > 0 ? karma.get(key.get()) : 0)));
        }

        return response;
    }

    @Override
    public String name() {
        return "/karma";
    }

    @Override
    public String help(String locale) {
        return this.name() + " - " + String.format(I18nHelper.resource("KarmaMessages",
                                                                       locale, "karma.help"));
    }

    @Override
    public String description(String locale) {
        return String.format(I18nHelper.resource("KarmaMessages",
                                                 locale, "karma.description"));
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
