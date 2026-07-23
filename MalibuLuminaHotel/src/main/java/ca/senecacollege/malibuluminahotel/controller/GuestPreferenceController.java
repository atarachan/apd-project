package ca.senecacollege.malibuluminahotel.controller;

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

        if (checkInDatePicker.getValue() != null &&
                checkOutDatePicker.getValue() != null &&
                checkOutDatePicker.getValue().isBefore(
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

        SceneNavigator.switchScene(
                event,
                "RoomSelection.fxml"
        );
    }
}