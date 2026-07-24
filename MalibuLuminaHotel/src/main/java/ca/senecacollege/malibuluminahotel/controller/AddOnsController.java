package ca.senecacollege.malibuluminahotel.controller;
import ca.senecacollege.malibuluminahotel.app.BookingSession;
import javafx.scene.control.CheckBox;

import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AddOnsController {

    @FXML
    private CheckBox breakfastCheckBox;

    @FXML
    private CheckBox wifiCheckBox;

    @FXML
    private CheckBox parkingCheckBox;

    @FXML
    private CheckBox spaCheckBox;

    @FXML
    private void handleBack(ActionEvent event) {

        SceneNavigator.switchScene(
                event,
                "ReservationDetails.fxml"
        );
    }

    @FXML
    private void handleContinue(ActionEvent event) {

        BookingSession session =
                BookingSession.getInstance();

        session.setBreakfastSelected(
                breakfastCheckBox.isSelected()
        );

        session.setWifiSelected(
                wifiCheckBox.isSelected()
        );

        session.setParkingSelected(
                parkingCheckBox.isSelected()
        );

        session.setSpaSelected(
                spaCheckBox.isSelected()
        );

        SceneNavigator.switchScene(
                event,
                "guestCheckout.fxml"
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