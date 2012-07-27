
package de.egore911.drilog.server;

import de.egore911.drilog.server.model.EMF;

import java.io.IOException;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EntityManagerFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(EntityManagerFilter.class.getName());

    private static final ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<EntityManager>();

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Nothing
    }

    @Override
    public void destroy() {
        // Nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOG.finest("Create entitymanager");
        entityManagerHolder.set(EMF.get().createEntityManager());
        try {
            chain.doFilter(request, response);
        } finally {
            entityManagerHolder.get().close();
            entityManagerHolder.set(null);
            LOG.finest("Closed entitymanager");
        }
    }

    public static EntityManager getEntityManager() {
        return entityManagerHolder.get();
    }

}
