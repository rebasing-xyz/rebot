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

package it.rebase.rebot.service.cache.producer;

import it.rebase.rebot.service.cache.qualifier.FaqCache;
import org.infinispan.cdi.embedded.ConfigureCache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@ApplicationScoped
public class FaqProducer {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Produces
    @ConfigureCache("faq-cache")
    @FaqCache()
    public Configuration specialCacheCfg(InjectionPoint injectionPoint) throws ClassNotFoundException, NoSuchMethodException {
        log.info("Configuring faq-cache...");
        Class classToIndex = null;
        String defaultValue = (String) FaqCache.class.getMethod("classToIndex").getDefaultValue();
        try {
            classToIndex = Class.forName(injectionPoint.getAnnotated().getAnnotation(FaqCache.class).classToIndex());
        } catch (final Exception e) {
            log.warning("Failed to lookup the class: " + classToIndex + " --> " + e.getMessage());
            log.warning("Configuring classToIndex to [" + defaultValue + "]");
            classToIndex = Class.forName(defaultValue);
        }
        return new ConfigurationBuilder()
                .indexing()
                .autoConfig(true)
                .addIndexedEntity(classToIndex)
                .addProperty("default.directory_provider", "ram")
                .build();
    }
}
