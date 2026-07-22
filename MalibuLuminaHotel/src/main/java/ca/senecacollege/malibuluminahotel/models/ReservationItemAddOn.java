package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ReservationAddOn")
public class ReservationItemAddOn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_addon_id")
    private Long reservationAddOnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_item_id", nullable = false)
    private ReservationItem reservationItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addon_id", nullable = false)
    private AddOn addOn;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal itemTotal;

    public ReservationItemAddOn() {
    }

    public ReservationItemAddOn(ReservationItem reservationItem,
                            AddOn addOn,
                            int quantity,
                            BigDecimal itemTotal) {
        this.reservationItem = reservationItem;
        this.addOn = addOn;
        this.quantity = quantity;
        this.itemTotal = itemTotal;
    }

    public Long getReservationAddOnId() {
        return reservationAddOnId;
    }

    public void setReservationAddOnId(Long reservationAddOnId) {
        this.reservationAddOnId = reservationAddOnId;
    }

    public ReservationItem getReservationItem() {
        return reservationItem;
    }

    public void setReservationItem(ReservationItem reservationItem) {
        this.reservationItem = reservationItem;
    }

    public AddOn getAddOn() {
        return addOn;
    }

    public void setAddOn(AddOn addOn) {
        this.addOn = addOn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getItemTotal() {
        return addOn.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }
}
