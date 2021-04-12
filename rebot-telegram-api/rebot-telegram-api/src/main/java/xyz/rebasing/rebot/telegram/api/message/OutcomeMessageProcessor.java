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

package xyz.rebasing.rebot.telegram.api.message;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import xyz.rebasing.rebot.api.conf.BotConfig;
import xyz.rebasing.rebot.api.i18n.I18nHelper;
import xyz.rebasing.rebot.api.management.message.MessageManagement;
import xyz.rebasing.rebot.api.message.sender.MessageSender;
import xyz.rebasing.rebot.api.object.Message;
import xyz.rebasing.rebot.api.object.MessageUpdate;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.api.spi.PluginProvider;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;
import xyz.rebasing.rebot.service.persistence.repository.ApiRepository;
import xyz.rebasing.rebot.service.persistence.repository.LocaleRepository;

import static xyz.rebasing.rebot.telegram.api.filter.ReBotPredicate.isCommand;
import static xyz.rebasing.rebot.telegram.api.filter.ReBotPredicate.messageIsNotNull;
import static xyz.rebasing.rebot.telegram.api.utils.StringUtils.concat;

@ApplicationScoped
public class OutcomeMessageProcessor implements Processor {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isAdministrativeCommand = false;

    private String locale;

    @Inject
    BotConfig config;

    @Inject
    private Instance<CommandProvider> command;
    @Inject
    private Instance<AdministrativeCommandProvider> administrativeCommand;
    @Inject
    private Instance<PluginProvider> plugin;
    @Inject
    private MessageSender reply;
    @Inject
    private ApiRepository apiRepository;
    @Inject
    private LocaleRepository localeRepository;
    @Inject
    private MessageManagement messageManagement;

    @Override
    public void process(MessageUpdate messageUpdate) {
        locale = localeRepository.get(messageUpdate.getMessage().getChat().getId(), messageUpdate.getMessage().getChat().getTitle());
        log.fine("current message is being processed with the locale: " + locale);
        // before proceed with other commands/plugins execute administrative commands
        administrativeCommand.forEach(c -> {

            if (c.canProcessCommand(messageUpdate, config.botUserId())) {
                if (concat(messageUpdate.getMessage().getText().split(" ")).equals("help")) {
                    reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), c.help(locale)),
                                                 c.deleteMessage(),
                                                 c.deleteMessageTimeout());
                    // delete the command itself
                    if (c.deleteMessage()) {
                        messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                                        messageUpdate.getMessage().getMessageId(),
                                                        c.deleteMessageTimeout());
                    }

                    isAdministrativeCommand = true;
                    return;
                }
                reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(),
                                                         messageUpdate.getMessage().getChat(),
                                                         c.execute(Optional.of(concat(messageUpdate.getMessage().getText().split(" "))), messageUpdate, locale).toString()),
                                             c.deleteMessage(),
                                             c.deleteMessageTimeout());
                // delete the command itself
                if (c.deleteMessage()) {
                    messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                                    messageUpdate.getMessage().getMessageId(),
                                                    c.deleteMessageTimeout());
                }
                isAdministrativeCommand = true;
                return;
            }
        });

        if (apiRepository.isBotEnabled(messageUpdate.getMessage().getChat().getId())) {
            Predicate predicate = messageIsNotNull().and(isCommand());

            if (predicate.test(messageUpdate)) {
                commandProcessor(messageUpdate);
            } else {
                nonCommandProcessor(messageUpdate);
            }
        }
    }

    @Override
    public void commandProcessor(MessageUpdate messageUpdate) {

        final StringBuilder response = new StringBuilder("");
        log.fine("Processing command: " + messageUpdate.getMessage().getText());

        String[] args = messageUpdate.getMessage().getText().split(" ");
        String command2process = args[0].replace("@" + config.botUserId(), "");

        // /help command
        // will delete messages within 10 seconds
        if (command2process.equals("/help")) {
            command.forEach(c -> response.append(c.name() + " - " + c.description(locale) + "\n"));
            administrativeCommand.forEach(ac -> response.append(ac.name() + " - " + ac.description(locale) + "\n"));
            response.append(I18nHelper.resource("Administrative", locale, "internal.help.response"));
            reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), response.toString()),
                                         config.deleteMessages(), config.deleteMessagesAfter());
            // delete the command itself
            if (config.deleteMessages()) {
                messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                                messageUpdate.getMessage().getMessageId(),
                                                config.deleteMessagesAfter());
            }
        }

        command.forEach(command -> {
            if (!apiRepository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), command.name().replace("/", ""))) {
                return;
            } else {
                if (command.canProcessCommand(messageUpdate, config.botUserId())) {
                    if (concat(args).equals("help")) {
                        response.append(command.help(locale));
                    } else {
                        response.append(command.execute(Optional.of(concat(args)), messageUpdate, locale));
                        log.fine("COMMAND_PROCESSOR - Command processed, result is: " + response);
                    }
                    reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), response.toString()),
                                                 command.deleteMessage(), command.deleteMessageTimeout());

                    // delete the command itself
                    if (command.deleteMessage()) {
                        messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                                        messageUpdate.getMessage().getMessageId(),
                                                        command.deleteMessageTimeout());
                    }
                }
            }
        });
        if (response.length() < 1 && !isAdministrativeCommand) {
            log.fine("Command [" + messageUpdate.getMessage().getText() + "] will not to be processed by this bot or is not an administrative command.");
        }
    }

    @Override
    public void nonCommandProcessor(MessageUpdate messageUpdate) {
        log.fine("NON_COMMAND_PROCESSOR - Processing message: " + messageUpdate.getMessage().toString());
        Message message = new Message();
        message.setChat(messageUpdate.getMessage().getChat());
        message.setMessageId(messageUpdate.getMessage().getMessageId());

        plugin.forEach(plugin -> {
            if (!apiRepository.isCommandEnabled(messageUpdate.getMessage().getChat().getId(), plugin.name())) {
                return;
            } else {
                message.setText(plugin.process(messageUpdate, locale));
                try {
                    if (null != message.getText()) {
                        if (message.getText().contains("karma")) {
                            message.setMessageId(0);
                        }
                        reply.processOutgoingMessage(message, plugin.deleteMessage(), plugin.deleteMessageTimeout());
                        // delete the command itself
                        if (plugin.deleteMessage()) {
                            messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                                            messageUpdate.getMessage().getMessageId(),
                                                            plugin.deleteMessageTimeout());
                        }
                    }
                } catch (final Exception e) {
                    log.fine("NON_COMMAND_PROCESSOR - Message not processed by the available plugins.");
                }
            }
        });
    }
}