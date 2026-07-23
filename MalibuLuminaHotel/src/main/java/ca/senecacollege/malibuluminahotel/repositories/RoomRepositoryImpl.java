package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class RoomRepositoryImpl extends AbstractRepository<Room, Long> implements IRoomRepository {

    public RoomRepositoryImpl() {
        super(Room.class);
    }

    @Override
    public Optional<Room> findByRoomNumber(String roomNumber) {

        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT r
                            FROM Room r
                            WHERE r.roomNumber = :roomNumber
                            """,
                            Room.class)
                    .setParameter("roomNumber", roomNumber)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT r
                            FROM Room r
                            WHERE r.status = :status
                            ORDER BY r.roomNumber
                            """,
                            Room.class)
                    .setParameter("status", status)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> findByRoomType(RoomType roomType) {
        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT r
                            FROM Room r
                            WHERE r.roomType = :roomType
                            ORDER BY r.roomNumber
                            """,
                            Room.class)
                    .setParameter("roomType", roomType)
                    .getResultList();

        }
        finally {
            em.close();
        }

    }

    @Override
    public List<Room> findByFloor(int floor) {
        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT r
                            FROM Room r
                            WHERE r.floor = :floor
                            ORDER BY r.roomNumber
                            """,
                            Room.class)
                    .setParameter("floor", floor)
                    .getResultList();

        }
        finally {
            em.close();
        }
    }
}
