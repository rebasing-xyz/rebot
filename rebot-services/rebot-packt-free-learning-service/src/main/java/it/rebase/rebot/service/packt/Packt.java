/*
  The MIT License (MIT)

  Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>

  Permission is hereby granted, free of charge, to any person obtaining a copy of
  this software and associated documentation files (the "Software"), to deal in
  the Software without restriction, including without limitation the rights to
  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
  the Software, and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.rebase.rebot.service.packt;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.service.packt.notifier.PacktNotifier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class Packt implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    PacktNotifier packtNotifier;

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
        packtNotifier.populate(true);
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        if (key.isPresent() && key.get().equals("notify")) return packtNotifier.registerNotification(messageUpdate);
        if (key.isPresent() && key.get().equals("off")) return packtNotifier.unregisterNotification(messageUpdate);
        return packtNotifier.get();
    }

    @Override
    public String name() {
        return "/packt";
    }

    @Override
    public String help() {
        StringBuilder builder = new StringBuilder(this.name() + " - ");
        builder.append("Returns the information about the daily free ebook offered by Packt Publishing\n");
        builder.append("    <code>" + this.name() + "</code> - returns information about the daily free ebook.\n");
        builder.append("    <code>" + this.name() + " notify</code> - Enable notifications for a group or for a private chat. The notifications are sent daily at 23h00.\n");
        builder.append("    <code>" + this.name() + " off</code> - Disable the notifications.");
        return builder.toString();
    }

    @Override
    public String description() {
        return "returns the daily free ebook";
    }
}
