package ca.senecacollege.malibuluminahotel.models.enums;

/**
 * Feedback categories for guest reviews.
 */
public enum FeedbackCategory {
    ROOM_QUALITY("Room Quality"),
    CLEANLINESS("Cleanliness"),
    STAFF_SERVICE("Staff Service"),
    AMENITIES("Amenities"),
    CHECK_IN_OUT("Check-in/Check-out"),
    VALUE_FOR_MONEY("Value for Money"),
    LOCATION("Location"),
    FOOD_BEVERAGE("Food & Beverage"),
    GENERAL("General");

    private final String label;

    FeedbackCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
