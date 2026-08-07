package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.events.AdminNotificationManager;
import ca.senecacollege.malibuluminahotel.events.WaitlistNotificationObserver;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;
import ca.senecacollege.malibuluminahotel.repositories.IWaitlistEntryRepository;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing waitlist operations.
 * Integrates with Observer pattern for room availability notifications.
 */
public class WaitlistService implements IWaitlistService {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistService.class);
    private final IWaitlistEntryRepository waitlistRepository;
    private final AdminNotificationManager notificationManager;

    @Inject
    public WaitlistService(IWaitlistEntryRepository waitlistRepository) {
        this.waitlistRepository = waitlistRepository;
        this.notificationManager = AdminNotificationManager.getInstance();

        WaitlistNotificationObserver observer = new WaitlistNotificationObserver();
        notificationManager.attach(observer);

        logger.info("WaitlistService initialized with notification observer");
    }

    @Override
    public void updateEntry(WaitlistEntry entry) {

        waitlistRepository.update(entry);

    }

    /**
     * Add a guest to the waitlist for a specific room type.
     * 
     * @param guest    The guest
     * @param roomType The room type
     * @param checkin  Requested check-in date
     * @param checkout Requested checkout date
     * @return The created waitlist entry
     */
    @Override
    public WaitlistEntry addToWaitlist(Guest guest, RoomType roomType, LocalDate checkin, LocalDate checkout) {
        logger.info("Adding guest {} to waitlist for room type: {}",
                guest.getEmail(), roomType.getRoomTypeName());

        WaitlistEntry entry = new WaitlistEntry(roomType, guest, checkin, checkout);
        WaitlistEntry saved = waitlistRepository.save(entry);

        logger.info("Waitlist entry created with ID: {}", saved.getWaitlistId());
        return saved;
    }

    /**
     * Get all waitlist entries.
     */
    @Override
    public List<WaitlistEntry> getAllWaitlistEntries() {
        return waitlistRepository.findAll();
    }

    /**
     * Get waitlist entries by status.
     */
    @Override
    public List<WaitlistEntry> getEntriesByStatus(WaitlistStatusType status) {
        return waitlistRepository.findByStatus(status);
    }

    /**
     * Get waitlist entries for a specific room type with status IN_QUEUE.
     */
    @Override
    public List<WaitlistEntry> getQueuedEntriesForRoomType(RoomType roomType) {
        return waitlistRepository.findByRoomTypeAndStatus(roomType, WaitlistStatusType.IN_QUEUE);
    }

    /**
     * Notify guest that room is available (marks as SPOT_AVAILABLE).
     */
    @Override
    public void notifyGuestSpotAvailable(WaitlistEntry entry) {
        logger.info("Notifying guest for waitlist entry ID: {}", entry.getWaitlistId());
        entry.notifyGuest(); // Sets status to SPOT_AVAILABLE
        waitlistRepository.save(entry);
        logger.info("Waitlist entry {} marked as SPOT_AVAILABLE", entry.getWaitlistId());
    }

    /**
     * Withdraw a waitlist entry.
     */
    @Override
    public void withdrawEntry(WaitlistEntry entry) {
        logger.info("Withdrawing waitlist entry ID: {}", entry.getWaitlistId());
        entry.setStatus(WaitlistStatusType.WITHDRAWN);
        waitlistRepository.save(entry);
    }

    /**
     * Delete waitlist entry.
     */
    @Override
    public void deleteEntry(WaitlistEntry entry) {
        logger.info("Deleting waitlist entry ID: {}", entry.getWaitlistId());
        waitlistRepository.delete(entry);
    }

    /**
     * Find waitlist entry by ID.
     */
    @Override
    public Optional<WaitlistEntry> findById(Long id) {
        return waitlistRepository.findById(id);
    }
}
