package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AdminLoyaltyController {

    @FXML
    private TextField memberField;

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleSearchMember(ActionEvent event) {

        String text = memberField.getText().trim();

        if (text.isEmpty()) {

            showMessage(
                    "Loyalty Program",
                    "Please enter a member name or ID."
            );

        } else {

            showMessage(
                    "Loyalty Program",
                    "Searching for member: " + text
            );
        }
    }

    @FXML
    private void handleUpdatePoints(ActionEvent event) {

        showMessage(
                "Loyalty Program",
                "Points updated successfully."
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