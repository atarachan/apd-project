package ca.senecacollege.malibuluminahotel.models.enums;

/**
 * Types of activities that can be logged in the system.
 */
public enum ActivityType {
    LOGIN("Login"),
    LOGOUT("Logout"),
    RESERVATION_CREATED("Reservation Created"),
    RESERVATION_MODIFIED("Reservation Modified"),
    RESERVATION_CANCELLED("Reservation Cancelled"),
    CHECKOUT_COMPLETED("Checkout Completed"),
    PAYMENT_PROCESSED("Payment Processed"),
    REFUND_PROCESSED("Refund Processed"),
    GUEST_CREATED("Guest Created"),
    GUEST_UPDATED("Guest Updated"),
    BILL_GENERATED("Bill Generated"),
    REPORT_GENERATED("Report Generated"),
    WAITLIST_ENTRY_CREATED("Waitlist Entry Created"),
    WAITLIST_ENTRY_CONVERTED("Waitlist Entry Converted"),
    FEEDBACK_RESPONDED("Feedback Responded"),
    SYSTEM_CONFIG_CHANGED("System Configuration Changed");

    private final String label;

    ActivityType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
