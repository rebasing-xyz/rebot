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

package it.rebase.rebot.service.karma;

import it.rebase.rebot.api.emojis.Emoji;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.service.persistence.repository.KarmaRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class KarmaCommand implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final String RESPONSE = "<b>%s</b> have <b>%s</b> point(s) of karma " + Emoji.THUMBS_UP_SIGN;

    @Inject
    private KarmaRepository karma;

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        if (key.get().length() < 1) {
            return "Parameter is required, use " + this.name() + " help for assistance.";
        }
        return String.format(RESPONSE, key.get(), key.get().length() > 0 ? karma.get(key.get()) : 0);
    }

    @Override
    public String name() {
        return "/karma";
    }

    @Override
    public String help() {
        return this.name() + " - Returns the karma for the given user/key. Ex: /karma chuckNorris";
    }

    @Override
    public String description() {
        return "search the karma points for the given key";
    }
}
