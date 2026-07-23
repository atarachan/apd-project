package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ReservationItems")
public class ReservationItem  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_item_id")
    private Long reservationItemId;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private BigDecimal nightlyRate;

    private int guestCount;

    @OneToMany(
            mappedBy = "reservationItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReservationItemAddOn> addOns =
            new ArrayList<>();

    public ReservationItem() {
    }

    public ReservationItem(Reservation reservation, Room room, BigDecimal nightlyRate, int guestCount) {
        this.reservation = reservation;
        this.room = room;
        this.nightlyRate = nightlyRate;
        this.guestCount = guestCount;
    }

    public Long getReservationItemId() {
        return reservationItemId;
    }

    public void setReservationItemId(Long reservationItemId) {
        this.reservationItemId = reservationItemId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(BigDecimal nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public List<ReservationItemAddOn> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<ReservationItemAddOn> addOns) {
        this.addOns.clear();

        if (addOns != null) {
            for (ReservationItemAddOn addOn : addOns) {
                addAddOn(addOn);
            }
        }
    }

    public void addAddOn(ReservationItemAddOn addOn) {
        addOns.add(addOn);
        addOn.setReservationItem(this);
    }

    public void removeAddOn(ReservationItemAddOn addOn) {
        addOns.remove(addOn);
        addOn.setReservationItem(null);
    }
}
