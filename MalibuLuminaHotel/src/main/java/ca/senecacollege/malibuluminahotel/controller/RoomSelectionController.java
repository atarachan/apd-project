package ca.senecacollege.malibuluminahotel.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class RoomSelectionController {

    @FXML
    private ImageView singleRoomImage;

    @FXML
    private ImageView doubleRoomImage;

    @FXML
    private ImageView penthouseImage;

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
    private void handleSelectRoom(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("guest-details.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}