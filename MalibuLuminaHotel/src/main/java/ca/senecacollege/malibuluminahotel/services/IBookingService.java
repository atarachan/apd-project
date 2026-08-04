package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.models.Reservation;

public interface IBookingService {

    BillSummary calculateBill(BookingSession session);

    Reservation createReservation(BookingSession session, BillSummary bill);
}
