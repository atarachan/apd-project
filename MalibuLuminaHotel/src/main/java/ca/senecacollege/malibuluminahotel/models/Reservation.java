package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
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

    private LocalDate createdDate;

    @Column(nullable = false)
    private int adults = 1;

    @Column(nullable = false)
    private int children = 0;

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
        createdDate = LocalDate.now();
        status = ReservationStatus.PENDING;
    }

    public Reservation(Guest guest, LocalDate checkInDate, LocalDate checkOutDate) {
        this();
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public Reservation(Guest guest, LocalDate checkInDate, LocalDate checkOutDate,
                       int adults, int children) {
        this(guest, checkInDate, checkOutDate);
        this.adults = adults;
        this.children = children;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
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

    public void setReservationItems(List<ReservationItem> reservationItems) {
        this.reservationItems = reservationItems;
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
