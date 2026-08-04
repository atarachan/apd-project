package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Feedback entity - represents guest feedback for reservations.
 * Matches ERD: feedback_id, Guestguest_id, rating, comment, submitted_date,
 * Reservationreservation_id
 */
@Entity
@Table(name = "Feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Reservationreservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Guestguest_id", nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private int rating; // 1-5 stars

    @Column(length = 255)
    private String comment;

    @Column(name = "submitted_date", nullable = false)
    private LocalDate submittedDate;

    // Constructors
    public Feedback() {
        this.submittedDate = LocalDate.now();
    }

    public Feedback(Reservation reservation, Guest guest, int rating, String comment) {
        this();
        this.reservation = reservation;
        this.guest = guest;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
    }
}
