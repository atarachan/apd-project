package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;
import ca.senecacollege.malibuluminahotel.factories.RoomFactory;
import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.RoomType;
import ca.senecacollege.malibuluminahotel.models.enums.RoomTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;

public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private DataSeeder() {}

    public static void seed() {
        EntityManager em = EntityManagerFactoryProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            Long count = em.createQuery("SELECT COUNT(rt) FROM RoomType rt", Long.class)
                    .getSingleResult();

            if (count > 0) {
                logger.info("Database already seeded — skipping.");
                return;
            }

            logger.info("Seeding database with default room types, rooms, and add-ons...");

            tx.begin();

            // Room types — built via RoomFactory (Factory Pattern)
            RoomType single     = RoomFactory.createRoomType(RoomTypeName.SINGLE);
            RoomType dbl        = RoomFactory.createRoomType(RoomTypeName.DOUBLE);
            RoomType penthouse  = RoomFactory.createRoomType(RoomTypeName.PENTHOUSE);

            em.persist(single);
            em.persist(dbl);
            em.persist(penthouse);
            em.flush();

            // Single rooms — floors 1 and 2
            em.persist(RoomFactory.createRoom(single, "101", 1));
            em.persist(RoomFactory.createRoom(single, "102", 1));
            em.persist(RoomFactory.createRoom(single, "103", 1));
            em.persist(RoomFactory.createRoom(single, "201", 2));
            em.persist(RoomFactory.createRoom(single, "202", 2));

            // Double rooms — floors 1, 2, 3
            em.persist(RoomFactory.createRoom(dbl, "104", 1));
            em.persist(RoomFactory.createRoom(dbl, "204", 2));
            em.persist(RoomFactory.createRoom(dbl, "304", 3));

            // Penthouse rooms — floor 4
            em.persist(RoomFactory.createRoom(penthouse, "401", 4));
            em.persist(RoomFactory.createRoom(penthouse, "402", 4));

            // Default add-ons
            em.persist(new AddOn("Daily Breakfast", new BigDecimal("25.00"),
                    "Fresh breakfast served every morning during your stay."));

            em.persist(new AddOn("Wi-Fi", new BigDecimal("10.00"),
                    "High-speed wireless internet throughout your stay."));

            em.persist(new AddOn("Parking", new BigDecimal("15.00"),
                    "Secure on-site parking throughout your stay."));

            em.persist(new AddOn("Spa Package", new BigDecimal("80.00"),
                    "Full access to our spa and wellness facilities."));

            tx.commit();

            logger.info("Seeding complete: 3 room types, 10 rooms, 4 add-ons.");

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Seeding failed — rolling back.", e);
            throw e;
        } finally {
            em.close();
        }
    }
}
