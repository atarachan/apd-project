package ca.senecacollege.malibuluminahotel.models;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.models.AddOn;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;

// TESTER CLASS TO VERIFY DATABASE WORKS
public final class DatabaseTest {

    private DatabaseTest() {
    }

    public static void testAddOnRoundTrip() {
        EntityManager entityManager =
                EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        Long generatedId = null;

        try {
            // PUSH: insert a record into SQLite
            transaction.begin();

            AddOn addOn = new AddOn(
                    "Database Test Breakfast",
                    new BigDecimal("19.99"),
                    "Temporary record used to test persistence"
            );

            entityManager.persist(addOn);
            entityManager.flush();

            generatedId = addOn.getAddOnId();

            transaction.commit();

            System.out.println(
                    "Saved AddOn successfully. Generated ID: " + generatedId
            );

            /*
             * Remove all managed entities from the persistence context.
             * This ensures the next find() actually retrieves the entity
             * again instead of simply returning the same managed object.
             */
            entityManager.clear();

            // PULL: retrieve the record from SQLite
            AddOn retrievedAddOn =
                    entityManager.find(AddOn.class, generatedId);

            if (retrievedAddOn == null) {
                throw new IllegalStateException(
                        "The AddOn was saved but could not be retrieved."
                );
            }

            System.out.println("Retrieved AddOn successfully:");
            System.out.println("ID: " + retrievedAddOn.getAddOnId());
            System.out.println("Name: " + retrievedAddOn.getName());
            System.out.println("Price: " + retrievedAddOn.getPrice());
            System.out.println(
                    "Description: " + retrievedAddOn.getDescription()
            );

            // Optional cleanup so repeated tests do not leave extra records
            transaction.begin();

            entityManager.remove(retrievedAddOn);

            transaction.commit();

            System.out.println("Deleted temporary AddOn successfully.");

        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            System.err.println("Database round-trip test failed.");
            exception.printStackTrace();

        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
