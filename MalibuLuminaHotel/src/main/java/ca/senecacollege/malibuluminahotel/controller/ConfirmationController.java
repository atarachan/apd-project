package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfirmationController {

    @FXML
    private Label reservationNumberLabel;

    @FXML
    public void initialize() {

        Long reservationId =
                BookingSession.getInstance()
                        .getSavedReservationId();

        reservationNumberLabel.setText(
                "Reservation Number: R" + reservationId
        );
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {

        BookingSession.getInstance().reset();

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }
}