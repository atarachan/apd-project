package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.WaitlistStatusType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * WaitlistEntry entity - represents guests waiting for room availability.
 * Matches ERD: waitlist_id, RoomTyperroom_type_id, Guestguest_id,
 * requested_checkin, requested_checkout, date_added, status
 */
@Entity
@Table(name = "WaitlistEntry")
public class WaitlistEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waitlist_id")
    private Long waitlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomTyperroom_type_id", nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Guestguest_id", nullable = false)
    private Guest guest;

    @Column(name = "requested_checkin", nullable = false)
    private LocalDate requestedCheckin;

    @Column(name = "requested_checkout", nullable = false)
    private LocalDate requestedCheckout;

    @Column(name = "date_added", nullable = false)
    private LocalDate dateAdded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private WaitlistStatusType status;

    // Constructors
    public WaitlistEntry() {
        this.dateAdded = LocalDate.now();
        this.status = WaitlistStatusType.IN_QUEUE;
    }

    public WaitlistEntry(RoomType roomType, Guest guest, LocalDate requestedCheckin, LocalDate requestedCheckout) {
        this();
        this.roomType = roomType;
        this.guest = guest;
        this.requestedCheckin = requestedCheckin;
        this.requestedCheckout = requestedCheckout;
    }

    /**
     * Notify guest method as per ERD class diagram.
     * Marks status as SPOT_AVAILABLE.
     */
    public void notifyGuest() {
        this.status = WaitlistStatusType.SPOT_AVAILABLE;
    }

    // Getters and Setters
    public Long getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Long waitlistId) {
        this.waitlistId = waitlistId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getRequestedCheckin() {
        return requestedCheckin;
    }

    public void setRequestedCheckin(LocalDate requestedCheckin) {
        this.requestedCheckin = requestedCheckin;
    }

    public LocalDate getRequestedCheckout() {
        return requestedCheckout;
    }

    public void setRequestedCheckout(LocalDate requestedCheckout) {
        this.requestedCheckout = requestedCheckout;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public WaitlistStatusType getStatus() {
        return status;
    }

    public void setStatus(WaitlistStatusType status) {
        this.status = status;
    }
}
