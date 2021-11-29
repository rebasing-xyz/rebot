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
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.domain.MessageUpdate;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.i18n.SupportedLocales;
import xyz.rebasing.rebot.api.shared.components.management.user.UserManagement;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;
import xyz.rebasing.rebot.service.persistence.domain.ChatLocale;
import xyz.rebasing.rebot.service.persistence.repository.LocaleRepository;

@ApplicationScoped
public class LocaleCommand implements AdministrativeCommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    BotConfig config;

    @Inject
    UserManagement userManagement;

    @Inject
    LocaleRepository localeRepository;

    @Override
    public void load() {
        log.debugv("Enabling chat locale command {0}", this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {

        boolean isAdministrator = userManagement.isAdministrator(messageUpdate);

        if (!key.isPresent() || "".equals(key.get())) {
            return String.format(I18nHelper.resource("Administrative", locale, "locale.current.definition"),
                                 messageUpdate.getMessage().getChat().getTitle(),
                                 localeRepository.get(messageUpdate.getMessage().getChat().getId(),
                                                      messageUpdate.getMessage().getChat().getTitle()));
        } else {

            if (!isAdministrator) {
                return I18nHelper.resource("Administrative", locale, "locale.command.not.allowed");
            }

            try {

                ChatLocale chatLocale = new ChatLocale(messageUpdate.getMessage().getChat().getId(),
                                                       messageUpdate.getMessage().getChat().getTitle(),
                                                       SupportedLocales.valueOf(key.get()).localeName());

                if ("persisted".equals(localeRepository.persistChatLocale(chatLocale))) {
                    return String.format(I18nHelper.resource("Administrative",
                                                             SupportedLocales.valueOf(key.get()).localeName(),
                                                             "locale.current.definition"),
                                         messageUpdate.getMessage().getChat().getTitle(),
                                         localeRepository.get(messageUpdate.getMessage().getChat().getId(), messageUpdate.getMessage().getChat().getTitle()));
                }
            } catch (final Exception e) {

                return String.format(I18nHelper.resource("Administrative", locale, "locale.definition.not.valid"),
                                     key.get(),
                                     Arrays.asList(SupportedLocales.class.getEnumConstants()));
            }

            return "Something went wrong, check logs or contact administrator.";
        }
    }

    @Override
    public String name() {
        return "/locale";
    }

    @Override
    public String help(String locale) {
        return String.format(
                I18nHelper.resource("Administrative", locale, "locale.command.help"),
                this.name(),
                this.name(),
                this.name());
    }

    @Override
    public String description(String locale) {
        return I18nHelper.resource("Administrative", locale, "locale.command.description");
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