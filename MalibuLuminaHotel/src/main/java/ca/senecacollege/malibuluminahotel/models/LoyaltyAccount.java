package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LoyaltyAccount")
public class LoyaltyAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_id")
    private Long loyaltyId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "guest_id",
            nullable = false,
            unique = true
    )
    private Guest guest;

    @Column(
            name = "member_number",
            nullable = false,
            unique = true,
            length = 20
    )
    private String memberNumber;

    @Column(name = "current_points", nullable = false)
    private int currentPoints;

    @OneToMany(
            mappedBy = "loyaltyAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LoyaltyTransaction> transactions = new ArrayList<>();

    public LoyaltyAccount() {
        this.currentPoints = 0;
    }

    public LoyaltyAccount(Guest guest, String memberNumber) {

        this();

        this.guest = guest;
        this.memberNumber = memberNumber;
    }


    public void addTransaction(LoyaltyTransaction transaction) {
        if (transaction == null) {
            return;
        }

        transactions.add(transaction);
        transaction.setLoyaltyAccount(this);
    }

    public void removeTransaction(LoyaltyTransaction transaction) {
        if (transaction == null) {
            return;
        }

        transactions.remove(transaction);
        transaction.setLoyaltyAccount(null);
    }

    public void setTransactions(List<LoyaltyTransaction> transactions) {

        this.transactions.clear();

        if (transactions == null) {
            return;
        }

        for (LoyaltyTransaction transaction : transactions) {
            addTransaction(transaction);
        }
    }
}
