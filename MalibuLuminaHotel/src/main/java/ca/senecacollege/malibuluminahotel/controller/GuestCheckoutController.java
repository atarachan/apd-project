package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.services.BillLineItem;
import ca.senecacollege.malibuluminahotel.services.BillSummary;
import ca.senecacollege.malibuluminahotel.services.IBookingService;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class GuestCheckoutController {

    @FXML private VBox billLinesBox;
    @FXML private Label roomChargesLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private final IBookingService bookingService;
    private BillSummary billSummary;
    private NumberFormat currencyFormat;

    @Inject
    public GuestCheckoutController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @FXML
    public void initialize() {
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA);

        try {
            billSummary = bookingService.calculateBill(BookingSession.getInstance());

            populateBillLines();
            roomChargesLabel.setText(formatCurrency(billSummary.roomTotal()));
            addOnsLabel.setText(formatCurrency(billSummary.addOnTotal()));
            subtotalLabel.setText(formatCurrency(billSummary.subtotal()));
            taxLabel.setText(formatCurrency(billSummary.tax()));
            totalLabel.setText(formatCurrency(billSummary.total()));

        } catch (Exception e) {
            roomChargesLabel.setText("--");
            addOnsLabel.setText("--");
            subtotalLabel.setText("--");
            taxLabel.setText("--");
            totalLabel.setText("--");
            showError("Billing Error", "Could not calculate bill: " + e.getMessage());
        }
    }

    private void populateBillLines() {
        billLinesBox.getChildren().clear();

        for (BillLineItem lineItem : billSummary.lineItems()) {
            HBox row = new HBox(12);
            row.setPadding(new Insets(4, 0, 4, 0));

            VBox descriptionBox = new VBox(2);
            descriptionBox.setPrefWidth(430);

            Label description = new Label(lineItem.description());
            description.setWrapText(true);
            description.getStyleClass().add("field-label");

            Label calculation = new Label(lineItem.calculation());
            calculation.setWrapText(true);
            calculation.getStyleClass().add("helper-label");

            Label amount = new Label(formatCurrency(lineItem.amount()));
            amount.setPrefWidth(140);
            amount.getStyleClass().add("field-label");

            descriptionBox.getChildren().addAll(description, calculation);
            row.getChildren().addAll(descriptionBox, amount);
            billLinesBox.getChildren().add(row);
        }
    }

    private String formatCurrency(BigDecimal amount) {
        return currencyFormat.format(amount);
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
