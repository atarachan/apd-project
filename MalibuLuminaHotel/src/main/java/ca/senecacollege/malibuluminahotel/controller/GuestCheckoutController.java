package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;
import ca.senecacollege.malibuluminahotel.services.BillLineItem;
import ca.senecacollege.malibuluminahotel.services.BillSummary;
import ca.senecacollege.malibuluminahotel.services.IBookingService;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class GuestCheckoutController {

    @FXML private VBox billLinesBox;
    @FXML private Label roomChargesLabel;
    @FXML private Label addOnsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private CheckBox depositCheckBox;
    @FXML private Label depositAmountLabel;
    @FXML private TextField cardholderNameField;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryDateField;
    @FXML private PasswordField cvvField;

    private final IBookingService bookingService;
    private BillSummary billSummary;
    private NumberFormat currencyFormat;
    private BigDecimal depositAmount = BigDecimal.ZERO;

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
            depositAmount = calculateDepositAmount();
            depositAmountLabel.setText(formatCurrency(depositAmount));
            setDepositFieldsDisabled(true);
            depositCheckBox.selectedProperty().addListener((obs, oldValue, selected) ->
                    setDepositFieldsDisabled(!selected));

        } catch (Exception e) {
            roomChargesLabel.setText("--");
            addOnsLabel.setText("--");
            subtotalLabel.setText("--");
            taxLabel.setText("--");
            totalLabel.setText("--");
            depositAmountLabel.setText("--");
            setDepositFieldsDisabled(true);
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

    private BigDecimal calculateDepositAmount() {
        return billSummary.total()
                .multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void setDepositFieldsDisabled(boolean disabled) {
        cardholderNameField.setDisable(disabled);
        cardNumberField.setDisable(disabled);
        expiryDateField.setDisable(disabled);
        cvvField.setDisable(disabled);
    }

    private Payment buildDepositPaymentIfProvided() {
        boolean selected = depositCheckBox.isSelected();

        if (!selected) {
            return null;
        }

        if (!hasText(cardholderNameField)
                || !hasText(cardNumberField)
                || !hasText(expiryDateField)
                || !hasText(cvvField)) {
            throw new IllegalArgumentException("Enter all deposit payment details or leave the deposit section blank.");
        }

        Payment deposit = new Payment();
        deposit.setAmount(depositAmount);
        deposit.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        deposit.setPaymentStatus(PaymentStatus.COMPLETED);
        deposit.setTransactionReference("DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return deposit;
    }

    private boolean hasText(TextField field) {
        return field.getText() != null && !field.getText().trim().isEmpty();
    }

    @FXML
    private void handleProceedToPayment(ActionEvent event) {
        if (billSummary == null) {
            showError("Error", "Bill summary is unavailable. Please go back and try again.");
            return;
        }

        try {
            Payment depositPayment = buildDepositPaymentIfProvided();
            Reservation reservation = bookingService.createReservation(
                    BookingSession.getInstance(), billSummary, depositPayment);

            BookingSession.getInstance().setSavedReservationId(reservation.getReservationId());
            SceneNavigator.switchScene(event, "Confirmation.fxml");

        } catch (IllegalArgumentException e) {
            showError("Deposit Payment", e.getMessage());
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
