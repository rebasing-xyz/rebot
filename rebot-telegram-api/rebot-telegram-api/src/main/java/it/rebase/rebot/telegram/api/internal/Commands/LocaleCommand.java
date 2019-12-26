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
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.i18n.SupportedLocales;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;
import it.rebase.rebot.api.management.user.UserManagement;
import it.rebase.rebot.service.persistence.pojo.ChatLocale;
import it.rebase.rebot.service.persistence.repository.LocaleRepository;

@ApplicationScoped
public class LocaleCommand implements AdministrativeCommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;

    @Inject
    private UserManagement userManagement;

    @Inject
    private LocaleRepository localeRepository;

    @Override
    public void load() {
        log.fine("Enabling chat locale command " + this.name());
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate, String locale) {

        boolean isAdministrator = userManagement.isAdministrator(messageUpdate);

        if (!key.isPresent() || key.get().equals("")) {
            return String.format(I18nHelper.resource("Administrative", locale, "locale.current.definition"),
                                 messageUpdate.getMessage().getChat().getTitle(),
                                 localeRepository.get(messageUpdate.getMessage().getChat().getId(), messageUpdate.getMessage().getChat().getTitle()));
        } else {

            if (!isAdministrator) {
                return I18nHelper.resource("Administrative", locale, "locale.command.not.allowed");
            }

            try {

                ChatLocale chatLocale = new ChatLocale(messageUpdate.getMessage().getChat().getId(),
                                                       messageUpdate.getMessage().getChat().getTitle(),
                                                       SupportedLocales.valueOf(key.get()).localeName());

                if ("persisted".equals(localeRepository.persistChatLocale(chatLocale))) {
                    return String.format(I18nHelper.resource("Administrative", SupportedLocales.valueOf(key.get()).localeName(),
                                                             "locale.current.definition"),
                                         messageUpdate.getMessage().getChat().getTitle(),
                                         localeRepository.get(messageUpdate.getMessage().getChat().getId(), messageUpdate.getMessage().getChat().getTitle()));
                }
            } catch (final Exception e) {

                return String.format(I18nHelper.resource("Administrative", locale, "locale.definition.not.valid"),
                                     key.get(),
                                     Arrays.asList(SupportedLocales.class.getEnumConstants()));
            }

            return "Something went wrong, check logs or contact administrator: just@rebase.it";
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
}