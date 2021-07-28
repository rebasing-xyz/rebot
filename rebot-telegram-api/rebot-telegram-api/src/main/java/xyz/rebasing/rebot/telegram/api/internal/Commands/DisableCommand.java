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

package xyz.rebasing.rebot.telegram.api.internal.Commands;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.management.user.UserManagement;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;
import xyz.rebasing.rebot.service.persistence.domain.CommandStatus;
import xyz.rebasing.rebot.service.persistence.repository.ApiRepository;
import xyz.rebasing.rebot.telegram.api.UpdatesReceiver;

@ApplicationScoped
public class DisableCommand implements AdministrativeCommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    private UpdatesReceiver updatesReceiver;
    @Inject
    private ApiRepository repository;
    @Inject
    private UserManagement userManagement;

    @Override
    public void load() {
        log.debugv("Enabling administrative command {0}", this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        if (key.get().length() < 1) {
            return I18nHelper.resource("Administrative", locale, "required.parameter");
        }

        boolean isAdministrator = userManagement.isAdministrator(messageUpdate);
        if (!isAdministrator) {
            return I18nHelper.resource("Administrative", locale, "disable.command.not.allowed");
        }

        if (key.get().equals("bot")) {
            if (!updatesReceiver.isEnabled(messageUpdate.getMessage().getChat().getId())) {
                return String.format(
                        I18nHelper.resource("Administrative", locale, "disable.command.already.disabled"),
                        config.botUserId());
            }

            updatesReceiver.disable(messageUpdate.getMessage());
            return String.format(
                    I18nHelper.resource("Administrative", locale, "disable.command.disabled"),
                    config.botUserId());
        } else {
            // if the provided command or plugin is valid make sure it is not already disabled before proceed.
            if (!repository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), key.get())) {
                return String.format(
                        I18nHelper.resource("Administrative", locale, "disable.command.already.disabled"),
                        key.get());
            } else {
                repository.disableCommand(new CommandStatus(messageUpdate.getMessage().getChat().getId(), key.get(), false));
                return String.format(
                        I18nHelper.resource("Administrative", locale, "disable.command.disabled"),
                        key.get());
            }
        }
    }

    @Override
    public String name() {
        return "/disable";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Administrative", locale, "disable.command.help"),
                this.name());
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Administrative", locale, "disable.command.description");
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