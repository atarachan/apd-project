package ca.senecacollege.malibuluminahotel.app;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.services.DataSeeder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        EntityManagerFactoryProvider.getEntityManagerFactory();

        DataSeeder.seed();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/fxml/Welcome.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Malibu Lumina Hotel");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        EntityManagerFactoryProvider.closeEntityManagerFactory();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
