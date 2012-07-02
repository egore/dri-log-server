
package de.egore911.drilog.server;

import com.google.api.server.spi.config.Api;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.egore911.drilog.server.model.EntityManagerFactoryHolder;
import de.egore911.drilog.server.model.Monitored;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

@Api(name = "preferences")
public class PreferencesService {

    private static EntityManager getEntityManager() {
        return EntityManagerFactoryHolder.getInstance().createEntityManager();
    }

    private static boolean isLoggedIn(User user) {
        return user != null && user.getEmail() != null && !user.getEmail().isEmpty();
    }

    private static User getCurrentUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    public List<Monitored> getMonitoreds() {
        User user = getCurrentUser();
        if (isLoggedIn(user)) {
            EntityManager em = getEntityManager();
            try {
                @SuppressWarnings("unchecked")
                List<Monitored> result = em
                        .createQuery("select from Monitored where by = :by " +
                                "order by username")
                        .setParameter("by", user.getEmail())
                        .getResultList();
                // XXX Lazy init workaround
                for (Monitored m : result) {
                    m.getUsername();
                }
                return result;
            } finally {
                em.close();
            }
        } else {
            return Collections.emptyList();
        }
    }

    public Monitored getMonitored(Long id) {
        User user = getCurrentUser();
        if (isLoggedIn(user)) {
            EntityManager em = getEntityManager();
            try {
                Monitored monitored = em.getReference(Monitored.class, id);
                if (user.getEmail().equals(monitored.getBy())) {
                    return monitored;
                }
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean addMonitored(Monitored monitored) {
        User user = getCurrentUser();
        if (isLoggedIn(user)) {
            monitored.setBy(user.getEmail());
            EntityManager em = getEntityManager();
            try {
                Integer count = (Integer) em
                        .createQuery("select count() from Monitored where by = :by " +
                                "and username = :username")
                        .setParameter("by", user.getEmail())
                        .setParameter("username", monitored.getUsername())
                        .getSingleResult();
                if (count.intValue() == 0) {
                    em.persist(monitored);
                    return true;
                }
            } finally {
                em.close();
            }
        }
        return false;
    }

    public void removeMonitored(Monitored monitored) {
        User user = getCurrentUser();
        if (isLoggedIn(user)) {
            EntityManager em = getEntityManager();
            try {
                em.remove(monitored);
            } finally {
                em.close();
            }
        }
    }
}
