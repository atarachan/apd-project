package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AdminUser repository.
 * Provides database operations for admin user management and authentication.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public class AdminUserRepositoryImpl extends AbstractRepository<AdminUser, Long> implements IAdminUserRepository {

    public AdminUserRepositoryImpl() {
        super(AdminUser.class);
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT a
                            FROM AdminUser a
                            WHERE LOWER(a.username) = LOWER(:username)
                            """,
                            AdminUser.class
                    )
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public List<AdminUser> findByRole(UserRole role) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT a
                            FROM AdminUser a
                            WHERE a.role = :role
                            ORDER BY a.username
                            """,
                            AdminUser.class
                    )
                    .setParameter("role", role)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<AdminUser> findAllActive() {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT a
                            FROM AdminUser a
                            WHERE a.isActive = true
                            ORDER BY a.username
                            """,
                            AdminUser.class
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
