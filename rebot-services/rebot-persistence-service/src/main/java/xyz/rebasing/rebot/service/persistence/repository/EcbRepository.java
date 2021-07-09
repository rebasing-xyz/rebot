package xyz.rebasing.rebot.service.persistence.repository;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import xyz.rebasing.rebot.service.persistence.pojo.Cubes;

@Transactional
@ApplicationScoped
public class EcbRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Inject
    EntityManager em;

    public void persist(Cubes cubes) {
        if (!exists(cubes.getClass(), cubes.getTime())) {
            log.debugv("Persisting cubes: {0}", cubes.toString());
            em.persist(cubes);
            em.flush();
            log.debug("Currencies successfully imported.");
        } else {
            log.warnv("Currencies for date [{0}] already exists.", cubes.getTime());
        }
    }

    public List<Cubes> listCubes(Optional<String> key) {
        CriteriaQuery<Cubes> criteria = em.getCriteriaBuilder().createQuery(Cubes.class);
        criteria.select(criteria.from(Cubes.class));
        if (!key.isPresent()) {
            return em.createQuery(criteria).getResultList();
        } else {
            Query q = em.createNativeQuery("SELECT time FROM ECB_CUBES where time='" + key + "'");
            return (List<Cubes>) q.getResultList();
        }
    }

    private boolean exists(Class clazz, String key) {
        try {
            return em.find(clazz, key) != null;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}