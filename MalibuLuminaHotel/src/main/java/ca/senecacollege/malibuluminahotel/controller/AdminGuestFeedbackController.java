package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class AdminGuestFeedbackController {

    @FXML
    private TextArea responseField1;

    @FXML
    private TextArea responseField2;

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    private void handleSubmitResponse1(ActionEvent event) {

        if (responseField1.getText().trim().isEmpty()) {

            showMessage(
                    "Guest Feedback",
                    "Please enter a response."
            );

        } else {

            showMessage(
                    "Guest Feedback",
                    "Response submitted successfully."
            );
        }
    }

    @FXML
    private void handleSubmitResponse2(ActionEvent event) {

        if (responseField2.getText().trim().isEmpty()) {

            showMessage(
                    "Guest Feedback",
                    "Please enter a response."
            );

        } else {

            showMessage(
                    "Guest Feedback",
                    "Response submitted successfully."
            );
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "AdminDashboard.fxml"
        );
    }
}