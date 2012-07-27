
package de.egore911.drilog.server;

import com.google.api.server.spi.config.Api;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.egore911.drilog.server.model.EMF;
import de.egore911.drilog.server.model.Monitored;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "preferences")
public class MonitoredEndpoint {

    private static boolean isLoggedIn(User user) {
        return user != null && user.getEmail() != null && !user.getEmail().isEmpty();
    }

    private static User getCurrentUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    /**
     * This method lists all the entities inserted in datastore. It uses HTTP
     * GET method.
     * 
     * @return List of all entities persisted.
     */
    @SuppressWarnings("unchecked")
    public List<Monitored> listMonitored() {
        EntityManager mgr = getEntityManager();
        List<Monitored> result = new ArrayList<Monitored>();
        try {
            User user = getCurrentUser();
            if (isLoggedIn(user)) {
                Query query = mgr.createQuery("select from Monitored where by = :by " +
                        "order by username").setParameter("by", user.getEmail());
                for (Object obj : (List<Object>) query.getResultList()) {
                    result.add(((Monitored) obj));
                }
            }
        } finally {
            closeEntitymanager();
        }
        return result;
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     * 
     * @param id the primary key of the java bean.
     * @return The entity with primary key id.
     */
    public Monitored getMonitored(@Named("id")
    Long id) {
        EntityManager mgr = getEntityManager();
        Monitored monitored = null;
        try {
            User user = getCurrentUser();
            if (isLoggedIn(user)) {
                Query query = mgr.createQuery("select from Monitored where id = :id and by = :by " +
                        "order by username").setParameter("id", id)
                        .setParameter("by", user.getEmail());
                monitored = (Monitored) query.getSingleResult();
            }
        } finally {
            closeEntitymanager();
        }
        return monitored;
    }

    /**
     * This inserts the entity into App Engine datastore. It uses HTTP POST
     * method.
     * 
     * @param monitored the entity to be inserted.
     * @return The inserted entity.
     */
    public Monitored insertMonitored(Monitored monitored) {
        EntityManager mgr = getEntityManager();
        try {
            User user = getCurrentUser();
            if (isLoggedIn(user)) {
                monitored.setBy(user.getEmail());
                mgr.persist(monitored);
            } else {
                return null;
            }
        } finally {
            closeEntitymanager();
        }
        return monitored;
    }

    /**
     * This method is used for updating a entity. It uses HTTP PUT method.
     * 
     * @param monitored the entity to be updated.
     * @return The updated entity.
     */
    public Monitored updateMonitored(Monitored monitored) {
        EntityManager mgr = getEntityManager();
        try {
            User user = getCurrentUser();
            if (isLoggedIn(user)) {
                if (mgr.getReference(Monitored.class, monitored.getId()).getBy()
                        .equals(user.getEmail())) {
                    mgr.persist(monitored);
                }
            }
        } finally {
            closeEntitymanager();
        }
        return monitored;
    }

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     * 
     * @param id the primary key of the entity to be deleted.
     * @return The deleted entity.
     */
    public Monitored removeMonitored(@Named("id")
    Long id) {
        EntityManager mgr = getEntityManager();
        Monitored monitored = null;
        try {
            User user = getCurrentUser();
            if (isLoggedIn(user)) {
                Query query = mgr.createQuery("select from Monitored where id = :id and by = :by " +
                        "order by username").setParameter("id", id)
                        .setParameter("by", user.getEmail());
                monitored = (Monitored) query.getSingleResult();
            }
            mgr.remove(monitored);
        } finally {
            closeEntitymanager();
        }
        return monitored;
    }

    private EntityManager created = null;

    private EntityManager getEntityManager() {
        if (EntityManagerFilter.getEntityManager() != null) {
            return EntityManagerFilter.getEntityManager();
        }
        created = EMF.get().createEntityManager();
        return created;
    }

    private void closeEntitymanager() {
        if (created != null) {
            created.close();
        }
    }

}
