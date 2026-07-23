package ca.senecacollege.malibuluminahotel.controller;

import ca.senecacollege.malibuluminahotel.app.BookingSession;
import ca.senecacollege.malibuluminahotel.app.SceneNavigator;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class RoomSelectionController {

    @FXML private ImageView singleRoomImage;
    @FXML private ImageView doubleRoomImage;
    @FXML private ImageView penthouseImage;

    @FXML
    public void initialize() {
        roundImage(singleRoomImage);
        roundImage(doubleRoomImage);
        roundImage(penthouseImage);
    }

    private void roundImage(ImageView imageView) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imageView.fitWidthProperty());
        clip.heightProperty().bind(imageView.fitHeightProperty());
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imageView.setClip(clip);
    }

    @FXML
    private void handleSelectSingle(ActionEvent event) {
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.SINGLE);
        SceneNavigator.switchScene(event, "ReservationDetails.fxml");
    }

    @FXML
    private void handleSelectDouble(ActionEvent event) {
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.DOUBLE);
        SceneNavigator.switchScene(event, "ReservationDetails.fxml");
    }

    @FXML
    private void handleSelectPenthouse(ActionEvent event) {
        BookingSession.getInstance().setSelectedRoomTypeName(RoomTypeName.PENTHOUSE);
        SceneNavigator.switchScene(event, "ReservationDetails.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneNavigator.switchScene(event, "GuestPreference.fxml");
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        if (BookingSession.getInstance().getSelectedRoomTypeName() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Room Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a room type before continuing.");
            alert.showAndWait();
            return;
        }
        SceneNavigator.switchScene(event, "ReservationDetails.fxml");
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
