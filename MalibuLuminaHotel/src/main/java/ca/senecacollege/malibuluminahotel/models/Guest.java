package ca.senecacollege.malibuluminahotel.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Guests")
public class Guest implements Serializable {

    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id", unique = true, nullable = false)
    private Long guestId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String phone;

    // probably not necessary
    //@Column(nullable = false, length = 100)
    //private String address;

    @Column(nullable = false)
    private LocalDateTime datedCreated;

    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations = new ArrayList<>();

    public Guest() {
        this.datedCreated = LocalDateTime.now();
    }

    public Guest(String firstName, String lastName, String email, String phone, LocalDateTime datedCreated) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getDatedCreated() {
        return datedCreated;
    }

    public void setDatedCreated(LocalDateTime datedCreated) {
        this.datedCreated = datedCreated;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
