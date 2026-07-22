package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RoomTypes")
public class RoomType implements Serializable {

    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoomTypeName roomTypeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal baseRate;

    @Column(nullable = false)
    private int maxOccupancy;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    public RoomType() {
    }

    public RoomType(RoomTypeName name, String description, BigDecimal baseRate, int maxOccupancy) {
        this.roomTypeName = name;
        this.description = description;
        this.baseRate = baseRate;
        this.maxOccupancy = maxOccupancy;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public RoomTypeName getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(RoomTypeName roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}
