package ca.senecacollege.malibuluminahotel.factories;

import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import java.math.BigDecimal;

public class RoomFactory {

    private RoomFactory() {}

    // Factory method: builds a fully configured RoomType based on the enum value
    public static RoomType createRoomType(RoomTypeName typeName) {
        return switch (typeName) {
            case SINGLE -> new RoomType(
                    RoomTypeName.SINGLE,
                    "Cozy queen-bed room with ocean-view balcony. Fits up to 2 guests.",
                    new BigDecimal("120.00"),
                    2
            );
            case DOUBLE -> new RoomType(
                    RoomTypeName.DOUBLE,
                    "Spacious room with two queen beds and a private balcony. Fits up to 4 guests.",
                    new BigDecimal("180.00"),
                    4
            );
            case PENTHOUSE -> new RoomType(
                    RoomTypeName.PENTHOUSE,
                    "Luxury penthouse suite with panoramic ocean views and complimentary breakfast. Fits up to 2 guests.",
                    new BigDecimal("350.00"),
                    2
            );
        };
    }

    // Factory method: builds a Room linked to an existing RoomType
    public static Room createRoom(RoomType roomType, String roomNumber, int floor) {
        return new Room(roomNumber, RoomStatus.AVAILABLE, floor, roomType);
    }
}
