package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IReservationRepository extends IRepository<Reservation, Long> {

    List<Reservation> findByGuest(Guest guest);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCheckInDate(LocalDate checkInDate);

    List<Reservation> findByDateRange(
            LocalDate startDate, LocalDate endDate
    );

    // Returns all reservations with reservationItems eagerly loaded (avoids LazyInitializationException).
    List<Reservation> findAllWithDetails();

    // Saves guest + reservation + room item + add-ons + bill in one transaction.
    // addOnQuantities maps each add-on's DB id to the quantity being booked.
    Reservation saveFullBooking(
            Guest guest,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            Long roomId,
            BigDecimal nightlyRate,
            Map<Long, Integer> addOnQuantities,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total
    );
}
