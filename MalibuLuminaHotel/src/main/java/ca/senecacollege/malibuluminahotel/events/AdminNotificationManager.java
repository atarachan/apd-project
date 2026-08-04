package ca.senecacollege.malibuluminahotel.events;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject implementation for managing room availability notifications.
 * Maintains a list of observers (typically waitlist services) and notifies them
 * when rooms become available after checkout.
 * 
 * This class implements the Singleton pattern to ensure only one notification
 * manager exists in the application.
 */
public class AdminNotificationManager implements RoomAvailabilitySubject {

    private static final Logger logger = LoggerFactory.getLogger(AdminNotificationManager.class);

    private static AdminNotificationManager instance;
    private final List<RoomAvailabilityObserver> observers;

    private AdminNotificationManager() {
        this.observers = new ArrayList<>();
    }

    /**
     * Get the singleton instance of AdminNotificationManager.
     * 
     * @return the single instance
     */
    public static synchronized AdminNotificationManager getInstance() {
        if (instance == null) {
            instance = new AdminNotificationManager();
        }
        return instance;
    }

    @Override
    public void attach(RoomAvailabilityObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            logger.info("Observer attached: {}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void detach(RoomAvailabilityObserver observer) {
        if (observers.remove(observer)) {
            logger.info("Observer detached: {}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void notifyObservers(Room room, RoomType roomType) {
        logger.info("Notifying {} observers about available room: {} ({})",
                observers.size(), room.getRoomNumber(), roomType.getRoomTypeName());

        for (RoomAvailabilityObserver observer : observers) {
            try {
                observer.onRoomAvailable(room, roomType);
            } catch (Exception e) {
                logger.error("Error notifying observer {}: {}",
                        observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    /**
     * Get the number of currently attached observers.
     * Useful for testing and debugging.
     * 
     * @return the count of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
}
