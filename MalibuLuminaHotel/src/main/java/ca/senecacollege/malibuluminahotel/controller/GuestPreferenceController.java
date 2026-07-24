package ca.senecacollege.malibuluminahotel.controller;
import ca.senecacollege.malibuluminahotel.app.BookingSession;


import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;

public class GuestPreferenceController {

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private void handleBack(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }

    @FXML
    private void handleContinue(ActionEvent event) {

        if (checkInDatePicker.getValue() == null
                || checkOutDatePicker.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Missing Dates");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Please select both check-in and check-out dates."
            );

            alert.showAndWait();
            return;
        }

        if (checkOutDatePicker.getValue().isBefore(
                checkInDatePicker.getValue())) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Invalid Dates");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Check-out date must be after check-in date."
            );

            alert.showAndWait();
            return;
        }

        BookingSession session = BookingSession.getInstance();

        session.setCheckInDate(
                checkInDatePicker.getValue()
        );

        session.setCheckOutDate(
                checkOutDatePicker.getValue()
        );


        SceneNavigator.switchScene(
                event,
                "RoomSelection.fxml"
        );
    }
}