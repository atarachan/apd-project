package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AdminUser entity operations.
 * Extends base repository functionality with authentication-specific queries.
 * 
 * @author Malibu Lumina Hotel Team
 * @version 1.0
 */
public interface IAdminUserRepository extends IRepository<AdminUser, Long> {

    /**
     * Find an admin user by username (case-insensitive).
     * Used for authentication.
     * 
     * @param username The username to search for
     * @return Optional containing the AdminUser if found
     */
    Optional<AdminUser> findByUsername(String username);

    /**
     * Find all admin users with a specific role.
     * Useful for admin management features.
     * 
     * @param role The role to filter by
     * @return List of AdminUsers with the specified role
     */
    List<AdminUser> findByRole(UserRole role);

    /**
     * Find all active admin users.
     * 
     * @return List of active AdminUsers
     */
    List<AdminUser> findAllActive();
}
