package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WaitlistEntry entity operations.
 */
public interface IWaitlistEntryRepository {

    /**
     * Save or update waitlist entry.
     */
    WaitlistEntry save(WaitlistEntry entry);

    /**
     * Find waitlist entry by ID.
     */
    Optional<WaitlistEntry> findById(Long id);

    WaitlistEntry update(WaitlistEntry entry);

    /**
     * Find all waitlist entries.
     */
    List<WaitlistEntry> findAll();

    /**
     * Find waitlist entries by room type and status.
     */
    List<WaitlistEntry> findByRoomTypeAndStatus(RoomType roomType, WaitlistStatusType status);

    /**
     * Find waitlist entries by status.
     */
    List<WaitlistEntry> findByStatus(WaitlistStatusType status);

    /**
     * Find waitlist entries matching date range.
     */
    List<WaitlistEntry> findByDateRange(LocalDate checkin, LocalDate checkout);

    /**
     * Delete waitlist entry.
     */
    void delete(WaitlistEntry entry);
}
