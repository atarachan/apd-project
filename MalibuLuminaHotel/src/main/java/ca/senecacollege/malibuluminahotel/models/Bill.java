package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;


    // bill owns the database relationship because this class
    // contains the reservation_id foreign key.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "reservation_id",
            nullable = false,
            unique = true
    )
    private Reservation reservation;

    @Column(
            name = "subtotal",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(
            name = "discount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(
            name = "tax",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(
            name = "total",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal total = BigDecimal.ZERO;

    @Column(
            name = "balance_due",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal balanceDue = BigDecimal.ZERO;

    @Column(name = "bill_date", nullable = false)
    private LocalDateTime billDate;

    @OneToMany(
            mappedBy = "bill",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Payment> payments = new ArrayList<>();

    public Bill() {
        this.billDate = LocalDateTime.now();
    }

    public Bill(Reservation reservation, BigDecimal subtotal, BigDecimal discount,
                BigDecimal tax, BigDecimal total, BigDecimal balanceDue) {

        this();

        this.reservation = reservation;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.balanceDue = balanceDue;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getBalanceDue() {
        return balanceDue;
    }

    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments.clear();

        if (payments == null) {
            return;
        }

        for (Payment payment : payments) {
            addPayment(payment);
        }
    }

     // keeps both sides of the Bill-Payment relationship synchronized.
    public void addPayment(Payment payment) {
        if (payment == null) {
            return;
        }

        payments.add(payment);
        payment.setBill(this);
    }

    public void removePayment(Payment payment) {
        if (payment == null) {
            return;
        }

        payments.remove(payment);
        payment.setBill(null);
    }


     //subtotal - discount + tax
    public void calculateTotal() {

        BigDecimal safeSubtotal = subtotal == null ? BigDecimal.ZERO : subtotal;

        BigDecimal safeDiscount = discount == null ? BigDecimal.ZERO : discount;

        BigDecimal safeTax = tax == null ? BigDecimal.ZERO : tax;

        total = safeSubtotal.subtract(safeDiscount).add(safeTax);

        balanceDue = total;
    }
}
