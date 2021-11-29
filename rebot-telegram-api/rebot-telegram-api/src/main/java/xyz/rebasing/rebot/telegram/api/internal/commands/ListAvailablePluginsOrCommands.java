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

package xyz.rebasing.rebot.telegram.api.internal.commands;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.shared.components.management.user.UserManagement;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.api.spi.PluginProvider;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;
import xyz.rebasing.rebot.service.persistence.repository.ApiRepository;

@ApplicationScoped
public class ListAvailablePluginsOrCommands implements AdministrativeCommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    ApiRepository repository;
    @Inject
    Instance<CommandProvider> command;
    @Inject
    Instance<PluginProvider> plugin;
    @Inject
    UserManagement userManagement;

    @Override
    public void load() {
        log.debugv("Enabling administrative command {0}", this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {
        boolean isAdministrator = userManagement.isAdministrator(messageUpdate);

        if (!isAdministrator) {
            return I18nHelper.resource("Administrative", locale, "list.command.not.allowed");
        } else {
            List<String> avialableResources = new ArrayList<>();
            command.stream().forEach(c -> {
                avialableResources.add(c.name().replace("/", ""));
            });
            plugin.stream().forEach(p -> {
                avialableResources.add(p.name());
            });

            if (key.isPresent() && "disabled".equals(key.get())) {
                avialableResources.removeIf(it -> repository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), it));
                if (!avialableResources.isEmpty()) {
                    return avialableResources;
                } else {
                    return I18nHelper.resource("Administrative", locale, "list.command.no.items.found");
                }
            }

            avialableResources.removeIf(it -> !repository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), it));
            return avialableResources;
        }
    }

    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Administrative", locale, "list.command.help"),
                this.name());
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Administrative", locale, "list.command.help");
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