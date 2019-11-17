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

package it.rebase.rebot.telegram.api.internal.Commands;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;
import it.rebase.rebot.telegram.api.UpdatesReceiver;
import it.rebase.rebot.telegram.api.chat.admin.ChatAdministratorsImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class EnableCommand implements AdministrativeCommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    private UpdatesReceiver updatesReceiver;

    @Inject
    private ChatAdministratorsImpl chatAdministratorsImpl;

    @Override
    public void load() {
        log.fine("Enabling administrative command " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        String locale = messageUpdate.getMessage().getFrom().getLanguageCode();
        boolean isAdministrator = chatAdministratorsImpl.isAdministrator(messageUpdate);
        if (isAdministrator && updatesReceiver.isEnabled(messageUpdate.getMessage().getChat().getId()))
            return String.format(
                    I18nHelper.resource("Administrative", locale, "enable.command.already.enabled"),
                    botUserId);
        if (!isAdministrator) return I18nHelper.resource("Administrative", locale, "enable.command.not.allowed");
        updatesReceiver.enable(messageUpdate.getMessage());
        return String.format(
                I18nHelper.resource("Administrative", locale, "enable.command.enabled"),
                botUserId);
    }

    @Override
    public String name() {
        return "/enable";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Administrative", locale, "enable.command.help"),
                this.name());
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Administrative", locale, "enable.command.description");
    }
}