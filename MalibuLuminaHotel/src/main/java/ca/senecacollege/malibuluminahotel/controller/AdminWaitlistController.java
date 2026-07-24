package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AdminWaitlistController {

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleAddToWaitlist(ActionEvent event) {

        showMessage(
                "Waitlist",
                "Guest added to waitlist."
        );
    }

    @FXML
    private void handleConvertReservation(ActionEvent event) {

        showMessage(
                "Reservation",
                "Waitlist entry converted to reservation."
        );
    }

    @FXML
    private void handleBack(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "AdminDashboard.fxml"
        );
    }

    @FXML
    private void handleLogout(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "Welcome.fxml"
        );
    }
}