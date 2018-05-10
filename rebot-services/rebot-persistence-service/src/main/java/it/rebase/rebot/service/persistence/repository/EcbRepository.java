package it.rebase.rebot.service.persistence.repository;

import it.rebase.rebot.service.persistence.pojo.Cubes;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Transactional
@ApplicationScoped
public class EcbRepository {

    private Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @PersistenceContext(unitName = "rebotPU")
    private EntityManager em;

    public void persist(Cubes cubes) {
        if (!exists(cubes.getClass(), cubes.getTime())) {
            log.fine("Persisting cubes: " + cubes.toString());
            em.persist(cubes);
            em.flush();
            log.fine("Currencies successfully imported.");
        } else {
            log.warning("Currencies for date [" + cubes.getTime() + "] already exists.");
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