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

package it.rebase.rebot.api.conf.systemproperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * {@link BotProperty} String producer
 */
@ApplicationScoped
public class BotPropertyProducerBean {

    Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().toString());

    @Produces
    @Dependent
    @BotProperty(name = "")
    public String findBotProperty(InjectionPoint injectionPoint) {
        BotProperty prop = injectionPoint.getAnnotated().getAnnotation(BotProperty.class);
        String property = System.getProperty(prop.name());
        log.fine("Injecting Property name: [" + prop.name() + "] value: [" + property + "] required [" + prop.required() + "]");
        if (prop.required() && (null == property) || property=="") {
            throw new IllegalStateException("The parameter " + prop.name() + " is required!");
        }
        return property;
    }
}