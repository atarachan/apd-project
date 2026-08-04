package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.decorators.*;
import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.enums.PricingModel;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.repositories.IAddOnRepository;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomTypeRepository;
import ca.senecacollege.malibuluminahotel.repositories.AddOnRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.GuestRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.ReservationRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.RoomRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.RoomTypeRepositoryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.13");

    private final IGuestRepository guestRepo;
    private final IRoomRepository roomRepo;
    private final IRoomTypeRepository roomTypeRepo;
    private final IReservationRepository reservationRepo;
    private final IAddOnRepository addOnRepo;

    public BookingService() {
        this.guestRepo = new GuestRepositoryImpl();
        this.roomRepo = new RoomRepositoryImpl();
        this.roomTypeRepo = new RoomTypeRepositoryImpl();
        this.reservationRepo = new ReservationRepositoryImpl();
        this.addOnRepo = new AddOnRepositoryImpl();
    }

    // Calculates the full bill for the current session without touching the DB.
    // Called by GuestCheckoutController to populate the bill summary screen.
    // NOW USES DECORATOR PATTERN for add-on pricing!
    public BillSummary calculateBill(BookingSession session) {

        RoomType roomType = roomTypeRepo.findByName(session.getSelectedRoomTypeName())
                .orElseThrow(() -> new IllegalStateException(
                        "Room type not found: " + session.getSelectedRoomTypeName()));

        LocalDate checkIn = session.getCheckInDate();
        LocalDate checkOut = session.getCheckOutDate();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        // Apply the correct pricing strategy per night (Standard vs Weekend)
        BigDecimal roomTotal = BigDecimal.ZERO;
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            PricingStrategy strategy = isWeekend(date)
                    ? new WeekendPricingStrategy()
                    : new StandardPricingStrategy();
            roomTotal = roomTotal.add(strategy.calculateNightlyRate(roomType, date));
        }

        // DECORATOR PATTERN: Build the booking component by wrapping with decorators
        BookingComponent booking = buildDecoratedBooking(session, roomType, roomTotal, nights);

        // Extract costs from the decorated booking
        BigDecimal totalWithAddOns = booking.getCost();
        BigDecimal addOnTotal = totalWithAddOns.subtract(roomTotal);

        BigDecimal subtotal = totalWithAddOns;
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        return new BillSummary(roomTotal, addOnTotal, subtotal, tax, total, nights);
    }

    /**
     * Builds a decorated booking component using the Decorator pattern.
     * Starts with a base booking and wraps it with add-on decorators based on
     * selections.
     * 
     * @param session   the current booking session
     * @param roomType  the selected room type
     * @param roomTotal the total room cost
     * @param nights    the number of nights
     * @return a fully decorated booking component
     */
    private BookingComponent buildDecoratedBooking(BookingSession session, RoomType roomType,
            BigDecimal roomTotal, long nights) {
        // Start with the base booking (room cost only)
        String roomDescription = roomType.getRoomTypeName() + " for " + nights + " night(s)";
        BookingComponent booking = new BaseBooking(roomTotal, roomDescription);

        // Load add-ons from database
        List<AddOn> allAddOns = addOnRepo.findAll();

        // Wrap the booking with decorator for each selected add-on
        for (AddOn addOn : allAddOns) {
            boolean selected = switch (addOn.getName()) {
                case "Daily Breakfast" -> session.isBreakfastSelected();
                case "Wi-Fi" -> session.isWifiSelected();
                case "Parking" -> session.isParkingSelected();
                case "Spa Package" -> session.isSpaSelected();
                default -> false;
            };

            if (!selected)
                continue;

            // Calculate the add-on cost based on pricing model
            BigDecimal addOnCost = addOn.getPricingModel() == PricingModel.PER_NIGHT
                    ? addOn.getPrice().multiply(BigDecimal.valueOf(nights))
                    : addOn.getPrice();

            // Wrap the booking with the appropriate decorator
            booking = switch (addOn.getName()) {
                case "Daily Breakfast" -> new BreakfastDecorator(booking, addOnCost);
                case "Wi-Fi" -> new WifiDecorator(booking, addOnCost);
                case "Parking" -> new ParkingDecorator(booking, addOnCost);
                case "Spa Package" -> new SpaDecorator(booking, addOnCost);
                default -> booking; // No decorator for unknown add-ons
            };
        }

        return booking;
    }

    // Persists the full booking to the database.
    // Called by GuestCheckoutController when the guest confirms.
    public Reservation createReservation(BookingSession session, BillSummary bill) {

        // Find existing guest by email or build a new one
        Guest guest = guestRepo.findByEmail(session.getGuestEmail())
                .orElseGet(() -> {
                    Guest g = new Guest();
                    g.setFirstName(session.getGuestFirstName());
                    g.setLastName(session.getGuestLastName());
                    g.setEmail(session.getGuestEmail());
                    g.setPhone(session.getGuestPhone());
                    return g;
                });

        // Find an available room of the selected type
        Room room = roomRepo.findFirstAvailable(
                session.getSelectedRoomTypeName(),
                session.getCheckInDate(),
                session.getCheckOutDate()).orElseThrow(
                        () -> new IllegalStateException(
                                "No available " + session.getSelectedRoomTypeName()
                                        + " rooms for the selected dates."));

        RoomType roomType = roomTypeRepo.findByName(session.getSelectedRoomTypeName())
                .orElseThrow(() -> new IllegalStateException("Room type not found."));

        Map<Long, Integer> addOnQuantities = buildAddOnQuantities(session, (int) bill.nights());

        return reservationRepo.saveFullBooking(
                guest,
                session.getCheckInDate(),
                session.getCheckOutDate(),
                session.getAdults(),
                session.getChildren(),
                room.getRoomId(),
                roomType.getBaseRate(),
                addOnQuantities,
                bill.subtotal(),
                bill.tax(),
                bill.total());
    }

    // Maps each selected add-on's DB id to the correct quantity.
    private Map<Long, Integer> buildAddOnQuantities(BookingSession session, int nights) {
        Map<Long, Integer> map = new LinkedHashMap<>();
        List<AddOn> allAddOns = addOnRepo.findAll();

        for (AddOn addOn : allAddOns) {
            switch (addOn.getName()) {
                case "Daily Breakfast" -> {
                    if (session.isBreakfastSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Wi-Fi" -> {
                    if (session.isWifiSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Parking" -> {
                    if (session.isParkingSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Spa Package" -> {
                    if (session.isSpaSelected())
                        map.put(addOn.getAddOnId(), 1);
                }
            }
        }

        return map;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    // Immutable result of a bill calculation — passed from controller to
    // createReservation()
    public record BillSummary(
            BigDecimal roomTotal,
            BigDecimal addOnTotal,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total,
            long nights) {
    }
}
