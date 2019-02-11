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
import it.rebase.rebot.api.object.Message;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;
import it.rebase.rebot.telegram.api.UpdatesReceiver;
import it.rebase.rebot.telegram.api.message.sender.MessageSender;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class OutcomeMessageProcessor implements Processor {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

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
    private UpdatesReceiver updatesReceiver;

    @Override
    public void process(MessageUpdate messageUpdate) {
        // before proceed with other commands/plugins execute administrative commands
        administrativeCommand.forEach(c -> {
            if (c.canProcessCommand(messageUpdate, botUserId)) {
                if (concat(messageUpdate.getMessage().getText().split(" ")).equals("help")) {
                    reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), c.help()));
                    return;
                }
                reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), c.execute(null, messageUpdate).toString()));
                return;
            }
        });

        if (updatesReceiver.isEnabled()) {
            if (null !=messageUpdate.getMessage().getText() && isCommand(messageUpdate.getMessage().getText())) {
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

        if (command2process.equals("/help")) {
            command.forEach(c -> response.append(c.name() + " - " + c.description() + "\n"));
            administrativeCommand.forEach(ac -> response.append(ac.name() + " - " + ac.description() + "\n"));
            response.append("\n&#60;command&#62; help: returns the command's help.");
        }
        command.forEach(command -> {
            if (command.canProcessCommand(messageUpdate, botUserId)) {
                if (concat(args).equals("help")) {
                    response.append(command.help());
                } else {
                    response.append(command.execute(Optional.of(concat(args)), messageUpdate));
                    log.fine("COMMAND_PROCESSOR - Command processed, result is: " + response);
                }
            }
        });
        if (response.length() < 1) {
            log.fine("Command [" + messageUpdate.getMessage().getText() + "] will not to be processed by this bot or is not an administrative command.");
        }
        reply.processOutgoingMessage(new Message(messageUpdate.getMessage().getMessageId(), messageUpdate.getMessage().getChat(), (response.toString())));
    }

    @Override
    public void nonCommandProcessor(MessageUpdate messageUpdate) {
        log.fine("NON_COMMAND_PROCESSOR - Processing message: " + messageUpdate.getMessage().toString());
        Message message = new Message();
        message.setChat(messageUpdate.getMessage().getChat());
        message.setMessageId(messageUpdate.getMessage().getMessageId());

        plugin.forEach(plugin -> {
            message.setText(plugin.process(messageUpdate));
            try {
                if (null != message.getText()) {
                    if (message.getText().contains("karma")) message.setMessageId(0);
                    reply.processOutgoingMessage(message);
                }
            } catch (final Exception e) {
                log.fine("NON_COMMAND_PROCESSOR - Message not processed by the available plugins.");
            }
        });
    }

    private boolean isCommand(String text) {
        return text.startsWith("/");
    }

    /**
     * Parse the parameters received into a single String and make it lower case
     *
     * @param parameters
     * @return the formatted string
     */
    private String concat(String... parameters) {
        String result = "";
        for (int i = 1; i < parameters.length; i++) {
            if (parameters.length > i + 1) {
                result += parameters[i] + " ";
            } else {
                result += parameters[i];
            }
        }
        return result.toLowerCase();
    }

}