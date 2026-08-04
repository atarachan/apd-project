package ca.senecacollege.malibuluminahotel.util;

import ca.senecacollege.malibuluminahotel.models.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class ValidationUtil {

    // Email regex pattern: basic validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Phone pattern: supports various formats (10 digits with optional formatting)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+?1[-\\s]?)?\\(?([0-9]{3})\\)?[-\\s]?([0-9]{3})[-\\s]?([0-9]{4})$");

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone number format.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates that date range is valid (start before end, not in past).
     */
    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return false;
        }
        // Start must be before end
        if (!start.isBefore(end)) {
            return false;
        }
        // Start must be today or in future
        LocalDate today = LocalDate.now();
        return !start.isBefore(today);
    }

    /**
     * Validates that check-in date is not in the past.
     */
    public static boolean isValidCheckInDate(LocalDate checkIn) {
        if (checkIn == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !checkIn.isBefore(today);
    }

    /**
     * Validates occupancy against room capacity.
     */
    public static boolean isValidOccupancy(List<Room> rooms, int adults, int children) {
        if (rooms == null || rooms.isEmpty()) {
            return false;
        }

        int totalCapacity = rooms.stream()
                .mapToInt(room -> room.getRoomType().getMaxOccupancy())
                .sum();

        int totalGuests = adults + children;

        return totalGuests > 0 && totalGuests <= totalCapacity;
    }

    /**
     * Validates that a string is not null or empty.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates that a numeric value is positive.
     */
    public static boolean isPositive(Number value) {
        return value != null && value.doubleValue() > 0;
    }

    /**
     * Validates that a numeric value is non-negative.
     */
    public static boolean isNonNegative(Number value) {
        return value != null && value.doubleValue() >= 0;
    }

    /**
     * Validates discount percentage (0-100).
     */
    public static boolean isValidDiscountPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }

    /**
     * Validates rating (1-5).
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
