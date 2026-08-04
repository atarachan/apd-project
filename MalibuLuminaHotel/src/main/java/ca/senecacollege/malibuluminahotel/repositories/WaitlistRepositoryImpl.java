package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatus;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of Waitlist repository.
 */
public class WaitlistRepositoryImpl implements IWaitlistRepository {

    private final EntityManager em;

    public WaitlistRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public WaitlistEntry save(WaitlistEntry waitlistEntry) {
        if (waitlistEntry.getWaitlistId() == null) {
            em.persist(waitlistEntry);
            return waitlistEntry;
        } else {
            return em.merge(waitlistEntry);
        }
    }

    @Override
    public Optional<WaitlistEntry> findById(Long waitlistId) {
        WaitlistEntry entry = em.find(WaitlistEntry.class, waitlistId);
        return Optional.ofNullable(entry);
    }

    @Override
    public List<WaitlistEntry> findAll() {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w ORDER BY w.createdAt DESC",
                WaitlistEntry.class);
        return query.getResultList();
    }

    @Override
    public List<WaitlistEntry> findByGuest(Guest guest) {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w WHERE w.guest = :guest ORDER BY w.createdAt DESC",
                WaitlistEntry.class);
        query.setParameter("guest", guest);
        return query.getResultList();
    }

    @Override
    public List<WaitlistEntry> findByRoomType(RoomType roomType) {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w WHERE w.roomType = :roomType ORDER BY w.createdAt DESC",
                WaitlistEntry.class);
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }

    @Override
    public List<WaitlistEntry> findByStatus(WaitlistStatus status) {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w WHERE w.status = :status ORDER BY w.createdAt DESC",
                WaitlistEntry.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<WaitlistEntry> findWaitingByRoomTypeOrderedByPriority(RoomType roomType) {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w WHERE w.roomType = :roomType " +
                        "AND w.status = :status ORDER BY w.priorityScore DESC, w.createdAt ASC",
                WaitlistEntry.class);
        query.setParameter("roomType", roomType);
        query.setParameter("status", WaitlistStatus.WAITING);
        return query.getResultList();
    }

    @Override
    public List<WaitlistEntry> findByDateRange(LocalDate checkIn, LocalDate checkOut) {
        TypedQuery<WaitlistEntry> query = em.createQuery(
                "SELECT w FROM WaitlistEntry w WHERE " +
                        "(w.checkInDate <= :checkOut AND w.checkOutDate >= :checkIn) " +
                        "ORDER BY w.createdAt DESC",
                WaitlistEntry.class);
        query.setParameter("checkIn", checkIn);
        query.setParameter("checkOut", checkOut);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long waitlistId) {
        WaitlistEntry entry = em.find(WaitlistEntry.class, waitlistId);
        if (entry != null) {
            em.remove(entry);
        }
    }

    @Override
    public long countByStatus(WaitlistStatus status) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(w) FROM WaitlistEntry w WHERE w.status = :status",
                Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }
}
