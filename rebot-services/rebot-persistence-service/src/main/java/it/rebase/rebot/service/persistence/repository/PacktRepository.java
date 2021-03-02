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

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import it.rebase.rebot.api.i18n.I18nHelper;
import it.rebase.rebot.service.persistence.pojo.PacktNotification;
import org.hibernate.exception.ConstraintViolationException;

@Transactional
@ApplicationScoped
public class PacktRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    EntityManager em;

    public String register(PacktNotification packtNotification) {
        try {
            em.persist(packtNotification);
            em.flush();
            return String.format(
                    I18nHelper.resource("Common", packtNotification.getLocale(), "packt.notification.created"),
                    packtNotification.getChannel());
        } catch (final Exception e) {
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                if (t.getClass().toString().contains("ConstraintViolationException")) {
                    return String.format(
                            I18nHelper.resource("Common", packtNotification.getLocale(), "packt.notification.already.exists"),
                            packtNotification.getChannel());
                }
            }
            return String.format(
                    I18nHelper.resource("Common", packtNotification.getLocale(), "packt.notification.fail.to.create"),
                    e.getCause());
        }
    }

    public String unregister(PacktNotification packtNotification) {
        try {
            em.remove(em.merge(packtNotification));
            em.flush();
            return String.format(
                    I18nHelper.resource("Common", packtNotification.getLocale(), "packt.notification.removed"),
                    packtNotification.getChannel());
        } catch (final Exception e) {
            log.warning("Failed to remove the notification: " + e.getMessage());
            return String.format(
                    I18nHelper.resource("Common", packtNotification.getLocale(), "packt.notification.fail.to.remove"),
                    e.getCause());
        }
    }

    public List<PacktNotification> get() {
        try {
            CriteriaQuery<PacktNotification> criteria = em.getCriteriaBuilder().createQuery(PacktNotification.class);
            criteria.select(criteria.from(PacktNotification.class));
            return em.createQuery(criteria).getResultList();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}