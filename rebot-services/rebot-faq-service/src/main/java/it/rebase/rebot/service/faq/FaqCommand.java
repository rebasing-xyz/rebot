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

package it.rebase.rebot.service.faq;

import it.rebase.rebot.api.object.MessageUpdate;
import it.rebase.rebot.api.spi.CommandProvider;
import it.rebase.rebot.service.cache.qualifier.FaqCache;
import it.rebase.rebot.service.faq.pojo.Project;
import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class FaqCommand implements CommandProvider {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    @Any
    FaqService service;

    @Inject
    @FaqCache()
    private Cache<String, Project> cache;

    public void load() {
        log.fine("loading command  " + this.name());
        service.populateCache();
    }

    @Override
    public Object execute(Optional<String> key, MessageUpdate messageUpdate) {
        return key.get().length() > 0 ? service.query(key.get()) : "Paramenter required, " + this.name() + " help.";
    }

    @Override
    public String name() {
        return "/faq";
    }

    @Override
    public String help() {
        StringBuilder strBuilder = new StringBuilder("/faq - ");
        strBuilder.append("Search Open Source projects registered on ReBot.\n");
        strBuilder.append("Example: <a href=\"/faq hibernate\">/faq hibernate</a>.");
        return strBuilder.toString();
    }

    @Override
    public String description() {
        return "list the information about the given project";
    }

}