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

package it.rebase.rebot.service.persistence.repository;

import it.rebase.rebot.service.persistence.pojo.Karma;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Transactional
@ApplicationScoped
public class KarmaRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    EntityManager em;

    public int get(String key) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<String> karmaPoints = criteriaBuilder.createQuery(String.class);
            Root<Karma> karma = karmaPoints.from(Karma.class);
            karmaPoints.select(karma.get("points")).where(criteriaBuilder.equal(karma.get("username"), key));
            return Integer.parseInt(em.createQuery(karmaPoints).getSingleResult());
        } catch (final Exception e) {
            log.fine("get() - There is no karma for [" + key + "]");
            return 0;
        }
    }

    public List<Karma> list(String key) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Karma> karmaQuery = criteriaBuilder.createQuery(Karma.class);
            Root<Karma> karma = karmaQuery.from(Karma.class);
            karmaQuery.select(karma)
                    .where(criteriaBuilder.like(karma.get("username"), key))
                    .orderBy(criteriaBuilder.asc(karma.get("username")));
            return em.createQuery(karmaQuery).getResultList();

        } catch (final Exception e) {
            log.fine("list() - There is no karma for [" + key + "]");
            return Arrays.asList(new Karma(key, "0"));
        }
    }

    public void updateOrCreateKarma(Karma karma) {
        log.fine("Updating the karma for [" + karma.toString() + "]");
        try {
            em.merge(karma);
            em.flush();
        } catch (final Exception e) {
            log.warning("Failed to persist object [" + karma.toString() + "]: " + e.getMessage());
        }
    }

}
