package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ReservationDetailsController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    @FXML private Label firstNameError;
    @FXML private Label lastNameError;
    @FXML private Label emailError;
    @FXML private Label phoneError;

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "RoomSelection.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();

        boolean valid = true;

        if (firstName.isEmpty()) {
            valid = setError(firstNameField, firstNameError, "First name is required.");
        } else {
            clearError(firstNameField, firstNameError);
        }

        if (lastName.isEmpty()) {
            valid = setError(lastNameField, lastNameError, "Last name is required.");
        } else {
            clearError(lastNameField, lastNameError);
        }

        if (email.isEmpty()) {
            valid = setError(emailField, emailError, "Email address is required.");
        } else if (!email.contains("@") || !email.contains(".")) {
            valid = setError(emailField, emailError, "Enter a valid email address.");
        } else {
            clearError(emailField, emailError);
        }

        if (phone.isEmpty()) {
            valid = setError(phoneField, phoneError, "Phone number is required.");
        } else if (!phone.matches("[0-9\\-\\+\\s\\(\\)]{7,15}")) {
            valid = setError(phoneField, phoneError, "Enter a valid phone number (digits only).");
        } else {
            clearError(phoneField, phoneError);
        }

        if (!valid) return;

        BookingSession session = BookingSession.getInstance();
        session.setGuestFirstName(firstName);
        session.setGuestLastName(lastName);
        session.setGuestEmail(email);
        session.setGuestPhone(phone);

        SceneNavigator.switchScene(event, "AddOns.fxml");
    }

    private boolean setError(TextField field, Label errorLabel, String message) {
        errorLabel.setText(message);
        field.setStyle("-fx-border-color: #c0392b; -fx-border-width: 1.5px; -fx-border-radius: 4px;");
        return false;
    }

    private void clearError(TextField field, Label errorLabel) {
        errorLabel.setText("");
        field.setStyle("");
    }

    @FXML
    private void handleRules(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hotel Rules");
        alert.setHeaderText("Malibu Lumina Hotel Rules");
        alert.setContentText(
                "• Check-in begins at 3:00 PM\n\n" +
                "• Check-out before 11:00 AM\n\n" +
                "• Single Room: Maximum 2 Guests\n\n" +
                "• Double Room: Maximum 4 Guests\n\n" +
                "• Penthouse: Maximum 2 Guests\n\n" +
                "• Smoking is prohibited inside rooms\n\n" +
                "• Guests are responsible for damages"
        );
        alert.showAndWait();
    }
}
