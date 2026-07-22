module ca.senecacollege.malibuluminahotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.persistence;

    opens ca.senecacollege.malibuluminahotel.app to javafx.fxml;
    opens ca.senecacollege.malibuluminahotel.controller to javafx.fxml;

    exports ca.senecacollege.malibuluminahotel.app;
}