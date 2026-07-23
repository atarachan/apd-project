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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
