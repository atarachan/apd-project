package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.services.BookingService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;

public class GuestCheckoutController {

    @FXML private Label roomChargesLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private BookingService bookingService;
    private BookingService.BillSummary billSummary;

    @FXML
    public void initialize() {
        bookingService = new BookingService();

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
}
