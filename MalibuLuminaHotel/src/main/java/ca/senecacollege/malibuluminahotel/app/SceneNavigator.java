package ca.senecacollege.malibuluminahotel.app;

import ca.senecacollege.malibuluminahotel.config.InjectorHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SceneNavigator {

    public static void switchScene(ActionEvent event, String fxmlFile) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    SceneNavigator.class.getResource("/view/fxml/" + fxmlFile)
            );
            loader.setControllerFactory(InjectorHolder.getInjector()::getInstance);

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
