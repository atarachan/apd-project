package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.models.ActivityLog;
import ca.senecacollege.malibuluminahotel.models.AdminUser;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA implementation of ActivityLog repository.
 */
public class ActivityLogRepositoryImpl extends AbstractRepository<ActivityLog, Long> implements IActivityLogRepository {

    public ActivityLogRepositoryImpl() {
        super(ActivityLog.class);
    }

    @Override
    public List<ActivityLog> findByAdminUser(AdminUser adminUser) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<ActivityLog> query = em.createQuery(
                    "SELECT a\n" +
                            "FROM ActivityLog a\n" +
                            "WHERE a.adminUser = :admin\n" +
                            "ORDER BY a.timestamp DESC",
                    ActivityLog.class);
            query.setParameter("admin", adminUser);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ActivityLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<ActivityLog> query = em.createQuery(
                    "SELECT a FROM ActivityLog a " +
                            "LEFT JOIN FETCH a.adminUser " +
                            "WHERE a.timestamp >= :start AND a.timestamp <= :end " +
                            "ORDER BY a.timestamp DESC",
                    ActivityLog.class);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ActivityLog> findByAction(String action) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<ActivityLog> query = em.createQuery(
                    "SELECT a\n" +
                            "FROM ActivityLog a\n" +
                            "WHERE a.action = :action\n" +
                            "ORDER BY a.timestamp DESC",
                    ActivityLog.class);
            query.setParameter("action", action);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ActivityLog> findRecent(int limit) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<ActivityLog> query = em.createQuery(
                    "SELECT a FROM ActivityLog a ORDER BY a.timestamp DESC",
                    ActivityLog.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
