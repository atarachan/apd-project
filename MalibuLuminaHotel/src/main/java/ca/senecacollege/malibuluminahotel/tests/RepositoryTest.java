package ca.senecacollege.malibuluminahotel.tests;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.repositories.AddOnRepositoryImpl;
import ca.senecacollege.malibuluminahotel.repositories.IAddOnRepository;

import java.math.BigDecimal;

// TEST TO VERIFY REPOSITORY WORKS -- TO BE DELETED LATER!!!!
public final class RepositoryTest {

    private RepositoryTest() {
    }

    public static void testAddOnRepository() {
        IAddOnRepository repository = new AddOnRepositoryImpl();

        AddOn addOn = new AddOn(
                "Airport Shuttle",
                new BigDecimal("45.00"),
                "Transportation between the hotel and airport"
        );

        // CREATE
        repository.save(addOn);

        Long id = addOn.getAddOnId();

        System.out.println("Saved AddOn ID: " + id);

        // READ BY ID
        AddOn found = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Could not find saved AddOn"
                        )
                );

        System.out.println("Found by ID: " + found.getName());

        // UPDATE
        found.setPrice(new BigDecimal("49.99"));

        AddOn updated = repository.update(found);

        System.out.println("Updated price: " + updated.getPrice());

        // CUSTOM QUERY
        repository.findByName("Airport Shuttle")
                .ifPresent(result ->
                        System.out.println(
                                "Found by name: " + result.getName()
                        )
                );

        // FIND ALL
        System.out.println(
                "Total AddOns: " + repository.findAll().size()
        );

        // DELETE
        repository.deleteById(id);

        boolean stillExists = repository.findById(id).isPresent();

        System.out.println(
                "Still exists after delete: " + stillExists
        );
    }
}
