package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Rooms")
public class Room implements Serializable {
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private int floor;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @OneToMany(mappedBy = "room")
    private List<ReservationItem> reservationItems = new ArrayList<>();

    public Room() {
    }

    public Room(String roomNumber, RoomStatus status, int floor, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.status = status;
        this.floor = floor;
        this.roomType = roomType;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<ReservationItem> getReservationItems() {
        return reservationItems;
    }

    public void setReservationItems(List<ReservationItem> reservationItems) {
        this.reservationItems = reservationItems;
    }
}
