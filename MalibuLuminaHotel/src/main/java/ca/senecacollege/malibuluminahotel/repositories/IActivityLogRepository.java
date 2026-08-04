package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.ActivityLog;
import ca.senecacollege.malibuluminahotel.models.AdminUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ActivityLog entity operations.
 */
public interface IActivityLogRepository {

    /**
     * Save activity log entry.
     */
    ActivityLog save(ActivityLog log);

    /**
     * Find activity log by ID.
     */
    Optional<ActivityLog> findById(Long id);

    /**
     * Find all activity logs.
     */
    List<ActivityLog> findAll();

    /**
     * Find activity logs by admin user.
     */
    List<ActivityLog> findByAdminUser(AdminUser adminUser);

    /**
     * Find activity logs within a time range.
     */
    List<ActivityLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find activity logs by action type.
     */
    List<ActivityLog> findByAction(String action);

    /**
     * Find recent activity logs (limit).
     */
    List<ActivityLog> findRecent(int limit);

    /**
     * Delete activity log.
     */
    void delete(ActivityLog log);
}
