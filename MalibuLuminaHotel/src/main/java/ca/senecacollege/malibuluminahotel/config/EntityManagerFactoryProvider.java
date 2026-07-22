package ca.senecacollege.malibuluminahotel.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class EntityManagerFactoryProvider {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    private static final String PERSISTENCE_UNIT_NAME = "hotelPU";
    private static final String PROPERTIES_FILE = "application.properties";

    private static EntityManagerFactory entityManagerFactory;

    private EntityManagerFactoryProvider() {}

    // 'synchronized' keyword prevents two instances of the manager being created.
    public static synchronized EntityManagerFactory getEntityManagerFactory() {

        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            return entityManagerFactory;
        }

        try {
            Map<String, Object> settings = loadSettings();

            logger.info("Creating EntityManagerFactory for presistence unit: {}", PERSISTENCE_UNIT_NAME);

            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, settings);

            logger.info("Created EntityManagerFactory successfully");

            return entityManagerFactory;
        }
        catch (RuntimeException e){
            logger.error("Failed to create EntityManagerFactory", e);

            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    private static Map<String, Object> loadSettings() {

        Properties prop = new Properties();

        try (InputStream is = EntityManagerFactoryProvider.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                prop.load(is);

                logger.info("Loaded persistance settings from file: {}", PROPERTIES_FILE);
            }
            else{
                logger.warn("{} was not found. Default settings will be used.", PROPERTIES_FILE);
            }
        }
        catch (IOException e){
            logger.warn("{} was not found. Default settings will be used.", PROPERTIES_FILE);
        }

        Map<String, Object> settings = new HashMap<>();

        settings.put("javax.persistence.jdbc.driver", prop.getProperty("db.driver", "org.sqlite.JDBC"));
        settings.put("javax.persistence.jdbc.url", prop.getProperty("db.url", "jdbc:sqlite:hotel.db"));

        settings.put("hibernate.dialect", prop.getProperty("hibernate.dialect", "org.sqlite.hibernate.dialect.SQLiteDialect"));
        settings.put("hibernate.hbm2ddl.auto", prop.getProperty("hibernate.hbm2ddl.auto", "update"));
        settings.put("hibernate.show_sql", prop.getProperty("hibernate.show_sql", "true"));
        settings.put("hibernate.format_sql", prop.getProperty("hibernate.format_sql", "true"));
        settings.put("hibernate.use_sql_comments", prop.getProperty("hibernate.use_sql_comments", "true"));
        settings.put("hibernate.connection.pool_size", prop.getProperty("hibernate.connection.pool_size", "5"));
        settings.put("hibernate.temp.use_jdbc_metadata_defaults", prop.getProperty("hibernate.temp.use_jdbc_metadata_defaults", "false"));


        String loggingProvider = prop.getProperty("org.jboss.logging.provider", "slf4j");
        System.setProperty("org.jboss.logging.provider", loggingProvider);

        return settings;
    }

    public static synchronized void closeEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            return;
        }

        try {
            entityManagerFactory.close();

            logger.info("Closed EntityManagerFactory successfully");
        }
        catch (RuntimeException e) {
            logger.error("Failed to close EntityManagerFactory", e);
        }
        finally {
            entityManagerFactory = null;
        }

    }

}
