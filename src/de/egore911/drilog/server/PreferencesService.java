
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

    public List<Monitored> getMonitored() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            EntityManager em = EntityManagerFactoryHolder.getInstance().createEntityManager();
            try {
                @SuppressWarnings("unchecked")
                List<Monitored> result = em
                        .createQuery("from Monitored where by = :by " +
                                "order by username")
                        .setParameter("by", user.getEmail())
                        .getResultList();
                return result;
            } finally {
                em.close();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
