package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class RoomTypeRepositoryImpl extends AbstractRepository<RoomType, Long> implements IRoomTypeRepository {

    public RoomTypeRepositoryImpl() {
        super(RoomType.class);
    }

    @Override
    public Optional<RoomType> findByName(RoomTypeName roomTypeName) {
        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT rt
                            FROM RoomType rt
                            WHERE rt.roomTypeName = :roomTypeName
                            """,
                            RoomType.class)
                    .setParameter("roomTypeName", roomTypeName)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public List<RoomType> findByMinimumOccupancy(int occupancy) {
        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            """
                            SELECT rt
                            FROM RoomType rt
                            WHERE rt.maxOccupancy >= :occupancy
                            ORDER BY rt.maxOccupancy
                            """,
                            RoomType.class)
                    .setParameter("occupancy", occupancy)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
