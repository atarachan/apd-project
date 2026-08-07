package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Feedback;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Feedback entity operations.
 */
public interface IFeedbackRepository {

    /**
     * Save or update feedback.
     */
    Feedback save(Feedback feedback);


    /**
     * Find feedback by ID.
     */
    Optional<Feedback> findById(Long id);

    List<Feedback> findAllWithDetails();

    List<Feedback> findByRatingGreaterThanOrEqualWithDetails(int rating);

    /**
     * Find feedback by reservation.
     */
    Optional<Feedback> findByReservation(Reservation reservation);

    /**
     * Calculate average rating.
     */
    Double getAverageRating();

    /**
     * Delete feedback.
     */
    void delete(Feedback feedback);
}
