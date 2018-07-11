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


package it.rebase.rebot;

import it.rebase.rebot.api.conf.systemproperties.BotProperty;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.api.spi.PluginProvider;
import it.rebase.rebot.api.spi.administrative.AdministrativeCommandProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@Singleton
@javax.ejb.Startup
public class Startup {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @BotProperty(name = "it.rebase.rebot.telegram.userId", required = true)
    String userId;

    @Inject
    private ReBot rebot;

    @Inject
    private Instance<CommandProvider> commands;

    @Inject
    private Instance<AdministrativeCommandProvider> administrativeCommand;

    @Inject
    private Instance<PluginProvider> plugins;

    @PostConstruct
    public void startupTasks() {
        try {
            // Loading commands
            commands.forEach(command -> command.load());
            administrativeCommand.forEach(command -> command.load());
            // loading plugins
            plugins.forEach(plugin -> plugin.load());
            rebot.start();
            log.info(userId + " successfully started.");
        } catch (final Exception e) {
            log.severe("Failed to start the " + userId + ": " + e.getMessage());
            System.exit(1);
        }
    }

    @PreDestroy
    public void shutdown() {
        rebot.stop();
    }

}