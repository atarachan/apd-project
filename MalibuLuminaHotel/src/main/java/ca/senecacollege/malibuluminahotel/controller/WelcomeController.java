package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class WelcomeController {

    @FXML
    private ImageView hotelImage;

    @FXML
    private Button rulesButton;

    @FXML
    public void initialize() {

        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(hotelImage.fitWidthProperty());
        clip.heightProperty().bind(hotelImage.fitHeightProperty());

        clip.setArcWidth(30);
        clip.setArcHeight(30);

        hotelImage.setClip(clip);

        rulesButton.setTooltip(
                new Tooltip("View hotel policies and guest regulations.")
        );
    }

    @FXML
    private void handleStartReservation(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "GuestPreference.fxml"
        );
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "AdminDashboard.fxml"
        );
    }

    @FXML
    private void handleRules(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Hotel Rules");
        alert.setHeaderText("Malibu Lumina Hotel Rules");

        alert.setContentText(
                "• Check-in begins at 3:00 PM\n\n" +
                        "• Check-out before 11:00 AM\n\n" +
                        "• Single Room: Maximum 2 Guests\n\n" +
                        "• Double Room: Maximum 4 Guests\n\n" +
                        "• Penthouse: Maximum 2 Guests\n\n" +
                        "• Smoking is prohibited inside rooms\n\n" +
                        "• Guests are responsible for damages"
        );

        alert.showAndWait();
    }
}