package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.Room;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ReservationItemRepositoryImpl extends AbstractRepository<ReservationItem, Long> implements IReservationItemRepository {

    public ReservationItemRepositoryImpl() {
        super(ReservationItem.class);
    }

    @Override
    public List<ReservationItem> findByReservation(Reservation reservation) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ri FROM ReservationItem ri " +
                                    "WHERE ri.reservation = :reservation " +
                                    "ORDER BY ri.reservationItemId",
                            ReservationItem.class
                    )
                    .setParameter("reservation", reservation)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<ReservationItem> findByRoom(Room room) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ri FROM ReservationItem ri " +
                                    "WHERE ri.room = :room " +
                                    "ORDER BY ri.reservation.checkInDate",
                            ReservationItem.class
                    )
                    .setParameter("room", room)
                    .getResultList();

        }
        finally {
            em.close();
        }

    }

    @Override
    public Optional<ReservationItem> findByReservationAndRoom(Reservation reservation, Room room) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ri FROM ReservationItem ri " +
                                    "WHERE ri.reservation = :reservation " +
                                    "AND ri.room = :room",
                            ReservationItem.class
                    )
                    .setParameter("reservation", reservation)
                    .setParameter("room", room)
                    .getResultStream()
                    .findFirst();

        }
        finally {
            em.close();
        }
    }
}
