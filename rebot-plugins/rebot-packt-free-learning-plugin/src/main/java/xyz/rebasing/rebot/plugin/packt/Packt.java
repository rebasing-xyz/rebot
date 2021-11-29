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

package xyz.rebasing.rebot.plugin.packt;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.plugin.packt.notifier.PacktNotifier;

@ApplicationScoped
public class Packt implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    PacktNotifier packtNotifier;

    @Override
    public void load() {
        new Thread(() -> {
            log.debugv("Loading command {0}", this.name());
            packtNotifier.populate();
        }).start();
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        if (key.isPresent() && "notify".equals(key.get())) {
            return packtNotifier.registerNotification(messageUpdate, locale);
        }
        if (key.isPresent() && "off".equals(key.get())) {
            return packtNotifier.unregisterNotification(messageUpdate, locale);
        }
        try {
            return packtNotifier.get(locale);
        } catch (final Exception e) {
            return String.format(
                    I18nHelper.resource("Packt", locale, "error.state"), this.name(), e.getMessage());
        }
    }

    @Override
    public String name() {
        return "/packt";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Packt", locale, "packt.help"),
                this.name(),
                this.name(),
                this.name(),
                this.name());
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Packt", locale, "description");
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
