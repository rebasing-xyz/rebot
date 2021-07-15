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

package xyz.rebasing.rebot.plugin.karma.listener;

import java.lang.invoke.MethodHandles;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.jboss.logging.Logger;
import xyz.rebasing.rebot.service.cache.qualifier.KarmaCache;
import xyz.rebasing.rebot.service.persistence.pojo.Karma;
import xyz.rebasing.rebot.service.persistence.repository.KarmaRepository;

@Listener
public class KarmaEventListener {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    public KarmaRepository karma;

    /**
     * Each new item added in the {@link KarmaCache} will trigger a event that will add/update the karma points for the given key
     * in the persistence layer {@link KarmaRepository}
     *
     * @param event {@link CacheEntryCreatedEvent}
     */
    @CacheEntryCreated
    public void entryCreated(@Observes CacheEntryCreatedEvent event) {
        if (event.getValue() != null) {
            try {
                karma.updateOrCreateKarma(new Karma(event.getKey().toString().split(":")[0], event.getValue().toString()));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update the karma points of the given key in the database, any event that update values on {@link KarmaCache} will trigger this event
     *
     * @param event {@link CacheEntryModifiedEvent}
     */
    @CacheEntryModified
    public void entryModified(@Observes CacheEntryModifiedEvent event) {
        try {
            karma.updateOrCreateKarma(new Karma(event.getKey().toString().split(":")[0], event.getValue().toString()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For each expired cache entry that had its timeout hit will trigger this event and a message will be printed in the logs.
     *
     * @param event {@link CacheEntryExpiredEvent}
     */
    @CacheEntryExpired
    public void entryExpired(@Observes CacheEntryExpiredEvent event) {
        log.debugv("CacheEntryExpired {0} {1}", event.getKey(), event.getValue().toString());
    }

    /**
     * Any removed cache entry will trigger this event, its function is only notify by printing information on the logs about
     * which entry was removed from {@link KarmaCache}
     *
     * @param event {@link CacheEntryRemovedEvent}
     */
    @CacheEntryRemoved
    public void entryRemoved(@Observes CacheEntryRemovedEvent event) {
        log.debugv("entry {0} removed from the cache.", event.getKey());
    }
}
