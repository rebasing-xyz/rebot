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

package xyz.rebasing.rebot.service.persistence.repository;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.service.persistence.domain.ChatLocale;

@Transactional
@ApplicationScoped
public class LocaleRepository {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final String DEFAULT_LOCALE = "en_US";

    @Inject
    EntityManager em;

    public String get(long chatId, String chatTitle) {
        try {
            return em.createNamedQuery("ChatLocale.Get", ChatLocale.class)
                    .setParameter("chatId", chatId)
                    .getSingleResult().getChatLocale();
        } catch (final Exception e) {
            e.printStackTrace();
            log.debugv("get() - There is no locale for  chat [{0}], defaulting to {1}", chatId, DEFAULT_LOCALE);
            this.persistChatLocale(new ChatLocale(chatId, chatTitle, DEFAULT_LOCALE));
            return DEFAULT_LOCALE;
        }
    }

    public List<ChatLocale> getRegisteredChatLocale() {
        CriteriaQuery<ChatLocale> criteria = em.getCriteriaBuilder().createQuery(ChatLocale.class);
        criteria.select(criteria.from(ChatLocale.class));
        return em.createQuery(criteria).getResultList();
    }

    public String persistChatLocale(ChatLocale chatLocale) {
        try {
            log.debugv("Persisting {0}", chatLocale.toString());
            em.merge(chatLocale);
            em.flush();
        } catch (final Exception e) {
            return "failed to persist locale " + chatLocale.toString();
        }
        return "persisted";
    }
}