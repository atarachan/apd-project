package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.AdminUser;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.UserRole;
import ca.senecacollege.malibuluminahotel.security.SessionManager;
import ca.senecacollege.malibuluminahotel.services.IDiscountService;
import ca.senecacollege.malibuluminahotel.services.IPaymentService;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AdminCheckoutController {

    // Admin cap: 15%. Would be 30% for Manager once real auth exists.
    private static final BigDecimal ADMIN_CAP_PCT = new BigDecimal("15");
    private static final BigDecimal BASE_TOTAL = new BigDecimal("1096.10");

    @FXML
    private ComboBox<String> discountTypeCombo;
    @FXML
    private TextField discountValueField;
    @FXML
    private Label totalLabel;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextField paymentAmountField;
    @FXML
    private Label balanceDueLabel;

    private final IPaymentService paymentService;
    private final IDiscountService discountService;
    private Bill currentBill; // In real implementation, would be loaded from reservation
    private BigDecimal currentTotal;

    @Inject
    public AdminCheckoutController(IPaymentService paymentService, IDiscountService discountService) {
        this.paymentService = paymentService;
        this.discountService = discountService;
        this.currentTotal = BASE_TOTAL;
    }

    @FXML
    private void initialize() {
        // Initialize payment method dropdown if it exists
        if (paymentMethodCombo != null) {
            paymentMethodCombo.setItems(FXCollections.observableArrayList(
                    "CASH", "CREDIT_CARD", "DEBIT_CARD", "APPLE_PAY", "GOOGLE_PAY"));
        }

        // Update balance due label
        updateBalanceDue();
    }

    @FXML
    private void handleApplyDiscount(ActionEvent event) {
        String type = discountTypeCombo.getValue();
        String valueText = discountValueField.getText().trim();

        if (type == null || type.equals("None") || valueText.isEmpty()) {
            currentTotal = BASE_TOTAL;
            updateTotalDisplay();
            return;
        }

        BigDecimal inputValue;
        try {
            inputValue = new BigDecimal(valueText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid input", "Please enter a valid number.");
            return;
        }

        if (inputValue.compareTo(BigDecimal.ZERO) < 0) {
            showAlert(Alert.AlertType.WARNING, "Invalid discount", "Discount value cannot be negative.");
            return;
        }

        BigDecimal discountAmount;

        if (type.equals("Percentage")) {
            // Get current admin user to check role limits
            AdminUser admin = SessionManager.getInstance().getCurrentUser();
            BigDecimal maxPct = admin != null ? discountService.getMaxDiscountPercentage(admin.getRole())
                    : ADMIN_CAP_PCT;

            if (inputValue.compareTo(maxPct) > 0) {
                String roleName = admin != null ? admin.getRole().toString() : "ADMIN";
                showAlert(Alert.AlertType.WARNING, "Discount limit exceeded",
                        roleName + " role allows a maximum of " + maxPct + "% discount.");
                return;
            }
            discountAmount = BASE_TOTAL
                    .multiply(inputValue)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            AdminUser admin = SessionManager.getInstance().getCurrentUser();
            BigDecimal maxPct = admin != null ? discountService.getMaxDiscountPercentage(admin.getRole())
                    : ADMIN_CAP_PCT;

            BigDecimal maxFixed = BASE_TOTAL
                    .multiply(maxPct)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (inputValue.compareTo(maxFixed) > 0) {
                showAlert(Alert.AlertType.WARNING, "Discount limit exceeded",
                        "Maximum fixed discount allowed: CAD " + maxFixed + ".");
                return;
            }
            discountAmount = inputValue.setScale(2, RoundingMode.HALF_UP);
        }

        currentTotal = BASE_TOTAL.subtract(discountAmount);
        updateTotalDisplay();
    }

    /**
     * Handle payment processing.
     */
    @FXML
    private void handleProcessPayment(ActionEvent event) {
        // Validate payment method
        String methodStr = paymentMethodCombo != null ? paymentMethodCombo.getValue() : null;
        if (methodStr == null) {
            showAlert(Alert.AlertType.WARNING, "No Payment Method", "Please select a payment method.");
            return;
        }

        // Validate payment amount
        String amountText = paymentAmountField != null ? paymentAmountField.getText().trim() : "";
        if (amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Amount", "Please enter a payment amount.");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Please enter a valid payment amount.");
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Payment amount must be positive.");
            return;
        }

        if (amount.compareTo(currentTotal) > 0) {
            showAlert(Alert.AlertType.WARNING, "Amount Exceeds Balance",
                    "Payment amount cannot exceed balance due (" + currentTotal + ").");
            return;
        }

        // In a real implementation with actual Bill entity:
        // try {
        // PaymentMethod method = PaymentMethod.valueOf(methodStr);
        // Payment payment = paymentService.processPayment(currentBill, method, amount);
        // currentTotal = currentTotal.subtract(amount);
        // updateTotalDisplay();
        // showAlert(Alert.AlertType.INFORMATION, "Payment Successful",
        // "Payment of $" + amount + " processed successfully.\n" +
        // "Transaction: " + payment.getTransactionReference());
        // } catch (Exception e) {
        // showAlert(Alert.AlertType.ERROR, "Payment Failed", e.getMessage());
        // }

        // Mock implementation for demonstration
        currentTotal = currentTotal.subtract(amount);
        updateTotalDisplay();
        showAlert(Alert.AlertType.INFORMATION, "Payment Successful",
                "Payment of CAD " + amount + " processed successfully via " + methodStr + ".");
    }

    /**
     * Update total display label.
     */
    private void updateTotalDisplay() {
        if (totalLabel != null) {
            totalLabel.setText(String.format("%.2f", currentTotal));
        }
        updateBalanceDue();
    }

    /**
     * Update balance due label.
     */
    private void updateBalanceDue() {
        if (balanceDueLabel != null) {
            balanceDueLabel.setText(String.format("CAD %.2f", currentTotal));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }
}
