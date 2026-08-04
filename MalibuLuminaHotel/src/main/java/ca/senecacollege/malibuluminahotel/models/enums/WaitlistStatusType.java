package ca.senecacollege.malibuluminahotel.models.enums;

/**
 * Enum representing waitlist entry status types.
 * Matches ERD specification.
 */
public enum WaitlistStatusType {
    IN_QUEUE("In Queue"),
    SPOT_AVAILABLE("Spot Available"),
    WITHDRAWN("Withdrawn");

    private final String label;

    WaitlistStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
