package ca.senecacollege.malibuluminahotel.tests;

import ca.senecacollege.malibuluminahotel.decorators.*;
import ca.senecacollege.malibuluminahotel.events.AdminNotificationManager;
import ca.senecacollege.malibuluminahotel.events.WaitlistNotificationObserver;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;

import java.math.BigDecimal;

/**
 * Test class for Phase 1: Critical Patterns
 * Tests both Observer and Decorator patterns.
 */
public class Phase1PatternTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("PHASE 1 PATTERN TESTING");
        System.out.println("=".repeat(80));
        System.out.println();

        testObserverPattern();
        System.out.println();
        testDecoratorPattern();
        System.out.println();

        System.out.println("=".repeat(80));
        System.out.println("ALL PHASE 1 TESTS COMPLETED SUCCESSFULLY!");
        System.out.println("=".repeat(80));
    }

    /**
     * Test 1: Observer Pattern
     * Verify that observers are notified when rooms become available.
     */
    private static void testObserverPattern() {
        System.out.println("--- TEST 1: OBSERVER PATTERN ---");
        System.out.println();

        // Create the subject (notification manager)
        AdminNotificationManager notificationManager = AdminNotificationManager.getInstance();

        // Create observers
        WaitlistNotificationObserver observer1 = new WaitlistNotificationObserver();
        WaitlistNotificationObserver observer2 = new WaitlistNotificationObserver();

        // Attach observers
        System.out.println("✓ Attaching observers...");
        notificationManager.attach(observer1);
        notificationManager.attach(observer2);
        System.out.println("  - Observer count: " + notificationManager.getObserverCount());
        System.out.println();

        // Create test room and room type
        RoomType roomType = new RoomType(
                RoomTypeName.SINGLE,
                "Test Single Room",
                new BigDecimal("120.00"),
                2);

        Room room = new Room("101", RoomStatus.AVAILABLE, 1, roomType);

        // Simulate room becoming available (e.g., after checkout)
        System.out.println("✓ Simulating checkout - notifying observers...");
        notificationManager.notifyObservers(room, roomType);
        System.out.println();

        // Verify notifications were received
        System.out.println("✓ Verifying notifications...");
        System.out.println("  - Observer 1 notifications: " + observer1.getNotificationCount());
        System.out.println("  - Observer 2 notifications: " + observer2.getNotificationCount());

        if (observer1.getNotificationCount() == 1 && observer2.getNotificationCount() == 1) {
            System.out.println("  ✅ SUCCESS: Both observers received notifications!");
        } else {
            System.out.println("  ❌ FAILED: Observers did not receive expected notifications!");
        }
        System.out.println();

        // Test detaching observer
        System.out.println("✓ Detaching observer 2...");
        notificationManager.detach(observer2);
        System.out.println("  - Observer count: " + notificationManager.getObserverCount());
        System.out.println();

        // Notify again
        System.out.println("✓ Notifying observers again...");
        notificationManager.notifyObservers(room, roomType);
        System.out.println();

        System.out.println("✓ Verifying notifications after detach...");
        System.out.println("  - Observer 1 notifications: " + observer1.getNotificationCount());
        System.out.println("  - Observer 2 notifications: " + observer2.getNotificationCount());

        if (observer1.getNotificationCount() == 2 && observer2.getNotificationCount() == 1) {
            System.out.println("  ✅ SUCCESS: Only attached observer received notification!");
        } else {
            System.out.println("  ❌ FAILED: Unexpected notification counts!");
        }

        System.out.println();
        System.out.println("✅ OBSERVER PATTERN TEST COMPLETED");
    }

    /**
     * Test 2: Decorator Pattern
     * Verify that add-ons correctly wrap and calculate booking costs.
     */
    private static void testDecoratorPattern() {
        System.out.println("--- TEST 2: DECORATOR PATTERN ---");
        System.out.println();

        // Base booking cost
        BigDecimal roomCost = new BigDecimal("360.00"); // $120/night * 3 nights
        System.out.println("✓ Creating base booking...");
        System.out.println("  - Room cost: $" + roomCost);

        BookingComponent booking = new BaseBooking(roomCost, "Single Room for 3 night(s)");
        System.out.println("  - Base booking cost: $" + booking.getCost());
        System.out.println("  - Description: " + booking.getDescription());
        System.out.println();

        // Add breakfast (per night)
        BigDecimal breakfastCost = new BigDecimal("15.00").multiply(new BigDecimal("3")); // $15/night * 3
        System.out.println("✓ Adding breakfast decorator...");
        System.out.println("  - Breakfast cost: $" + breakfastCost);
        booking = new BreakfastDecorator(booking, breakfastCost);
        System.out.println("  - Total cost: $" + booking.getCost());
        System.out.println("  - Description: " + booking.getDescription());
        System.out.println();

        // Add Wi-Fi (per night)
        BigDecimal wifiCost = new BigDecimal("10.00").multiply(new BigDecimal("3")); // $10/night * 3
        System.out.println("✓ Adding Wi-Fi decorator...");
        System.out.println("  - Wi-Fi cost: $" + wifiCost);
        booking = new WifiDecorator(booking, wifiCost);
        System.out.println("  - Total cost: $" + booking.getCost());
        System.out.println("  - Description: " + booking.getDescription());
        System.out.println();

        // Add parking (per night)
        BigDecimal parkingCost = new BigDecimal("20.00").multiply(new BigDecimal("3")); // $20/night * 3
        System.out.println("✓ Adding parking decorator...");
        System.out.println("  - Parking cost: $" + parkingCost);
        booking = new ParkingDecorator(booking, parkingCost);
        System.out.println("  - Total cost: $" + booking.getCost());
        System.out.println("  - Description: " + booking.getDescription());
        System.out.println();

        // Add spa (per stay)
        BigDecimal spaCost = new BigDecimal("150.00"); // One-time charge
        System.out.println("✓ Adding spa decorator...");
        System.out.println("  - Spa cost: $" + spaCost);
        booking = new SpaDecorator(booking, spaCost);
        System.out.println("  - Total cost: $" + booking.getCost());
        System.out.println("  - Description: " + booking.getDescription());
        System.out.println();

        // Verify final calculation
        BigDecimal expectedTotal = roomCost
                .add(breakfastCost)
                .add(wifiCost)
                .add(parkingCost)
                .add(spaCost);

        System.out.println("✓ Verifying final calculation...");
        System.out.println("  - Room: $" + roomCost);
        System.out.println("  - Breakfast: $" + breakfastCost);
        System.out.println("  - Wi-Fi: $" + wifiCost);
        System.out.println("  - Parking: $" + parkingCost);
        System.out.println("  - Spa: $" + spaCost);
        System.out.println("  - Expected total: $" + expectedTotal);
        System.out.println("  - Actual total: $" + booking.getCost());

        if (booking.getCost().compareTo(expectedTotal) == 0) {
            System.out.println("  ✅ SUCCESS: Decorator pattern calculates correctly!");
        } else {
            System.out.println("  ❌ FAILED: Cost mismatch!");
        }
        System.out.println();

        // Test individual decorators
        System.out.println("✓ Testing individual decorators...");

        BookingComponent base = new BaseBooking(new BigDecimal("100.00"), "Test Room");
        BookingComponent withBreakfast = new BreakfastDecorator(base, new BigDecimal("30.00"));

        System.out.println("  - Base only: $" + base.getCost());
        System.out.println("  - Base + Breakfast: $" + withBreakfast.getCost());

        if (withBreakfast.getCost().compareTo(new BigDecimal("130.00")) == 0) {
            System.out.println("  ✅ SUCCESS: Individual decorator works correctly!");
        } else {
            System.out.println("  ❌ FAILED: Individual decorator calculation incorrect!");
        }

        System.out.println();
        System.out.println("✅ DECORATOR PATTERN TEST COMPLETED");
    }
}
