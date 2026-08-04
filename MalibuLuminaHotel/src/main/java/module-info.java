module ca.senecacollege.malibuluminahotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.logging;
    requires org.slf4j;
    requires java.sql;
    requires jbcrypt;

    opens ca.senecacollege.malibuluminahotel.app to javafx.fxml;
    opens ca.senecacollege.malibuluminahotel.controller to javafx.fxml;

    opens ca.senecacollege.malibuluminahotel.models to org.hibernate.orm.core, javafx.fxml;
    opens ca.senecacollege.malibuluminahotel.models.enums to org.hibernate.orm.core;

    exports ca.senecacollege.malibuluminahotel.models;
    exports ca.senecacollege.malibuluminahotel.app;
    exports ca.senecacollege.malibuluminahotel.tests;
    exports ca.senecacollege.malibuluminahotel.events;
    exports ca.senecacollege.malibuluminahotel.decorators;
    exports ca.senecacollege.malibuluminahotel.security;

    opens ca.senecacollege.malibuluminahotel.tests to javafx.fxml, org.hibernate.orm.core;
}
