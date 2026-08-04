package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.ActivityLog;
import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.repositories.IActivityLogRepository;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing activity log operations.
 * Provides audit trail for admin actions.
 */
public class ActivityLogService implements IActivityLogService {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogService.class);
    private final IActivityLogRepository activityLogRepository;

    @Inject
    public ActivityLogService(IActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Log an admin action.
     * 
     * @param adminUser  The admin performing the action
     * @param action     The action type (e.g., "checkout", "discount_applied")
     * @param entityType The entity type (e.g., "Reservation", "Guest")
     * @param entityId   The entity ID
     * @param message    Descriptive message
     * @return The saved activity log
     */
    @Override
    public ActivityLog log(AdminUser adminUser, String action, String entityType, Long entityId, String message) {
        logger.debug("Logging action: {} by admin: {}", action, adminUser.getUsername());

        ActivityLog log = new ActivityLog(adminUser, action, entityType, entityId, message);
        log.record(); // Set timestamp

        ActivityLog saved = activityLogRepository.save(log);
        logger.info("Activity logged: ID {}, Action: {}", saved.getLogId(), action);

        return saved;
    }

    /**
     * Log an action related to a reservation.
     */
    @Override
    public ActivityLog logReservationAction(AdminUser adminUser, Reservation reservation, String action,
            String message) {
        logger.debug("Logging reservation action: {} for reservation ID: {}", action, reservation.getReservationId());

        ActivityLog log = new ActivityLog(adminUser, reservation, action, message);
        log.record();

        return activityLogRepository.save(log);
    }

    /**
     * Get all activity logs.
     */
    @Override
    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAll();
    }

    /**
     * Get activity logs for a specific admin user.
     */
    @Override
    public List<ActivityLog> getLogsByAdmin(AdminUser adminUser) {
        return activityLogRepository.findByAdminUser(adminUser);
    }

    /**
     * Get activity logs within a time range.
     */
    @Override
    public List<ActivityLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return activityLogRepository.findByTimestampBetween(start, end);
    }

    /**
     * Get activity logs by action type.
     */
    @Override
    public List<ActivityLog> getLogsByAction(String action) {
        return activityLogRepository.findByAction(action);
    }

    /**
     * Get recent activity logs.
     */
    @Override
    public List<ActivityLog> getRecentLogs(int limit) {
        return activityLogRepository.findRecent(limit);
    }

    /**
     * Log reservation creation.
     */
    @Override
    public ActivityLog logReservationCreated(AdminUser adminUser, Reservation reservation) {
        return logReservationAction(adminUser, reservation, "RESERVATION_CREATED",
                "Created reservation for guest: " + reservation.getGuest().getFirstName() + " " +
                        reservation.getGuest().getLastName());
    }

    /**
     * Log reservation update.
     */
    @Override
    public ActivityLog logReservationUpdated(AdminUser adminUser, Reservation reservation) {
        return logReservationAction(adminUser, reservation, "RESERVATION_UPDATED",
                "Updated reservation #" + reservation.getReservationId());
    }

    /**
     * Log reservation cancellation.
     */
    @Override
    public ActivityLog logReservationCancelled(AdminUser adminUser, Reservation reservation) {
        return logReservationAction(adminUser, reservation, "RESERVATION_CANCELLED",
                "Cancelled reservation #" + reservation.getReservationId());
    }
}
