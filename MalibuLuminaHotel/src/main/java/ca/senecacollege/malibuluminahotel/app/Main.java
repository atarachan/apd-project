package ca.senecacollege.malibuluminahotel.app;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.tests.DatabaseTest;
import ca.senecacollege.malibuluminahotel.tests.RepositoryTest;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import ca.senecacollege.malibuluminahotel.repositories.IRoomTypeRepository;
import ca.senecacollege.malibuluminahotel.repositories.RoomTypeRepositoryImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // INITIALIZES DATABASE
        EntityManagerFactoryProvider.getEntityManagerFactory();

        // TEST TO VERIFY DATABASE WORKS
        // DatabaseTest.testAddOnRoundTrip();

        RepositoryTest.testBillRepository();


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
