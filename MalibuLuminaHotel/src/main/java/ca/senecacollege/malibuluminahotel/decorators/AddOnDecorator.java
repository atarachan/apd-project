package ca.senecacollege.malibuluminahotel.decorators;

import java.math.BigDecimal;

/**
 * Abstract Decorator for the Decorator pattern.
 * Base class for all add-on decorators. Wraps a BookingComponent and adds
 * additional cost and description for an add-on service.
 */
public abstract class AddOnDecorator implements BookingComponent {

    protected final BookingComponent wrappedBooking;

    /**
     * Create a decorator that wraps another booking component.
     * 
     * @param booking the booking component to wrap
     */
    public AddOnDecorator(BookingComponent booking) {
        this.wrappedBooking = booking;
    }

    /**
     * Get the cost of the add-on service.
     * Must be implemented by concrete decorators.
     * 
     * @return the cost of this specific add-on
     */
    protected abstract BigDecimal getAddOnCost();

    /**
     * Get the name/description of the add-on service.
     * Must be implemented by concrete decorators.
     * 
     * @return the description of this specific add-on
     */
    protected abstract String getAddOnDescription();

    @Override
    public BigDecimal getCost() {
        return wrappedBooking.getCost().add(getAddOnCost());
    }

    @Override
    public String getDescription() {
        return wrappedBooking.getDescription() + ", " + getAddOnDescription();
    }
}
