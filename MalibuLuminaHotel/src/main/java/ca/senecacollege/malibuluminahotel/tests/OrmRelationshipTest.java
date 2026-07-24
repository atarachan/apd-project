package ca.senecacollege.malibuluminahotel.tests;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;
import ca.senecacollege.malibuluminahotel.config.EntityManagerFactoryProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class OrmRelationshipTest {

    public static void testOrmRelationships() {

        EntityManager em = EntityManagerFactoryProvider.createEntityManager();

        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            /*
             * 1. Create the Guest.
             */
            Guest guest = new Guest(
                    "ORM",
                    "Tester",
                    "orm.test." + UUID.randomUUID() + "@hotel.ca",
                    "416-555-0199"
            );

            em.persist(guest);

            /*
             * 2. Create the Reservation connected to the Guest.
             */
            Reservation reservation = new Reservation(
                    guest,
                    LocalDate.now().plusDays(5),
                    LocalDate.now().plusDays(8)
            );

            em.persist(reservation);

            /*
             * 3. Create the Bill connected to the Reservation.
             */
            Bill bill = new Bill(
                    reservation,
                    new BigDecimal("500.00"),
                    new BigDecimal("25.00"),
                    new BigDecimal("61.75"),
                    new BigDecimal("536.75"),
                    new BigDecimal("536.75")
            );

            em.persist(bill);

            /*
             * 4. Create two Payments connected to the Bill.
             */
            Payment firstPayment = new Payment(
                    bill,
                    new BigDecimal("200.00"),
                    PaymentMethod.CREDIT_CARD,
                    PaymentStatus.COMPLETED,
                    "ORM-PAYMENT-1-" + UUID.randomUUID()
            );

            Payment secondPayment = new Payment(
                    bill,
                    new BigDecimal("100.00"),
                    PaymentMethod.CREDIT_CARD,
                    PaymentStatus.COMPLETED,
                    "ORM-PAYMENT-2-" + UUID.randomUUID()
            );

            /*
             * addPayment() synchronizes both sides:
             *
             * bill.getPayments().add(payment)
             * payment.setBill(bill)
             *
             * Because Bill.payments uses CascadeType.ALL,
             * persisting the Bill allows Hibernate to persist
             * payments added to its collection.
             */
            bill.addPayment(firstPayment);
            bill.addPayment(secondPayment);

            /*
             * Since bill was already persisted, the new children will be
             * detected during flush through the cascade relationship.
             */
            em.flush();

            Long guestId = guest.getGuestId();
            Long reservationId = reservation.getReservationId();
            Long billId = bill.getBillId();
            Long firstPaymentId = firstPayment.getPaymentId();
            Long secondPaymentId = secondPayment.getPaymentId();

            require(guestId != null,
                    "Guest ID was not generated.");

            require(reservationId != null,
                    "Reservation ID was not generated.");

            require(billId != null,
                    "Bill ID was not generated.");

            require(firstPaymentId != null,
                    "First Payment ID was not generated.");

            require(secondPaymentId != null,
                    "Second Payment ID was not generated.");

            /*
             * Important:
             *
             * clear() detaches every entity. From this point forward,
             * the test cannot pass merely because the original Java
             * objects are still connected in memory.
             */
            em.clear();

            /*
             * 5. Reload the Bill with its Reservation and Payments.
             *
             * JOIN FETCH loads the lazy relationships while the
             * EntityManager is open.
             *
             * DISTINCT avoids duplicate Bill results caused by joining
             * the one-to-many payments collection.
             */
            Bill loadedBill = em.createQuery(
                            "SELECT DISTINCT b FROM Bill b " +
                                    "JOIN FETCH b.reservation " +
                                    "LEFT JOIN FETCH b.payments " +
                                    "WHERE b.billId = :billId",
                            Bill.class)
                    .setParameter("billId", billId)
                    .getSingleResult();

            /*
             * Verify Bill → Reservation.
             */
            require(loadedBill.getReservation() != null,
                    "Bill did not reload its Reservation relationship.");

            require(
                    loadedBill.getReservation()
                            .getReservationId()
                            .equals(reservationId),
                    "Bill points to the wrong Reservation."
            );

            /*
             * Verify Bill → Payments.
             */
            require(
                    loadedBill.getPayments().size() == 2,
                    "Expected two Payments, but found "
                            + loadedBill.getPayments().size()
            );

            boolean foundFirstPayment = loadedBill.getPayments()
                    .stream()
                    .anyMatch(payment ->
                            payment.getPaymentId()
                                    .equals(firstPaymentId));

            boolean foundSecondPayment = loadedBill.getPayments()
                    .stream()
                    .anyMatch(payment ->
                            payment.getPaymentId()
                                    .equals(secondPaymentId));

            require(foundFirstPayment,
                    "The first Payment was not found in Bill.payments.");

            require(foundSecondPayment,
                    "The second Payment was not found in Bill.payments.");

            /*
             * Verify Payment → Bill.
             *
             * Every reloaded Payment should point back to the same Bill.
             */
            for (Payment loadedPayment : loadedBill.getPayments()) {

                require(loadedPayment.getBill() != null,
                        "A Payment has no Bill relationship.");

                require(
                        loadedPayment.getBill()
                                .getBillId()
                                .equals(billId),
                        "A Payment points to the wrong Bill."
                );
            }

            /*
             * Reload one Payment independently to prove the owning-side
             * foreign-key mapping works.
             */
            Payment loadedPayment = em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "JOIN FETCH p.bill " +
                                    "WHERE p.paymentId = :paymentId",
                            Payment.class)
                    .setParameter(
                            "paymentId",
                            firstPaymentId
                    )
                    .getSingleResult();

            require(
                    loadedPayment.getBill()
                            .getBillId()
                            .equals(billId),
                    "Reloaded Payment did not reference the expected Bill."
            );

            require(
                    loadedPayment.getAmount()
                            .compareTo(new BigDecimal("200.00")) == 0,
                    "Payment amount was not persisted correctly."
            );

            require(
                    loadedPayment.getPaymentStatus()
                            == PaymentStatus.COMPLETED,
                    "Payment status was not persisted correctly."
            );

            require(
                    loadedPayment.getPaymentMethod()
                            == PaymentMethod.CREDIT_CARD,
                    "Payment method was not persisted correctly."
            );

            System.out.println("ORM relationship test passed.");
            System.out.println(
                    "Guest ID: " + guestId
            );
            System.out.println(
                    "Reservation ID: " + reservationId
            );
            System.out.println(
                    "Bill ID: " + billId
            );
            System.out.println(
                    "Payments loaded through Bill: "
                            + loadedBill.getPayments().size()
            );

            /*
             * This is a test, so roll back everything after proving
             * Hibernate wrote and reloaded the graph.
             */
            transaction.rollback();

        } catch (Exception exception) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "ORM relationship test failed.",
                    exception
            );

        } finally {
            em.close();
        }
    }

    private static void require(
            boolean condition,
            String errorMessage) {

        if (!condition) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
