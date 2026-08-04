package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Component interface for the Decorator pattern.
 * Represents any booking component that can calculate cost and provide a
 * description.
 * This can be a base booking or a booking decorated with add-on services.
 */
public interface BookingComponent {

    /**
     * Calculate the total cost of this booking component.
     * 
     * @return the total cost including all decorated services
     */
    BigDecimal getCost();

    /**
     * Get a description of this booking component.
     * 
     * @return a description including all services
     */
    String getDescription();
}
