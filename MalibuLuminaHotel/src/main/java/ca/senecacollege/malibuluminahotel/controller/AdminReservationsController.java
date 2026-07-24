package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AdminReservationsController {

    @FXML
    private TextField searchField;

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleSearch(ActionEvent event) {

        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {

            showMessage(
                    "Search",
                    "Please enter a reservation ID or guest name."
            );

        } else {

            showMessage(
                    "Search",
                    "Searching for: " + searchText
            );
        }
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