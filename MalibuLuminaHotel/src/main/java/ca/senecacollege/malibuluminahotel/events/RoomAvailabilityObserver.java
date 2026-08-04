package ca.senecacollege.malibuluminahotel.events;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;

/**
 * Observer interface for room availability notifications.
 * Observers implementing this interface will be notified when rooms become
 * available.
 */
public interface RoomAvailabilityObserver {

    /**
     * Called when a room becomes available after checkout.
     * 
     * @param room     the room that became available
     * @param roomType the type of the room
     */
    void onRoomAvailable(Room room, RoomType roomType);
}
