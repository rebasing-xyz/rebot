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

package xyz.rebasing.rebot;

import java.lang.invoke.MethodHandles;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.api.spi.CommandProvider;
import xyz.rebasing.rebot.api.spi.PluginProvider;
import xyz.rebasing.rebot.api.spi.administrative.AdministrativeCommandProvider;
import xyz.rebasing.rebot.telegram.api.UpdatesReceiver;

@ApplicationScoped
public class Startup {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    Instance<CommandProvider> commands;

    @Inject
    Instance<AdministrativeCommandProvider> administrativeCommand;

    @Inject
    Instance<PluginProvider> plugins;

    @Inject
    UpdatesReceiver receiver;

    void onStart(@Observes StartupEvent ev) {
        log.info("ReBot is starting...");
        // Loading commands
        commands.forEach(command -> command.load());
        administrativeCommand.forEach(command -> command.load());
        // loading plugins
        plugins.forEach(plugin -> plugin.load());
        receiver.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("ReBot is stopping...");
        receiver.interrupt();
    }
}