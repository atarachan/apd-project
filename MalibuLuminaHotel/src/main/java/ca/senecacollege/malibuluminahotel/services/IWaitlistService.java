package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IWaitlistService {

    WaitlistEntry addToWaitlist(Guest guest, RoomType roomType, LocalDate checkin, LocalDate checkout);

    List<WaitlistEntry> getAllWaitlistEntries();

    List<WaitlistEntry> getEntriesByStatus(WaitlistStatusType status);

    List<WaitlistEntry> getQueuedEntriesForRoomType(RoomType roomType);

    void updateEntry(WaitlistEntry entry);

    void notifyGuestSpotAvailable(WaitlistEntry entry);

    void withdrawEntry(WaitlistEntry entry);

    void deleteEntry(WaitlistEntry entry);

    Optional<WaitlistEntry> findById(Long id);
}
