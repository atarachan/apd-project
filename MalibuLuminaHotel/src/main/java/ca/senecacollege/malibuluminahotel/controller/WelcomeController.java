package ca.senecacollege.malibuluminahotel.controller;

import javafx.fxml.FXML;
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
}