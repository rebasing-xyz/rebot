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

package it.rebase.rebot.service.persistence.repository;

import it.rebase.rebot.service.persistence.pojo.BotStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@Transactional
@ApplicationScoped
public class ApiRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @PersistenceContext(unitName = "rebotPU")
    private EntityManager em;

    /**
     * Persist the bot state in the database
     * @param botStatus {@link BotStatus}
     */
    public void persist(BotStatus botStatus) {
        log.fine("Persisting " + botStatus.toString());
        em.persist(botStatus);
        em.flush();
    }

    /**
     * @return if the bot is enabled or not
     * In case there is no state saved return true.
     */
    public boolean isEnabled() {
        try {
            Query q = em.createNativeQuery("SELECT isEnabled from BOT_STATUS where id=LAST_INSERT_ID();");
            return (boolean) q.getSingleResult();
        } catch (final Exception e) {
            return true;
        }
    }
}