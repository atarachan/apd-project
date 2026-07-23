package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AdminBillingController {

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

        String text = searchField.getText().trim();

        if (text.isEmpty()) {

            showMessage(
                    "Search",
                    "Please enter a bill ID or guest name."
            );

        } else {

            showMessage(
                    "Search",
                    "Searching billing records for: " + text
            );
        }
    }

    @FXML
    private void handleUpdateStatus(ActionEvent event) {

        showMessage(
                "Billing",
                "Payment status updated."
        );
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {

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