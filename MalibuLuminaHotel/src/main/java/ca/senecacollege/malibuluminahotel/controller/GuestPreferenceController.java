package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class GuestPreferenceController {

    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private ComboBox<String> adultsComboBox;
    @FXML private ComboBox<String> childrenComboBox;

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "Welcome.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        LocalDate checkIn  = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            showError("Missing Dates", "Please select both check-in and check-out dates.");
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            showError("Invalid Dates", "Check-out date must be after check-in date.");
            return;
        }

        int adults   = parseCount(adultsComboBox.getValue(), 1);
        int children = parseCount(childrenComboBox.getValue(), 0);

        BookingSession session = BookingSession.getInstance();
        session.setCheckInDate(checkIn);
        session.setCheckOutDate(checkOut);
        session.setAdults(adults);
        session.setChildren(children);

        SceneNavigator.switchScene(event, "RoomSelection.fxml");
    }

    private int parseCount(String value, int defaultVal) {
        if (value == null || value.isEmpty() || value.endsWith("+")) return defaultVal;
        try { return Integer.parseInt(value.trim()); } catch (NumberFormatException e) { return defaultVal; }
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
