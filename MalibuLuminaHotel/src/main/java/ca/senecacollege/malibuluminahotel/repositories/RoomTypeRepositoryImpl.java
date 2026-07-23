package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import javax.persistence.EntityManager;
import java.util.Optional;

public class RoomTypeRepositoryImpl extends AbstractRepository<RoomType, Long>
        implements IRoomTypeRepository {

    public RoomTypeRepositoryImpl() {
        super(RoomType.class);
    }

    @Override
    public Optional<RoomType> findByName(RoomTypeName name) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT rt FROM RoomType rt WHERE rt.roomTypeName = :name",
                            RoomType.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
