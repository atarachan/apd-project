package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.WaitlistEntry;
import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of WaitlistEntry repository.
 */
public class WaitlistEntryRepositoryImpl extends AbstractRepository<WaitlistEntry, Long>
        implements IWaitlistEntryRepository {

    public WaitlistEntryRepositoryImpl() {
        super(WaitlistEntry.class);
    }

    @Override
    public List<WaitlistEntry> findAll() {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        try {
            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w " +
                            "JOIN FETCH w.guest " +
                            "JOIN FETCH w.roomType " +
                            "ORDER BY w.dateAdded DESC",
                    WaitlistEntry.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<WaitlistEntry> findById(Long id) {

        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        try {

            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w " +
                            "JOIN FETCH w.guest " +
                            "JOIN FETCH w.roomType " +
                            "WHERE w.waitlistId = :id",
                    WaitlistEntry.class);

            query.setParameter("id", id);

            List<WaitlistEntry> results = query.getResultList();

            if (results.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(results.get(0));

        } finally {
            em.close();
        }
    }

    @Override
    public List<WaitlistEntry> findByRoomTypeAndStatus(RoomType roomType, WaitlistStatusType status) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w WHERE w.roomType = :roomType AND w.status = :status " +
                    "ORDER BY w.createdAt ASC",
                    WaitlistEntry.class);
            query.setParameter("roomType", roomType);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<WaitlistEntry> findByStatus(WaitlistStatusType status) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w WHERE w.status = :status ORDER BY w.createdAt ASC",
                    WaitlistEntry.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<WaitlistEntry> findByDateRange(LocalDate checkin, LocalDate checkout) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w WHERE " +
                            "(w.checkInDate <= :checkout AND w.checkOutDate >= :checkin) " +
                            "ORDER BY w.createdAt ASC",
                    WaitlistEntry.class);
            query.setParameter("checkin", checkin);
            query.setParameter("checkout", checkout);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}