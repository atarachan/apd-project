package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import ca.senecacollege.malibuluminahotel.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// TEST TO VERIFY REPOSITORY WORKS -- TO BE DELETED LATER!!!!
public final class RepositoryTest {

    private RepositoryTest() {
    }

    public static void testAddOnRepository() {
        IAddOnRepository repository = new AddOnRepositoryImpl();

        AddOn addOn = new AddOn(
                "Airport Shuttle",
                new BigDecimal("45.00"),
                "Transportation between the hotel and airport"
        );

        // CREATE
        repository.save(addOn);

        Long id = addOn.getAddOnId();

        System.out.println("Saved AddOn ID: " + id);

        // READ BY ID
        AddOn found = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Could not find saved AddOn"
                        )
                );

        System.out.println("Found by ID: " + found.getName());

        // UPDATE
        found.setPrice(new BigDecimal("49.99"));

        AddOn updated = repository.update(found);

        System.out.println("Updated price: " + updated.getPrice());

        // CUSTOM QUERY
        repository.findByName("Airport Shuttle")
                .ifPresent(result ->
                        System.out.println(
                                "Found by name: " + result.getName()
                        )
                );

        // FIND ALL
        System.out.println(
                "Total AddOns: " + repository.findAll().size()
        );

        // DELETE
        repository.deleteById(id);

        boolean stillExists = repository.findById(id).isPresent();

        System.out.println(
                "Still exists after delete: " + stillExists
        );
    }

    public static void testReservationRepository() {

        IGuestRepository guestRepository = new GuestRepositoryImpl();
        IReservationRepository reservationRepository =
                new ReservationRepositoryImpl();

        String testEmail = "reservation.test@malibulumina.ca";

        /*
         * Clean up data from a previous interrupted test.
         * Reservations must be deleted before their associated guest.
         */
        guestRepository.findByEmail(testEmail).ifPresent(existingGuest -> {
            List<Reservation> existingReservations =
                    reservationRepository.findByGuest(existingGuest);

            existingReservations.forEach(reservationRepository::delete);
            guestRepository.delete(existingGuest);
        });

        Guest guest = new Guest(
                "Jordan",
                "Taylor",
                testEmail,
                "416-555-0199"
        );

        Reservation reservation = new Reservation(
                guest,
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10)
        );

        try {
            // Save the Guest first because Reservation does not cascade Guest
            guestRepository.save(guest);

            System.out.println("Saved Guest ID: " + guest.getGuestId());

            // CREATE
            reservationRepository.save(reservation);

            Long reservationId = reservation.getReservationId();

            System.out.println(
                    "Saved Reservation ID: " + reservationId
            );

            // READ BY ID
            Reservation foundById =
                    reservationRepository.findById(reservationId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Reservation could not be found by ID"
                                    )
                            );

            System.out.println(
                    "Found by ID: " + foundById.getReservationId()
            );

            System.out.println(
                    "Check-in: " + foundById.getCheckInDate()
            );

            System.out.println(
                    "Check-out: " + foundById.getCheckOutDate()
            );

            // UPDATE
            foundById.setStatus(ReservationStatus.CONFIRMED);

            Reservation updatedReservation =
                    reservationRepository.update(foundById);

            System.out.println(
                    "Updated status: " + updatedReservation.getStatus()
            );

            // FIND BY GUEST
            List<Reservation> reservationsByGuest =
                    reservationRepository.findByGuest(guest);

            System.out.println(
                    "Reservations found for guest: "
                            + reservationsByGuest.size()
            );

            // FIND BY STATUS
            List<Reservation> confirmedReservations =
                    reservationRepository.findByStatus(
                            ReservationStatus.CONFIRMED
                    );

            boolean foundInStatusResults =
                    confirmedReservations.stream()
                            .anyMatch(r ->
                                    r.getReservationId()
                                            .equals(reservationId)
                            );

            System.out.println(
                    "Found in CONFIRMED reservations: "
                            + foundInStatusResults
            );

            // FIND BY CHECK-IN DATE
            List<Reservation> reservationsByCheckIn =
                    reservationRepository.findByCheckInDate(
                            reservation.getCheckInDate()
                    );

            boolean foundByCheckIn =
                    reservationsByCheckIn.stream()
                            .anyMatch(r ->
                                    r.getReservationId()
                                            .equals(reservationId)
                            );

            System.out.println(
                    "Found by check-in date: " + foundByCheckIn
            );

            // FIND BY DATE RANGE
            List<Reservation> reservationsByDateRange =
                    reservationRepository.findByDateRange(
                            LocalDate.now().plusDays(6),
                            LocalDate.now().plusDays(8)
                    );

            boolean foundInDateRange =
                    reservationsByDateRange.stream()
                            .anyMatch(r ->
                                    r.getReservationId()
                                            .equals(reservationId)
                            );

            System.out.println(
                    "Found in date range: " + foundInDateRange
            );

            // FIND ALL
            System.out.println(
                    "Total Reservations: "
                            + reservationRepository.findAll().size()
            );

            // DELETE RESERVATION
            reservationRepository.deleteById(reservationId);

            boolean reservationStillExists =
                    reservationRepository.findById(reservationId)
                            .isPresent();

            System.out.println(
                    "Reservation still exists after delete: "
                            + reservationStillExists
            );

        } finally {
            /*
             * Cleanup even if part of the test fails.
             * Delete Reservation records before deleting the Guest.
             */
            if (reservation.getReservationId() != null) {
                reservationRepository
                        .findById(reservation.getReservationId())
                        .ifPresent(reservationRepository::delete);
            }

            if (guest.getGuestId() != null) {
                guestRepository
                        .findById(guest.getGuestId())
                        .ifPresent(guestRepository::delete);
            }
        }
    }

    public static void testReservationItemRepository() {

        IGuestRepository guestRepository =
                new GuestRepositoryImpl();

        IRoomTypeRepository roomTypeRepository =
                new RoomTypeRepositoryImpl();

        IRoomRepository roomRepository =
                new RoomRepositoryImpl();

        IReservationRepository reservationRepository =
                new ReservationRepositoryImpl();

        IReservationItemRepository reservationItemRepository =
                new ReservationItemRepositoryImpl();

        /*
         * Using values()[0] prevents the test from depending on specific
         * enum constant names.
         */
        RoomTypeName testRoomTypeName = RoomTypeName.values()[0];
        RoomStatus testRoomStatus = RoomStatus.values()[0];

        String testEmail = "reservationitem.test@malibulumina.ca";
        String testRoomNumber = "TEST-901";

        Guest guest = null;
        RoomType roomType = null;
        Room room = null;
        Reservation reservation = null;
        ReservationItem reservationItem = null;

        try {
            /*
             * Clean up records left behind by an earlier interrupted test.
             */
            guestRepository.findByEmail(testEmail).ifPresent(existingGuest -> {
                List<Reservation> existingReservations =
                        reservationRepository.findByGuest(existingGuest);

                for (Reservation existingReservation : existingReservations) {
                    List<ReservationItem> existingItems =
                            reservationItemRepository.findByReservation(
                                    existingReservation
                            );

                    existingItems.forEach(reservationItemRepository::delete);
                    reservationRepository.delete(existingReservation);
                }

                guestRepository.delete(existingGuest);
            });

            roomRepository.findByRoomNumber(testRoomNumber)
                    .ifPresent(existingRoom -> {
                        List<ReservationItem> existingItems =
                                reservationItemRepository.findByRoom(
                                        existingRoom
                                );

                        existingItems.forEach(
                                reservationItemRepository::delete
                        );

                        roomRepository.delete(existingRoom);
                    });

            // Create and save Guest
            guest = new Guest(
                    "Morgan",
                    "Reed",
                    testEmail,
                    "416-555-0145"
            );

            guestRepository.save(guest);

            System.out.println(
                    "Saved Guest ID: " + guest.getGuestId()
            );

            /*
             * Try to reuse an existing RoomType with the selected enum name.
             * Otherwise, create a new one.
             */
            roomType = roomTypeRepository
                    .findByName(testRoomTypeName)
                    .orElseGet(() -> {
                        RoomType newRoomType = new RoomType(
                                testRoomTypeName,
                                "Room type used by repository test",
                                new BigDecimal("175.00"),
                                4
                        );

                        return roomTypeRepository.save(newRoomType);
                    });

            System.out.println(
                    "Using RoomType ID: " + roomType.getRoomTypeId()
            );

            // Create and save Room
            room = new Room(
                    testRoomNumber,
                    testRoomStatus,
                    9,
                    roomType
            );

            roomRepository.save(room);

            System.out.println(
                    "Saved Room ID: " + room.getRoomId()
            );

            // Create and save Reservation
            reservation = new Reservation(
                    guest,
                    LocalDate.now().plusDays(10),
                    LocalDate.now().plusDays(13)
            );

            reservationRepository.save(reservation);

            System.out.println(
                    "Saved Reservation ID: "
                            + reservation.getReservationId()
            );

            // CREATE
            reservationItem = new ReservationItem(
                    reservation,
                    room,
                    new BigDecimal("199.99"),
                    2
            );

            reservationItemRepository.save(reservationItem);

            Long reservationItemId =
                    reservationItem.getReservationItemId();

            System.out.println(
                    "Saved ReservationItem ID: " + reservationItemId
            );

            // READ BY ID
            ReservationItem foundById =
                    reservationItemRepository
                            .findById(reservationItemId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "ReservationItem was not found by ID"
                                    )
                            );

            System.out.println(
                    "Found by ID: "
                            + foundById.getReservationItemId()
            );

            System.out.println(
                    "Nightly rate: " + foundById.getNightlyRate()
            );

            System.out.println(
                    "Guest count: " + foundById.getGuestCount()
            );

            System.out.println(
                    "Room number: "
                            + foundById.getRoom().getRoomNumber()
            );

            // UPDATE
            foundById.setNightlyRate(new BigDecimal("229.99"));
            foundById.setGuestCount(3);

            ReservationItem updatedItem =
                    reservationItemRepository.update(foundById);

            System.out.println(
                    "Updated nightly rate: "
                            + updatedItem.getNightlyRate()
            );

            System.out.println(
                    "Updated guest count: "
                            + updatedItem.getGuestCount()
            );

            // FIND BY RESERVATION
            List<ReservationItem> itemsByReservation =
                    reservationItemRepository.findByReservation(
                            reservation
                    );

            boolean foundByReservation =
                    itemsByReservation.stream()
                            .anyMatch(item ->
                                    item.getReservationItemId()
                                            .equals(reservationItemId)
                            );

            System.out.println(
                    "Found by reservation: " + foundByReservation
            );

            // FIND BY ROOM
            List<ReservationItem> itemsByRoom =
                    reservationItemRepository.findByRoom(room);

            boolean foundByRoom =
                    itemsByRoom.stream()
                            .anyMatch(item ->
                                    item.getReservationItemId()
                                            .equals(reservationItemId)
                            );

            System.out.println(
                    "Found by room: " + foundByRoom
            );

            // FIND BY RESERVATION AND ROOM
            Optional<ReservationItem> foundByReservationAndRoom =
                    reservationItemRepository
                            .findByReservationAndRoom(
                                    reservation,
                                    room
                            );

            System.out.println(
                    "Found by reservation and room: "
                            + foundByReservationAndRoom.isPresent()
            );

            // FIND ALL
            List<ReservationItem> allItems =
                    reservationItemRepository.findAll();

            System.out.println(
                    "Total ReservationItems: " + allItems.size()
            );

            // DELETE
            reservationItemRepository.deleteById(reservationItemId);

            boolean stillExists =
                    reservationItemRepository
                            .findById(reservationItemId)
                            .isPresent();

            System.out.println(
                    "ReservationItem still exists after delete: "
                            + stillExists
            );

        } finally {
            /*
             * Delete records in reverse dependency order.
             * Each lookup prevents an error if the main test already deleted it.
             */
            if (reservationItem != null
                    && reservationItem.getReservationItemId() != null) {

                reservationItemRepository
                        .findById(
                                reservationItem.getReservationItemId()
                        )
                        .ifPresent(
                                reservationItemRepository::delete
                        );
            }

            if (reservation != null
                    && reservation.getReservationId() != null) {

                reservationRepository
                        .findById(reservation.getReservationId())
                        .ifPresent(reservationRepository::delete);
            }

            if (room != null && room.getRoomId() != null) {
                roomRepository
                        .findById(room.getRoomId())
                        .ifPresent(roomRepository::delete);
            }

            if (guest != null && guest.getGuestId() != null) {
                guestRepository
                        .findById(guest.getGuestId())
                        .ifPresent(guestRepository::delete);
            }

            /*
             * The RoomType is intentionally not deleted because the test may
             * have reused an existing RoomType record.
             */
        }
    }

    public static void testReservationItemAddOnRepository() {

        IGuestRepository guestRepository =
                new GuestRepositoryImpl();

        IRoomTypeRepository roomTypeRepository =
                new RoomTypeRepositoryImpl();

        IRoomRepository roomRepository =
                new RoomRepositoryImpl();

        IReservationRepository reservationRepository =
                new ReservationRepositoryImpl();

        IReservationItemRepository reservationItemRepository =
                new ReservationItemRepositoryImpl();

        IAddOnRepository addOnRepository =
                new AddOnRepositoryImpl();

        IReservationItemAddOnRepository reservationItemAddOnRepository =
                new ReservationItemAddOnRepositoryImpl();

        String testEmail =
                "reservationaddon.test@malibulumina.ca";

        String testRoomNumber =
                "TEST-ADDON-901";

        String testAddOnName =
                "Repository Test Breakfast";

        Guest guest = null;
        RoomType roomType = null;
        Room room = null;
        Reservation reservation = null;
        ReservationItem reservationItem = null;
        AddOn addOn = null;
        ReservationItemAddOn reservationItemAddOn = null;

        /*
         * This tracks whether the test created the RoomType.
         * We should not delete a RoomType that already existed.
         */
        boolean roomTypeCreatedByTest = false;

        try {
            /*
             * Create and save Guest
             */
            guest = new Guest(
                    "Jordan",
                    "Blake",
                    testEmail,
                    "416-555-0198"
            );

            guest = guestRepository.save(guest);

            System.out.println(
                    "Saved Guest ID: " + guest.getGuestId()
            );

            /*
             * Use an existing RoomType if possible.
             *
             * Change this section if your RoomType repository method
             * or constructor uses different names.
             */
            RoomTypeName roomTypeName =
                    RoomTypeName.values()[0];

            Optional<RoomType> existingRoomType =
                    roomTypeRepository.findByName(roomTypeName);

            if (existingRoomType.isPresent()) {
                roomType = existingRoomType.get();

            } else {
                roomType = new RoomType(
                        roomTypeName,
                        "Room type used for add-on repository testing",
                        new BigDecimal("180.00"),
                        4
                );

                roomType = roomTypeRepository.save(roomType);
                roomTypeCreatedByTest = true;
            }

            System.out.println(
                    "Using RoomType ID: "
                            + roomType.getRoomTypeId()
            );

            /*
             * Create and save Room
             */
            room = new Room(
                    testRoomNumber,
                    RoomStatus.values()[0],
                    9,
                    roomType
            );

            room = roomRepository.save(room);

            System.out.println(
                    "Saved Room ID: " + room.getRoomId()
            );

            /*
             * Create and save Reservation
             */
            reservation = new Reservation(
                    guest,
                    LocalDate.now().plusDays(14),
                    LocalDate.now().plusDays(17)
            );

            reservation = reservationRepository.save(reservation);

            System.out.println(
                    "Saved Reservation ID: "
                            + reservation.getReservationId()
            );

            /*
             * Create and save ReservationItem
             */
            reservationItem = new ReservationItem(
                    reservation,
                    room,
                    new BigDecimal("219.99"),
                    2
            );

            reservationItem =
                    reservationItemRepository.save(reservationItem);

            System.out.println(
                    "Saved ReservationItem ID: "
                            + reservationItem.getReservationItemId()
            );

            /*
             * Create and save AddOn.
             *
             * This assumes:
             *
             * AddOn(String name, String description, BigDecimal price)
             *
             * Adjust this constructor call if your AddOn entity uses
             * different arguments.
             */
            addOn = new AddOn(
                    testAddOnName,
                    new BigDecimal("25.00"),"Breakfast package created for repository testing"
            );

            addOn = addOnRepository.save(addOn);

            System.out.println(
                    "Saved AddOn ID: " + addOn.getAddOnId()
            );

            /*
             * CREATE ReservationItemAddOn
             *
             * Quantity = 2
             * Add-on price = $25.00
             * Item total = $50.00
             */
            int initialQuantity = 2;

            BigDecimal initialItemTotal =
                    addOn.getPrice().multiply(
                            BigDecimal.valueOf(initialQuantity)
                    );

            reservationItemAddOn =
                    new ReservationItemAddOn(
                            reservationItem,
                            addOn,
                            initialQuantity,
                            initialItemTotal
                    );

            reservationItemAddOn =
                    reservationItemAddOnRepository.save(
                            reservationItemAddOn
                    );

            Long reservationAddOnId =
                    reservationItemAddOn.getReservationAddOnId();

            System.out.println(
                    "Saved ReservationItemAddOn ID: "
                            + reservationAddOnId
            );

            /*
             * READ BY ID
             */
            ReservationItemAddOn foundById =
                    reservationItemAddOnRepository
                            .findById(reservationAddOnId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "ReservationItemAddOn was not found by ID"
                                    )
                            );

            System.out.println(
                    "Found by ID: "
                            + foundById.getReservationAddOnId()
            );

            System.out.println(
                    "Initial quantity: "
                            + foundById.getQuantity()
            );

            System.out.println(
                    "Initial item total: "
                            + foundById.getItemTotal()
            );

            /*
             * Verify initial values
             */
            if (foundById.getQuantity() != 2) {
                throw new IllegalStateException(
                        "Expected quantity 2, but found "
                                + foundById.getQuantity()
                );
            }

            if (foundById.getItemTotal()
                    .compareTo(new BigDecimal("50.00")) != 0) {

                throw new IllegalStateException(
                        "Expected item total 50.00, but found "
                                + foundById.getItemTotal()
                );
            }

            /*
             * UPDATE
             *
             * Change quantity from 2 to 3.
             * The stored item total should therefore become $75.00.
             */
            int updatedQuantity = 3;

            BigDecimal updatedItemTotal =
                    addOn.getPrice().multiply(
                            BigDecimal.valueOf(updatedQuantity)
                    );

            foundById.setQuantity(updatedQuantity);
            foundById.setItemTotal(updatedItemTotal);

            ReservationItemAddOn updatedRecord =
                    reservationItemAddOnRepository.update(foundById);

            System.out.println(
                    "Updated quantity: "
                            + updatedRecord.getQuantity()
            );

            System.out.println(
                    "Updated item total: "
                            + updatedRecord.getItemTotal()
            );

            /*
             * Reload the record to verify the update was persisted,
             * rather than only changing the Java object.
             */
            ReservationItemAddOn reloadedRecord =
                    reservationItemAddOnRepository
                            .findById(reservationAddOnId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Updated ReservationItemAddOn was not found"
                                    )
                            );

            if (reloadedRecord.getQuantity() != 3) {
                throw new IllegalStateException(
                        "Updated quantity was not persisted"
                );
            }

            if (reloadedRecord.getItemTotal()
                    .compareTo(new BigDecimal("75.00")) != 0) {

                throw new IllegalStateException(
                        "Updated item total was not persisted"
                );
            }

            /*
             * FIND BY RESERVATION ITEM
             */
            List<ReservationItemAddOn> resultsByReservationItem =
                    reservationItemAddOnRepository
                            .findByReservationItem(reservationItem);

            boolean foundByReservationItem =
                    resultsByReservationItem.stream()
                            .anyMatch(record ->
                                    record.getReservationAddOnId()
                                            .equals(reservationAddOnId)
                            );

            System.out.println(
                    "Found by reservation item: "
                            + foundByReservationItem
            );

            if (!foundByReservationItem) {
                throw new IllegalStateException(
                        "findByReservationItem did not return the record"
                );
            }

            /*
             * FIND BY ADD-ON
             */
            List<ReservationItemAddOn> resultsByAddOn =
                    reservationItemAddOnRepository.findByAddOn(addOn);

            boolean foundByAddOn =
                    resultsByAddOn.stream()
                            .anyMatch(record ->
                                    record.getReservationAddOnId()
                                            .equals(reservationAddOnId)
                            );

            System.out.println(
                    "Found by add-on: " + foundByAddOn
            );

            if (!foundByAddOn) {
                throw new IllegalStateException(
                        "findByAddOn did not return the record"
                );
            }

            /*
             * FIND BY RESERVATION ITEM AND ADD-ON
             */
            Optional<ReservationItemAddOn>
                    foundByReservationItemAndAddOn =
                    reservationItemAddOnRepository
                            .findByReservationItemAndAddOn(
                                    reservationItem,
                                    addOn
                            );

            System.out.println(
                    "Found by reservation item and add-on: "
                            + foundByReservationItemAndAddOn.isPresent()
            );

            if (foundByReservationItemAndAddOn.isEmpty()) {
                throw new IllegalStateException(
                        "findByReservationItemAndAddOn did not return the record"
                );
            }

            if (!foundByReservationItemAndAddOn
                    .get()
                    .getReservationAddOnId()
                    .equals(reservationAddOnId)) {

                throw new IllegalStateException(
                        "findByReservationItemAndAddOn returned the wrong record"
                );
            }

            /*
             * FIND ALL
             */
            List<ReservationItemAddOn> allRecords =
                    reservationItemAddOnRepository.findAll();

            boolean foundInAll =
                    allRecords.stream()
                            .anyMatch(record ->
                                    record.getReservationAddOnId()
                                            .equals(reservationAddOnId)
                            );

            System.out.println(
                    "Found in findAll: " + foundInAll
            );

            if (!foundInAll) {
                throw new IllegalStateException(
                        "findAll did not return the record"
                );
            }

            /*
             * DELETE
             */
            reservationItemAddOnRepository.deleteById(
                    reservationAddOnId
            );

            boolean stillExists =
                    reservationItemAddOnRepository
                            .findById(reservationAddOnId)
                            .isPresent();

            System.out.println(
                    "ReservationItemAddOn still exists after delete: "
                            + stillExists
            );

            if (stillExists) {
                throw new IllegalStateException(
                        "ReservationItemAddOn was not deleted"
                );
            }

            System.out.println(
                    "ReservationItemAddOnRepository test passed."
            );

        } finally {
            /*
             * Cleanup must happen in reverse dependency order.
             */

            if (reservationItemAddOn != null
                    && reservationItemAddOn
                    .getReservationAddOnId() != null) {

                reservationItemAddOnRepository
                        .findById(
                                reservationItemAddOn
                                        .getReservationAddOnId()
                        )
                        .ifPresent(
                                reservationItemAddOnRepository::delete
                        );
            }

            if (reservationItem != null
                    && reservationItem
                    .getReservationItemId() != null) {

                reservationItemRepository
                        .findById(
                                reservationItem
                                        .getReservationItemId()
                        )
                        .ifPresent(
                                reservationItemRepository::delete
                        );
            }

            if (reservation != null
                    && reservation.getReservationId() != null) {

                reservationRepository
                        .findById(
                                reservation.getReservationId()
                        )
                        .ifPresent(
                                reservationRepository::delete
                        );
            }

            if (room != null
                    && room.getRoomId() != null) {

                roomRepository
                        .findById(room.getRoomId())
                        .ifPresent(roomRepository::delete);
            }

            if (guest != null
                    && guest.getGuestId() != null) {

                guestRepository
                        .findById(guest.getGuestId())
                        .ifPresent(guestRepository::delete);
            }

            if (addOn != null
                    && addOn.getAddOnId() != null) {

                addOnRepository
                        .findById(addOn.getAddOnId())
                        .ifPresent(addOnRepository::delete);
            }

            /*
             * Only delete the RoomType if this test created it.
             */
            if (roomTypeCreatedByTest
                    && roomType != null
                    && roomType.getRoomTypeId() != null) {

                roomTypeRepository
                        .findById(roomType.getRoomTypeId())
                        .ifPresent(roomTypeRepository::delete);
            }
        }
    }
}
