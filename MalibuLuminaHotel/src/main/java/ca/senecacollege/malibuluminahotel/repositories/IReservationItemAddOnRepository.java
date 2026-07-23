package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.ReservationItemAddOn;
import java.util.List;
import java.util.Optional;

public interface IReservationItemAddOnRepository extends IRepository<ReservationItemAddOn, Long> {

    List<ReservationItemAddOn> findByReservationItem(
            ReservationItem reservationItem
    );

    List<ReservationItemAddOn> findByAddOn(
            AddOn addOn
    );

    Optional<ReservationItemAddOn> findByReservationItemAndAddOn(
            ReservationItem reservationItem,
            AddOn addOn
    );
}
