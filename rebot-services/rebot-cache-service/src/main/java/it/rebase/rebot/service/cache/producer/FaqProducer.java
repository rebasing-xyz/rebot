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

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import it.rebase.rebot.service.cache.pojo.faq.Project;
import it.rebase.rebot.service.cache.qualifier.FaqCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

@ApplicationScoped
public class FaqProducer {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Produces
    @FaqCache
    public Cache<String, Project> returnCache() {
        return faqCacheContainer().getCache();
    }

    @Produces
    public Configuration faqCacheConfiguration() {
        log.info("Configuring faq-cache...");
        return new ConfigurationBuilder()
                .indexing()
                .autoConfig(true)
                .addIndexedEntity(Project.class)
                .memory()
                .build();
    }

    public EmbeddedCacheManager faqCacheContainer() {
        GlobalConfiguration g = new GlobalConfigurationBuilder()
                .nonClusteredDefault()
                .defaultCacheName("faq-cache")
                .globalJmxStatistics()
                .allowDuplicateDomains(false)
                .build();
        return new DefaultCacheManager(g, faqCacheConfiguration());
    }
}
