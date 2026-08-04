package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WaitlistEntry entity operations.
 */
public interface IWaitlistRepository {

    /**
     * Save a new waitlist entry or update existing one.
     */
    WaitlistEntry save(WaitlistEntry waitlistEntry);

    /**
     * Find waitlist entry by ID.
     */
    Optional<WaitlistEntry> findById(Long waitlistId);

    /**
     * Find all waitlist entries.
     */
    List<WaitlistEntry> findAll();

    /**
     * Find waitlist entries by guest.
     */
    List<WaitlistEntry> findByGuest(Guest guest);

    /**
     * Find waitlist entries by room type.
     */
    List<WaitlistEntry> findByRoomType(RoomType roomType);

    /**
     * Find waitlist entries by status.
     */
    List<WaitlistEntry> findByStatus(WaitlistStatus status);

    /**
     * Find active waiting entries for a room type, ordered by priority.
     */
    List<WaitlistEntry> findWaitingByRoomTypeOrderedByPriority(RoomType roomType);

    /**
     * Find waitlist entries that overlap with given dates.
     */
    List<WaitlistEntry> findByDateRange(LocalDate checkIn, LocalDate checkOut);

    /**
     * Delete waitlist entry by ID.
     */
    void deleteById(Long waitlistId);

    /**
     * Count active waiting entries.
     */
    long countByStatus(WaitlistStatus status);
}
