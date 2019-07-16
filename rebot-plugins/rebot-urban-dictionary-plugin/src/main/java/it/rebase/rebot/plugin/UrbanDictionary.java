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

import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.plugin.helper.Helper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class UrbanDictionary implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @Any
    private Helper helper;

    @Override
    public void load() {
        log.fine("Loading command " + this.name("en"));
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        return key.get().length() > 0 ? helper.query(key.get()) : String.format(
                I18nHelper.resource("Urban", messageUpdate.getMessage().getFrom().getLanguageCode(), "required.parameter"),
                this.name(messageUpdate.getMessage().getFrom().getLanguageCode()));
    }

    @Override
    public String name(String locale) {
        return "/urban";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Urban", locale, "urban.help"),
                this.name(locale));
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Urban", locale, "description");
    }
}
