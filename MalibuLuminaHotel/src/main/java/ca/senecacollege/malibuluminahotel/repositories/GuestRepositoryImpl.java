package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GuestRepositoryImpl extends AbstractRepository<Guest, Long> implements IGuestRepository {

    public GuestRepositoryImpl() {
        super(Guest.class);
    }

    @Override
    public Optional<Guest> findByEmail(String email) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT g
                            FROM Guest g
                            WHERE LOWER(g.email) = LOWER(:email)
                            """,
                            Guest.class
                    )
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }

    }

    @Override
    public List<Guest> findByLastName(String lastName) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            """
                            SELECT g
                            FROM Guest g
                            WHERE LOWER(g.lastName) = LOWER(:lastName)
                            ORDER BY g.firstName
                            """,
                            Guest.class
                    )
                    .setParameter("lastName", lastName)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
