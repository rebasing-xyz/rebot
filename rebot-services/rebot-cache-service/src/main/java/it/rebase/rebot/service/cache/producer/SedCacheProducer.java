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

import it.rebase.rebot.service.cache.qualifier.SedCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@ApplicationScoped
public class SedCacheProducer {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private DefaultCacheManager defaultCacheManager;

    @Produces
    @SedCache
    public Cache<Long, String> returnCache() {
        return sedCacheContainer().getCache();
    }

    @Produces
    public Configuration sedCacheProducer() {
        log.info("Configuring sed-cache...");
        return new ConfigurationBuilder()
                .indexing()
                .autoConfig(true)
                .memory()
                .size(1000)
                .build();
    }

    public EmbeddedCacheManager sedCacheContainer() {
        if (null == defaultCacheManager) {
            GlobalConfiguration g = new GlobalConfigurationBuilder()
                    .nonClusteredDefault()
                    .defaultCacheName("sed-cache")
                    .globalJmxStatistics()
                    .allowDuplicateDomains(false)
                    .build();
            defaultCacheManager = new DefaultCacheManager(g, sedCacheProducer());
        }
        return defaultCacheManager;
    }

}
