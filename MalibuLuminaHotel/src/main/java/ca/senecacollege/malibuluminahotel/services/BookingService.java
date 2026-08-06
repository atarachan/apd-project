package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.decorators.*;
import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PricingModel;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.repositories.IAddOnRepository;
import ca.senecacollege.malibuluminahotel.repositories.IGuestRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository.ReservationItemDraft;
import ca.senecacollege.malibuluminahotel.repositories.IRoomRepository;
import ca.senecacollege.malibuluminahotel.repositories.IRoomTypeRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookingService implements IBookingService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.13");

    private final IGuestRepository guestRepo;
    private final IRoomRepository roomRepo;
    private final IRoomTypeRepository roomTypeRepo;
    private final IReservationRepository reservationRepo;
    private final IAddOnRepository addOnRepo;

    @Inject
    public BookingService(IGuestRepository guestRepo,
                          IRoomRepository roomRepo,
                          IRoomTypeRepository roomTypeRepo,
                          IReservationRepository reservationRepo,
                          IAddOnRepository addOnRepo) {
        this.guestRepo = guestRepo;
        this.roomRepo = roomRepo;
        this.roomTypeRepo = roomTypeRepo;
        this.reservationRepo = reservationRepo;
        this.addOnRepo = addOnRepo;
    }

    // Calculates the full bill for the current session without touching the DB.
    // Called by GuestCheckoutController to populate the bill summary screen.
    // NOW USES DECORATOR PATTERN for add-on pricing!
    @Override
    public BillSummary calculateBill(BookingSession session) {
        LocalDate checkIn = session.getCheckInDate();
        LocalDate checkOut = session.getCheckOutDate();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        System.out.println("nights: " + nights);

        BigDecimal roomTotal = BigDecimal.ZERO;
        BigDecimal addOnTotal = BigDecimal.ZERO;
        List<BillLineItem> lineItems = new ArrayList<>();
        int roomNumber = 1;

        for (BookingSession.ReservationItemSelection selection : session.getReservationItemSelections()) {
            RoomType roomType = roomTypeRepo.findByName(selection.roomTypeName())
                    .orElseThrow(() -> new IllegalStateException(
                            "Room type not found: " + selection.roomTypeName()));

            BigDecimal itemRoomTotal = calculateRoomTotal(roomType, checkIn, checkOut);
            BookingComponent booking = buildDecoratedBooking(selection, roomType, itemRoomTotal, nights);
            BigDecimal averageNightlyRate = itemRoomTotal
                    .divide(BigDecimal.valueOf(nights), 2, RoundingMode.HALF_UP);

            lineItems.add(new BillLineItem(
                    "Room " + roomNumber + " - " + formatRoomTypeName(roomType),
                    "CAD " + averageNightlyRate + " x " + nights + " night(s)",
                    itemRoomTotal
            ));
            lineItems.addAll(buildAddOnLineItems(selection, nights));

            roomTotal = roomTotal.add(itemRoomTotal);
            addOnTotal = addOnTotal.add(booking.getCost().subtract(itemRoomTotal));
            roomNumber++;
        }

        BigDecimal subtotal = roomTotal.add(addOnTotal);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        return new BillSummary(roomTotal, addOnTotal, subtotal, tax, total, nights, lineItems);
    }

    private List<BillLineItem> buildAddOnLineItems(BookingSession.ReservationItemSelection selection, long nights) {
        List<BillLineItem> lineItems = new ArrayList<>();
        List<AddOn> allAddOns = addOnRepo.findAll();

        for (AddOn addOn : allAddOns) {
            boolean selected = switch (addOn.getName()) {
                case "Daily Breakfast" -> selection.breakfastSelected();
                case "Wi-Fi" -> selection.wifiSelected();
                case "Parking" -> selection.parkingSelected();
                case "Spa Package" -> selection.spaSelected();
                default -> false;
            };

            if (!selected) {
                continue;
            }

            int quantity = addOn.getPricingModel() == PricingModel.PER_NIGHT ? (int) nights : 1;
            BigDecimal lineTotal = addOn.getPrice().multiply(BigDecimal.valueOf(quantity));
            String unit = addOn.getPricingModel() == PricingModel.PER_NIGHT ? "night(s)" : "room";

            lineItems.add(new BillLineItem(
                    "  Add-on - " + addOn.getName(),
                    "CAD " + addOn.getPrice() + " x " + quantity + " " + unit,
                    lineTotal
            ));
        }

        return lineItems;
    }

    private String formatRoomTypeName(RoomType roomType) {
        return switch (roomType.getRoomTypeName()) {
            case SINGLE -> "Single Room";
            case DOUBLE -> "Double Room";
            case PENTHOUSE -> "Penthouse";
        };
    }

    private BigDecimal calculateRoomTotal(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal roomTotal = BigDecimal.ZERO;

        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            PricingStrategy strategy = isWeekend(date)
                    ? new WeekendPricingStrategy()
                    : new StandardPricingStrategy();
            roomTotal = roomTotal.add(strategy.calculateNightlyRate(roomType, date));
        }

        return roomTotal;
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
    private BookingComponent buildDecoratedBooking(BookingSession.ReservationItemSelection selection, RoomType roomType,
            BigDecimal roomTotal, long nights) {
        // Start with the base booking (room cost only)
        String roomDescription = roomType.getRoomTypeName() + " for " + nights + " night(s)";
        BookingComponent booking = new BaseBooking(roomTotal, roomDescription);

        // Load add-ons from database
        List<AddOn> allAddOns = addOnRepo.findAll();

        // Wrap the booking with decorator for each selected add-on
        for (AddOn addOn : allAddOns) {
            boolean selected = switch (addOn.getName()) {
                case "Daily Breakfast" -> selection.breakfastSelected();
                case "Wi-Fi" -> selection.wifiSelected();
                case "Parking" -> selection.parkingSelected();
                case "Spa Package" -> selection.spaSelected();
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
    @Override
    public Reservation createReservation(BookingSession session, BillSummary bill, Payment depositPayment) {

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

        List<ReservationItemDraft> reservationItemDrafts = buildReservationItemDrafts(
                session, (int) bill.nights());

        return reservationRepo.saveFullBooking(
                guest,
                session.getCheckInDate(),
                session.getCheckOutDate(),
                session.getAdults(),
                session.getChildren(),
                reservationItemDrafts,
                bill.subtotal(),
                bill.tax(),
                bill.total(),
                depositPayment);
    }

    private List<ReservationItemDraft> buildReservationItemDrafts(BookingSession session, int nights) {
        List<ReservationItemDraft> drafts = new ArrayList<>();
        Set<Long> assignedRoomIds = new HashSet<>();

        for (BookingSession.ReservationItemSelection selection : session.getReservationItemSelections()) {
            RoomType roomType = roomTypeRepo.findByName(selection.roomTypeName())
                    .orElseThrow(() -> new IllegalStateException(
                            "Room type not found: " + selection.roomTypeName()));

            Room room = roomRepo.findAvailable(
                            selection.roomTypeName(),
                            session.getCheckInDate(),
                            session.getCheckOutDate(),
                            session.getRoomCount())
                    .stream()
                    .filter(candidate -> !assignedRoomIds.contains(candidate.getRoomId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No available " + selection.roomTypeName()
                                    + " rooms for the selected dates."));

            assignedRoomIds.add(room.getRoomId());
            drafts.add(new ReservationItemDraft(
                    room.getRoomId(),
                    roomType.getBaseRate(),
                    buildAddOnQuantities(selection, nights)
            ));
        }

        return drafts;
    }

    // Maps each selected add-on's DB id to the correct quantity.
    private Map<Long, Integer> buildAddOnQuantities(BookingSession.ReservationItemSelection selection, int nights) {
        Map<Long, Integer> map = new LinkedHashMap<>();
        List<AddOn> allAddOns = addOnRepo.findAll();

        for (AddOn addOn : allAddOns) {
            switch (addOn.getName()) {
                case "Daily Breakfast" -> {
                    if (selection.breakfastSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Wi-Fi" -> {
                    if (selection.wifiSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Parking" -> {
                    if (selection.parkingSelected())
                        map.put(addOn.getAddOnId(), nights);
                }
                case "Spa Package" -> {
                    if (selection.spaSelected())
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

}
