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

package xyz.rebasing.rebot.service.cache.producer;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import xyz.rebasing.rebot.service.cache.pojo.postal.PostalCode;
import xyz.rebasing.rebot.service.cache.qualifier.BrazilPostalCodeCache;

@ApplicationScoped
public class BrazilPostalCacheProducer {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private DefaultCacheManager defaultCacheManager;

    @Produces
    @BrazilPostalCodeCache
    public Cache<String, PostalCode> returnCache() {
        return postalCodeCacheContainer().getCache();
    }

    @Produces
    public Configuration postalCodeCacheConfiguration() {
        log.info("Configuring brazil-postal-code-cache...");
        return new ConfigurationBuilder()
                .indexing()
                .autoConfig(true)
                .memory()
                .build();
    }

    public EmbeddedCacheManager postalCodeCacheContainer() {
        if (null == defaultCacheManager) {
            GlobalConfiguration g = new GlobalConfigurationBuilder()
                    .nonClusteredDefault()
                    .defaultCacheName("ddd-cache")
                    .globalJmxStatistics()
                    .allowDuplicateDomains(false)
                    .build();
            defaultCacheManager = new DefaultCacheManager(g, postalCodeCacheConfiguration());
        }
        return defaultCacheManager;
    }
}
