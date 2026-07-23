package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ConfirmationController {

    @FXML
    private void handleReturnHome(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }
}