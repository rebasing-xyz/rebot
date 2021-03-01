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

package it.rebase.rebot.plugin.chuck;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import it.rebase.rebot.api.conf.BotConfig;
import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.plugin.chuck.utils.Utils;
import it.rebase.rebot.service.persistence.pojo.Fact;
import it.rebase.rebot.service.persistence.repository.ChuckRepository;

@ApplicationScoped
public class ChuckNorris implements PluginProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final Pattern FULL_MSG_PATTERN = Pattern.compile("(chuck norris)");

    @Inject
    BotConfig config;

    @Inject
    ChuckRepository chuckRepository;

    Fact fact = new Fact();

    @Override
    public void load() {
        log.fine("Enabling Chuck Norris plugin.");
    }

    @Override
    public String name() {
        return "chuck-norris";
    }

    @Override
    public String process(MessageUpdate update, String locale) {
        if (canProcess(update.getMessage().getText())) {
            StringBuilder response = new StringBuilder();

            fact = Utils.getFact();
            response.append(fact.getValue());

            // Persist chuck fact Asynchronously
            new Thread(() -> chuckRepository.persisteChuckFact(fact)).start();
            return response.toString();
        }
        return null;
    }

    @Override
    public boolean deleteMessage() {
        return config.deleteMessages();
    }

    @Override
    public long deleteMessageTimeout() {
        return config.deleteMessagesAfter();
    }

    /**
     * Verifies if the received text can be processed by this plugin
     *
     * @param messageContent
     * @return true if the message matches the chuck plugin pattern, otherwise returns false
     */
    private boolean canProcess(String messageContent) {
        boolean canProcess = null == messageContent ? false : FULL_MSG_PATTERN.matcher(messageContent.toLowerCase()).find();
        log.fine("Chuck Norris plugin - can process [" + messageContent + "] - " + canProcess);
        return canProcess;
    }
}