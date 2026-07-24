package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AdminReportsController {

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleGenerateReport(ActionEvent event) {

        showMessage(
                "Report Generated",
                "The selected report has been generated successfully."
        );
    }

    @FXML
    private void handleBack(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "AdminDashboard.fxml"
        );
    }
}