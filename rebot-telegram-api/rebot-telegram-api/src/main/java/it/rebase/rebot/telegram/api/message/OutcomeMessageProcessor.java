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

package it.rebase.rebot.telegram.api.message;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.api.management.message.MessageManagement;
import it.rebase.rebot.api.message.sender.MessageSender;
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;
import it.rebase.rebot.service.persistence.repository.ApiRepository;
import it.rebase.rebot.service.persistence.repository.LocaleRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static it.rebase.rebot.telegram.api.filter.ReBotPredicate.isCommand;
import static it.rebase.rebot.telegram.api.filter.ReBotPredicate.messageIsNotNull;
import static it.rebase.rebot.telegram.api.utils.StringUtils.concat;

@ApplicationScoped
public class OutcomeMessageProcessor implements Processor {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private boolean isAdministrativeCommand = false;

    private String locale;

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.token", required = true)
    String botTokenId;
    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String botUserId;
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

            if (c.canProcessCommand(messageUpdate, botUserId)) {
                if (concat(messageUpdate.getMessage().getText().split(" ")).equals("help")) {
                    reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), c.help(locale)),
                            c.removeMessage(),
                            c.deleteMessageTimeout());
                    // delete the command itself
                    if (c.removeMessage()) {
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
                        c.removeMessage(),
                        c.deleteMessageTimeout());
                // delete the command itself
                if (c.removeMessage()) {
                    messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                            messageUpdate.getMessage().getMessageId(),
                            c.deleteMessageTimeout());
                }
                isAdministrativeCommand = true;
                return;
            }
        });

        if (apiRepository.isEnabled(messageUpdate.getMessage().getChat().getId())) {
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
        String command2process = args[0].replace("@" + botUserId, "");

        // /help command
        // will delete messages within 10 seconds
        if (command2process.equals("/help")) {
            command.forEach(c -> response.append(c.name() + " - " + c.description(locale) + "\n"));
            administrativeCommand.forEach(ac -> response.append(ac.name() + " - " + ac.description(locale) + "\n"));
            response.append(I18nHelper.resource("Administrative", locale, "internal.help.response"));
            reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), response.toString()),
                    true, 10);
            // delete the command itself
            messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                    messageUpdate.getMessage().getMessageId(),
                    10);

        }

        command.forEach(command -> {
            if (command.canProcessCommand(messageUpdate, botUserId)) {
                if (concat(args).equals("help")) {
                    response.append(command.help(locale));
                } else {
                    response.append(command.execute(Optional.of(concat(args)), messageUpdate, locale));
                    log.fine("COMMAND_PROCESSOR - Command processed, result is: " + response);
                }
                reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), response.toString()),
                        command.removeMessage(), command.deleteMessageTimeout());

                // delete the command itself
                if (command.removeMessage()){
                    messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                            messageUpdate.getMessage().getMessageId(),
                            command.deleteMessageTimeout());
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
            message.setText(plugin.process(messageUpdate, locale));
            try {
                if (null != message.getText()) {
                    if (message.getText().contains("karma")) {
                        message.setMessageId(0);
                    }
                    reply.processOutgoingMessage(message, plugin.removeMessage(), plugin.deleteMessageTimeout());
                    // delete the command itself
                    if (plugin.removeMessage()){
                        messageManagement.deleteMessage(messageUpdate.getMessage().getChat().getId(),
                                messageUpdate.getMessage().getMessageId(),
                                plugin.deleteMessageTimeout());
                    }
                }
            } catch (final Exception e) {
                log.fine("NON_COMMAND_PROCESSOR - Message not processed by the available plugins.");
            }
        });
    }
}