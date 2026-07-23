package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class AdminDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private void handleReservations(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminReservations.fxml");
    }

    @FXML
    private void handleBilling(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminBilling.fxml");
    }

    @FXML
    private void handleGuestFeedback(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminFeedback.fxml");
    }

    @FXML
    private void handleReports(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminReports.fxml");
    }

    @FXML
    private void handleLoyaltyProgram(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminLoyalty.fxml");
    }

    @FXML
    private void handleWaitlist(ActionEvent event) {
        SceneNavigator.switchScene(event, "AdminCheckout.fxml");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a guest name or reservation ID.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search");
            alert.setHeaderText(null);
            alert.setContentText("Searching for: " + searchText);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SceneNavigator.switchScene(event, "Welcome.fxml");
    }
}
