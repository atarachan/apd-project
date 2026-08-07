package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.models.Feedback;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of Feedback repository.
 */
public class FeedbackRepositoryImpl extends AbstractRepository<Feedback, Long> implements IFeedbackRepository {

    public FeedbackRepositoryImpl() {
        super(Feedback.class);
    }

    @Override
    public Optional<Feedback> findByReservation(Reservation reservation) {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<Feedback> query = em.createQuery(
                    "SELECT f FROM Feedback f WHERE f.reservation = :reservation",
                    Feedback.class);
            query.setParameter("reservation", reservation);
            return query.getResultStream().findFirst();
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Feedback> findAllWithDetails() {

        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                    "SELECT DISTINCT f " +
                            "FROM Feedback f " +
                            "LEFT JOIN FETCH f.guest " +
                            "LEFT JOIN FETCH f.reservation r " +
                            "LEFT JOIN FETCH r.reservationItems ri " +
                            "LEFT JOIN FETCH ri.room room " +
                            "LEFT JOIN FETCH room.roomType",
                    Feedback.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Feedback> findByRatingGreaterThanOrEqualWithDetails(int rating) {

        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                            "SELECT DISTINCT f " +
                                    "FROM Feedback f " +
                                    "LEFT JOIN FETCH f.guest " +
                                    "LEFT JOIN FETCH f.reservation r " +
                                    "LEFT JOIN FETCH r.reservationItems ri " +
                                    "LEFT JOIN FETCH ri.room room " +
                                    "LEFT JOIN FETCH room.roomType " +
                                    "WHERE f.rating >= :rating " +
                                    "ORDER BY f.submittedDate DESC",
                            Feedback.class
                    )
                    .setParameter("rating", rating)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Double getAverageRating() {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(f.rating) FROM Feedback f", Double.class);
            Double avg = query.getSingleResult();
            return avg != null ? avg : 0.0;
        } finally {
            em.close();
        }
    }
}
