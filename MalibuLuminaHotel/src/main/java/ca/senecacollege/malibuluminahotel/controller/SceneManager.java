package ca.senecacollege.malibuluminahotel.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void switchScene(Node source, String fxmlFile) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/view/fxml/" + fxmlFile)
            );

            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}