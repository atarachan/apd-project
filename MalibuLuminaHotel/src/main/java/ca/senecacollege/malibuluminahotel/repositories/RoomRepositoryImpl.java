package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;

public class RoomRepositoryImpl extends AbstractRepository<Room, Long>
        implements IRoomRepository {

    public RoomRepositoryImpl() {
        super(Room.class);
    }

    @Override
    public Optional<Room> findFirstAvailable(RoomTypeName typeName,
                                             LocalDate checkIn,
                                             LocalDate checkOut) {
        EntityManager em = createEntityManager();

        try {
            /*
             * Finds the first AVAILABLE room of the requested type that has no
             * overlapping non-cancelled reservation items.
             * Overlap condition: existing checkIn < requested checkOut
             *                AND existing checkOut > requested checkIn
             */
            String jpql = """
                    SELECT r FROM Room r
                    WHERE r.roomType.roomTypeName = :typeName
                    AND r.status = :available
                    AND r NOT IN (
                        SELECT ri.room FROM ReservationItem ri
                        WHERE ri.reservation.status <> :cancelled
                        AND ri.reservation.checkInDate < :checkOut
                        AND ri.reservation.checkOutDate > :checkIn
                    )
                    ORDER BY r.roomNumber ASC
                    """;

            return em.createQuery(jpql, Room.class)
                    .setParameter("typeName", typeName)
                    .setParameter("available", RoomStatus.AVAILABLE)
                    .setParameter("cancelled", ReservationStatus.CANCELLED)
                    .setParameter("checkIn", checkIn)
                    .setParameter("checkOut", checkOut)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }
}
