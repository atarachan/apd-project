package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Feedback;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.repositories.FeedbackRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IFeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing feedback operations.
 */
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);
    private final IFeedbackRepository feedbackRepository;

    public FeedbackService() {
        this.feedbackRepository = new FeedbackRepositoryImpl();
    }

    public FeedbackService(IFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Submit feedback for a reservation.
     * 
     * @param reservation The reservation
     * @param rating      Rating (1-5)
     * @param comment     Feedback comment
     * @return The saved feedback
     */
    public Feedback submitFeedback(Reservation reservation, int rating, String comment) {
        logger.info("Submitting feedback for reservation ID: {}", reservation.getReservationId());

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Check if feedback already exists
        Optional<Feedback> existing = feedbackRepository.findByReservation(reservation);
        if (existing.isPresent()) {
            logger.warn("Feedback already exists for reservation ID: {}", reservation.getReservationId());
            throw new IllegalStateException("Feedback already submitted for this reservation");
        }

        Guest guest = reservation.getGuest();
        Feedback feedback = new Feedback(reservation, guest, rating, comment);

        Feedback saved = feedbackRepository.save(feedback);
        logger.info("Feedback submitted successfully: ID {}, Rating {}", saved.getFeedbackId(), rating);

        return saved;
    }

    /**
     * Get all feedback entries.
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    /**
     * Get feedback by reservation.
     */
    public Optional<Feedback> getFeedbackByReservation(Reservation reservation) {
        return feedbackRepository.findByReservation(reservation);
    }

    /**
     * Get feedback with rating >= specified value.
     */
    public List<Feedback> getFeedbackByMinRating(int minRating) {
        return feedbackRepository.findByRatingGreaterThanOrEqual(minRating);
    }

    /**
     * Get average rating across all feedback.
     */
    public double getAverageRating() {
        Double avg = feedbackRepository.getAverageRating();
        return avg != null ? avg : 0.0;
    }

    /**
     * Delete feedback.
     */
    public void deleteFeedback(Feedback feedback) {
        logger.info("Deleting feedback ID: {}", feedback.getFeedbackId());
        feedbackRepository.delete(feedback);
    }
}
