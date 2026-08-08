package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IReservationRepository extends IRepository<Reservation, Long> {

    List<Reservation> findByGuest(Guest guest);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByCheckInDate(LocalDate checkInDate);

    List<Reservation> findByDateRange(
            LocalDate startDate, LocalDate endDate
    );

    // Returns all reservations with reservationItems eagerly loaded (avoids LazyInitializationException).
    List<Reservation> findAllWithDetails();

    Optional<Reservation> findByIdWithDetails(Long reservationId);

    Reservation updateReservationDetails(ReservationEditDraft draft);

    Reservation checkoutReservation(
            Long reservationId,
            PaymentMethod paymentMethod,
            BigDecimal paymentAmount,
            BigDecimal discountPercent
    );

    // Saves guest + reservation + room items + add-ons + bill in one transaction.
    Reservation saveFullBooking(
            Guest guest,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            List<ReservationItemDraft> reservationItemDrafts,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total,
            Payment depositPayment
    );

    record ReservationItemDraft(
            Long roomId,
            BigDecimal nightlyRate,
            Map<Long, Integer> addOnQuantities
    ) {
    }

    record ReservationEditDraft(
            Long reservationId,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            ReservationStatus status,
            List<ReservationItemDraft> reservationItemDrafts,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total
    ) {
    }
}
