package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AdminCheckoutController {

    // Admin cap: 15%. Would be 30% for Manager once real auth exists.
    private static final BigDecimal ADMIN_CAP_PCT = new BigDecimal("15");
    private static final BigDecimal BASE_TOTAL = new BigDecimal("1096.10");

    @FXML private ComboBox<String> discountTypeCombo;
    @FXML private TextField discountValueField;
    @FXML private Label totalLabel;

    @FXML
    private void handleApplyDiscount(ActionEvent event) {
        String type = discountTypeCombo.getValue();
        String valueText = discountValueField.getText().trim();

        if (type == null || type.equals("None") || valueText.isEmpty()) {
            totalLabel.setText(String.format("%.2f", BASE_TOTAL));
            return;
        }

        BigDecimal inputValue;
        try {
            inputValue = new BigDecimal(valueText);
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Please enter a valid number.");
            return;
        }

        if (inputValue.compareTo(BigDecimal.ZERO) < 0) {
            showAlert("Invalid discount", "Discount value cannot be negative.");
            return;
        }

        BigDecimal discountAmount;

        if (type.equals("Percentage")) {
            if (inputValue.compareTo(ADMIN_CAP_PCT) > 0) {
                showAlert("Discount limit exceeded",
                        "Admin role allows a maximum of 15% discount.");
                return;
            }
            discountAmount = BASE_TOTAL
                    .multiply(inputValue)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal maxFixed = BASE_TOTAL
                    .multiply(ADMIN_CAP_PCT)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            if (inputValue.compareTo(maxFixed) > 0) {
                showAlert("Discount limit exceeded",
                        "Admin role allows a maximum fixed discount of CAD " + maxFixed + ".");
                return;
            }
            discountAmount = inputValue.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal newTotal = BASE_TOTAL.subtract(discountAmount);
        totalLabel.setText(String.format("%.2f", newTotal));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
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
