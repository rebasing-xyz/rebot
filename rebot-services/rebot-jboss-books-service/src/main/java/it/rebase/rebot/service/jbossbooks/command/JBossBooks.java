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

package it.rebase.rebot.service.jbossbooks.command;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.service.jbossbooks.JBossBooksService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class JBossBooks implements CommandProvider {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    private JBossBooksService service;

    @Override
    public void load() {
        log.fine("Loading command " + this.name());
        service.initialize();
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        StringBuilder response = new StringBuilder();
        try {
            service.getBooks().stream()
                    .filter(book -> book.isPublic())
                    .forEach(b -> {
                        response.append("<pre>" + b.getTitle() + "</pre>");
                        response.append(" - ");
                        response.append("<a href=\"" +  b.getUrls().getRead() + "\">Read</a> / ");
                        response.append("<a href=\"" + b.getUrls().getDownload().getPdf() + "\">Download</a>");
                        response.append("\n");
                    });
        } catch (final Exception e) {
            log.warning("Failed to execute the command [" + this.name() + "]: " + e.getMessage());
        }
        return response.toString();
    }

    @Override
    public String name() {
        return "/books";
    }

    @Override
    public String help() {
        return this.name() + " - List the available books on https://www.gitbook.com/@jboss-book";
    }

    @Override
    public String description() {
        return "list all available books on https://www.gitbook.com/@jboss-books";
    }
}
