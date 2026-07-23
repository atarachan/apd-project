package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import java.util.List;
import java.util.Optional;

public interface IRoomTypeRepository extends IRepository<RoomType, Long> {

    Optional<RoomType> findByName(RoomTypeName roomTypeName);

    List<RoomType> findByMinimumOccupancy(int occupancy);
}
