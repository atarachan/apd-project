package ca.senecacollege.malibuluminahotel.events;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;

/**
 * Subject interface for the Observer pattern.
 * Maintains a list of observers and notifies them of room availability changes.
 */
public interface RoomAvailabilitySubject {

    /**
     * Attach an observer to receive notifications.
     * 
     * @param observer the observer to attach
     */
    void attach(RoomAvailabilityObserver observer);

    /**
     * Detach an observer from receiving notifications.
     * 
     * @param observer the observer to detach
     */
    void detach(RoomAvailabilityObserver observer);

    /**
     * Notify all attached observers that a room has become available.
     * 
     * @param room     the room that became available
     * @param roomType the type of the room
     */
    void notifyObservers(Room room, RoomType roomType);
}
