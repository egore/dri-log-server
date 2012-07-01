
package de.egore911.drilog.server.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EntityManagerFactoryHolder {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("transactions-optional");

    private EntityManagerFactoryHolder() {
    }

    public static EntityManagerFactory getInstance() {
        return emf;
    }
}
