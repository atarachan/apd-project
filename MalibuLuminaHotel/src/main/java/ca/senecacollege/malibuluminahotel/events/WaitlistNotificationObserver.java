package ca.senecacollege.malibuluminahotel.events;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete Observer implementation for waitlist notifications.
 * When notified that a room becomes available, this observer checks if there
 * are any guests on the waitlist for that room type and logs the notification.
 * 
 * In a complete implementation, this would:
 * - Query the waitlist database for matching entries
 * - Send notifications to waiting guests
 * - Update waitlist status
 */
public class WaitlistNotificationObserver implements RoomAvailabilityObserver {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistNotificationObserver.class);

    private int notificationCount = 0;

    @Override
    public void onRoomAvailable(Room room, RoomType roomType) {
        notificationCount++;

        logger.info("WAITLIST NOTIFICATION #{}: Room {} of type {} is now available!",
                notificationCount, room.getRoomNumber(), roomType.getRoomTypeName());

        // In a full implementation, this would:
        // 1. Query waitlist for guests waiting for this room type
        // 2. Find the first guest on the waitlist with matching dates
        // 3. Send notification to that guest (email, SMS, admin dashboard alert)
        // 4. Update waitlist entry status to NOTIFIED

        logger.info("Checking waitlist for room type: {}", roomType.getRoomTypeName());
        logger.info("Admin should be notified to convert waitlist entries to reservations");
    }

    /**
     * Get the total number of notifications received.
     * Useful for testing and statistics.
     * 
     * @return the count of notifications
     */
    public int getNotificationCount() {
        return notificationCount;
    }

    /**
     * Reset the notification counter.
     * Useful for testing.
     */
    public void resetCounter() {
        notificationCount = 0;
    }
}
