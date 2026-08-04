package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.ActivityLog;
import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface IActivityLogService {

    ActivityLog log(AdminUser adminUser, String action, String entityType, Long entityId, String message);

    ActivityLog logReservationAction(AdminUser adminUser, Reservation reservation, String action, String message);

    List<ActivityLog> getAllLogs();

    List<ActivityLog> getLogsByAdmin(AdminUser adminUser);

    List<ActivityLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end);

    List<ActivityLog> getLogsByAction(String action);

    List<ActivityLog> getRecentLogs(int limit);

    ActivityLog logReservationCreated(AdminUser adminUser, Reservation reservation);

    ActivityLog logReservationUpdated(AdminUser adminUser, Reservation reservation);

    ActivityLog logReservationCancelled(AdminUser adminUser, Reservation reservation);
}
