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

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;
import it.rebase.rebot.api.management.user.UserManagement;
import it.rebase.rebot.service.persistence.repository.ApiRepository;
import it.rebase.rebot.telegram.api.UpdatesReceiver;

@ApplicationScoped
public class EnableCommand implements AdministrativeCommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    private UpdatesReceiver updatesReceiver;
    @Inject
    private ApiRepository repository;
    @Inject
    private Instance<CommandProvider> command;
    @Inject
    private Instance<PluginProvider> plugin;
    @Inject
    private UserManagement userManagement;

    @Override
    public void load() {
        log.fine("Enabling administrative command " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        if (key.get().length() < 1) return I18nHelper.resource("Administrative", locale, "required.parameter");

        boolean isAdministrator = userManagement.isAdministrator(messageUpdate);
        if (!isAdministrator) {
            return I18nHelper.resource("Administrative", locale, "enable.command.not.allowed");
        }

        if (key.get().equals("bot")) {
            if (updatesReceiver.isEnabled(messageUpdate.getMessage().getChat().getId())) {
                return String.format(
                        I18nHelper.resource("Administrative", locale, "enable.command.already.enabled"),
                        botUserId);
            }

            updatesReceiver.enable(messageUpdate.getMessage());
            return String.format(
                    I18nHelper.resource("Administrative", locale, "enable.command.enabled"),
                    botUserId);
        } else {
            // ve se o parametro passado eh um plugin ou comando valido e ve se ele ja esta ativado.
            if (repository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), key.get())) {
                return String.format(
                        I18nHelper.resource("Administrative", locale, "enable.command.already.enabled"),
                        key.get());
            } else {
                repository.enableCommand(messageUpdate.getMessage().getChat().getId(), key.get());
                return String.format(
                        I18nHelper.resource("Administrative", locale, "enable.command.enabled"),
                        key.get());
            }

        }
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

    @Override
    public boolean removeMessage() {
        return true;
    }

    @Override
    public long deleteMessageTimeout() {
        return 10;
    }
}