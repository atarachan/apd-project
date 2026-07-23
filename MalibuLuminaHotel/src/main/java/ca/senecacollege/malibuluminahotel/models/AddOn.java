package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.PricingModel;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AddOns")
public class AddOn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "add_on_id")
    private Long addOnId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingModel pricingModel = PricingModel.PER_NIGHT;

    @OneToMany(mappedBy = "addOn")
    private List<ReservationItemAddOn> reservationItemAddOns =
            new ArrayList<>();

    public AddOn() {
    }

    public AddOn(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.pricingModel = PricingModel.PER_NIGHT;
    }

    public AddOn(String name, BigDecimal price, String description, PricingModel pricingModel) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.pricingModel = pricingModel;
    }

    public Long getAddOnId() {
        return addOnId;
    }

    public void setAddOnId(Long addOnId) {
        this.addOnId = addOnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<ReservationItemAddOn> getReservationAddOns() {
        return reservationItemAddOns;
    }

    public void setReservationAddOns(List<ReservationItemAddOn> reservationAddOns) {
        this.reservationItemAddOns = reservationAddOns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PricingModel getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
    }
}
