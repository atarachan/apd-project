package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class GuestCheckoutController {

    @FXML
    private void handleProceedToPayment(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Confirmation.fxml"
        );
    }

    @FXML
    private void handleCancel(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }
}