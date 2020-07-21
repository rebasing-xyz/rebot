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

import it.rebase.rebot.api.conf.BotConfig;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class Ping implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        return "pong";
    }

    @Override
    public String name() {
        return "/ping";
    }

    @Override
    public String help(String locale) {
        return "/ping - pong";
    }

    @Override
    public String description(String locale) {
        return "pong";
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