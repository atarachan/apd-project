package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class ReservationRepositoryImpl extends AbstractRepository<Reservation, Long> implements IReservationRepository {

    public ReservationRepositoryImpl() {
        super(Reservation.class);
    }

    @Override
    public List<Reservation> findByGuest(Guest guest) {EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.guest = :guest " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("guest", guest)
                    .getResultList();

        } finally{
            em.close();
        }

    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.status = :status " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("status", status)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByCheckInDate(LocalDate checkInDate) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.checkInDate = :checkInDate " +
                                    "ORDER BY r.reservationDate",
                            Reservation.class
                    )
                    .setParameter("checkInDate", checkInDate)
                    .getResultList();

        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.checkInDate BETWEEN :startDate AND :endDate " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

        }
        finally {
            em.close();
        }
    }
}
