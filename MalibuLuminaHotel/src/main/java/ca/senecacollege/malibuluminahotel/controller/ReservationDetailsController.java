package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ReservationDetailsController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

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

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please complete all required fields.");
            alert.showAndWait();
            return;
        }

        BookingSession session = BookingSession.getInstance();
        session.setGuestFirstName(firstName);
        session.setGuestLastName(lastName);
        session.setGuestEmail(email);
        session.setGuestPhone(phone);

        SceneNavigator.switchScene(event, "AddOns.fxml");
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
