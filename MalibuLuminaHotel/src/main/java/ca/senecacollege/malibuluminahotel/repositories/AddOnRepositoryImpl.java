package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.models.AddOn;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Optional;

public class AddOnRepositoryImpl extends AbstractRepository<AddOn, Long> implements IAddOnRepository {

    public AddOnRepositoryImpl() {
        super(AddOn.class);
    }

    @Override
    public Optional<AddOn> findByName(String name) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery("""
                            SELECT a
                            FROM AddOn a
                            WHERE LOWER(a.name) = LOWER(:name)
                            """,
                            AddOn.class
                    )
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst();
        }
        finally {
            em.close();
        }
    }
}
