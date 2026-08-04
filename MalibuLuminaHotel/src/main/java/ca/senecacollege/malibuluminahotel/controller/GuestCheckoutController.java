package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.services.BookingService;
import ca.senecacollege.malibuluminahotel.services.LoyaltyService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class GuestCheckoutController {

    @FXML private Label roomChargesLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private BookingService bookingService;
    private LoyaltyService loyaltyService;
    private BookingService.BillSummary billSummary;

    @FXML
    public void initialize() {
        bookingService = new BookingService();
        loyaltyService = new LoyaltyService();

        try {
            billSummary = bookingService.calculateBill(BookingSession.getInstance());

            NumberFormat fmt = NumberFormat.getNumberInstance(Locale.CANADA);
            fmt.setMinimumFractionDigits(2);
            fmt.setMaximumFractionDigits(2);

            roomChargesLabel.setText("CAD " + fmt.format(billSummary.roomTotal()));
            addOnsLabel.setText("CAD " + fmt.format(billSummary.addOnTotal()));
            subtotalLabel.setText("CAD " + fmt.format(billSummary.subtotal()));
            taxLabel.setText("CAD " + fmt.format(billSummary.tax()));
            totalLabel.setText("CAD " + fmt.format(billSummary.total()));

        } catch (Exception e) {
            roomChargesLabel.setText("CAD --");
            addOnsLabel.setText("CAD --");
            subtotalLabel.setText("CAD --");
            taxLabel.setText("CAD --");
            totalLabel.setText("CAD --");
            showError("Billing Error", "Could not calculate bill: " + e.getMessage());
        }
    }

    @FXML
    private void handleProceedToPayment(ActionEvent event) {
        if (billSummary == null) {
            showError("Error", "Bill summary is unavailable. Please go back and try again.");
            return;
        }

        try {
            Reservation reservation = bookingService.createReservation(
                    BookingSession.getInstance(), billSummary);

            BookingSession.getInstance().setSavedReservationId(reservation.getReservationId());

            // Offer loyalty enrollment
            offerLoyaltyEnrollment(reservation.getGuest());

            SceneNavigator.switchScene(event, "Confirmation.fxml");

        } catch (IllegalStateException e) {
            showError("Booking Failed", e.getMessage());
        } catch (Exception e) {
            showError("Booking Failed", "An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        BookingSession.getInstance().reset();
        SceneNavigator.switchScene(event, "Welcome.fxml");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Offers loyalty program enrollment to the guest.
     */
    private void offerLoyaltyEnrollment(Guest guest) {
        try {
            // Check if already enrolled
            if (loyaltyService.isEnrolled(guest)) {
                return; // Already a member
            }

            // Show enrollment offer
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Join Loyalty Program");
            alert.setHeaderText("Earn Points on Your Stay!");
            alert.setContentText("Would you like to join our loyalty program and start earning points? \n\n" +
                    "Benefits:\n" +
                    "• Earn 10 points per dollar spent\n" +
                    "• Redeem points for discounts\n" +
                    "• Exclusive member offers\n\n" +
                    "Join now?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                LoyaltyAccount account = loyaltyService.enrollGuest(guest);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Welcome to Loyalty Program!");
                success.setHeaderText("Enrollment Successful");
                success.setContentText("Your member number is: " + account.getMemberNumber() + "\n" +
                        "Current points: " + account.getCurrentPoints());
                success.showAndWait();
            }
        } catch (Exception e) {
            // Log error but don't interrupt checkout flow
            System.err.println("Failed to offer loyalty enrollment: " + e.getMessage());
        }
    }
}
