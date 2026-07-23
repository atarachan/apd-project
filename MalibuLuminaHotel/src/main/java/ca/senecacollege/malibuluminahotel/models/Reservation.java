package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Reservations")
public class Reservation implements Serializable {

    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReservationItem> reservationItems =
            new ArrayList<>();

    @OneToOne(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Bill bill;

    public Reservation() {
        reservationDate = LocalDateTime.now();
        status = ReservationStatus.PENDING;
    }

    public Reservation(Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this();
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public List<ReservationItem> getReservationItems() {
        return reservationItems;
    }

    public void setReservationItems(
            List<ReservationItem> reservationItems
    ) {
        this.reservationItems.clear();

        if (reservationItems != null) {
            for (ReservationItem item : reservationItems) {
                addReservationItem(item);
            }
        }
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;

        if (bill != null) {
            bill.setReservation(this);
        }
    }


    // likely add these to add entities. useful for keeping relationships
    public void addReservationItem(ReservationItem reservationItem) {
        reservationItems.add(reservationItem);
        reservationItem.setReservation(this);
    }

    public void removeReservationItem(ReservationItem reservationItem) {
        reservationItems.remove(reservationItem);
        reservationItem.setReservation(null);
    }
}
