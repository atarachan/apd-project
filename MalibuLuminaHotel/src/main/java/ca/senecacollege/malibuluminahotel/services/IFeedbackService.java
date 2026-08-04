package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Feedback;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import java.util.List;
import java.util.Optional;

public interface IFeedbackService {

    Feedback submitFeedback(Reservation reservation, int rating, String comment);

    List<Feedback> getAllFeedback();

    Optional<Feedback> getFeedbackByReservation(Reservation reservation);

    List<Feedback> getFeedbackByMinRating(int minRating);

    double getAverageRating();

    void deleteFeedback(Feedback feedback);
}
