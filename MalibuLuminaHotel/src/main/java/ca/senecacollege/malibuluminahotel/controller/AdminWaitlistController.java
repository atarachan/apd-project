package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminWaitlistController {

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminDashboard.fxml");
    }
}
