
package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PaymentRepositoryImpl
        extends AbstractRepository<Payment, Long>
        implements IPaymentRepository {

    public PaymentRepositoryImpl() {
        super(Payment.class);
    }

    @Override
    public List<Payment> findByBill(
            Bill bill) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.bill = :bill " +
                                    "ORDER BY p.paymentDate",
                            Payment.class)
                    .setParameter("bill", bill)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByPaymentMethod(
            PaymentMethod paymentMethod) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.paymentMethod = :paymentMethod " +
                                    "ORDER BY p.paymentDate DESC",
                            Payment.class)
                    .setParameter(
                            "paymentMethod",
                            paymentMethod
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByPaymentStatus(
            PaymentStatus paymentStatus) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.paymentStatus = :paymentStatus " +
                                    "ORDER BY p.paymentDate DESC",
                            Payment.class)
                    .setParameter(
                            "paymentStatus",
                            paymentStatus
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Payment> findByTransactionReference(
            String transactionReference) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.transactionReference = :reference",
                            Payment.class)
                    .setParameter(
                            "reference",
                            transactionReference
                    )
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByPaymentDate(
            LocalDate paymentDate) {

        EntityManager em = createEntityManager();

        LocalDateTime startOfDay =
                paymentDate.atStartOfDay();

        LocalDateTime startOfNextDay =
                paymentDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.paymentDate >= :startOfDay " +
                                    "AND p.paymentDate < :startOfNextDay " +
                                    "ORDER BY p.paymentDate",
                            Payment.class)
                    .setParameter(
                            "startOfDay",
                            startOfDay
                    )
                    .setParameter(
                            "startOfNextDay",
                            startOfNextDay
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Payment> findByPaymentDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        EntityManager em = createEntityManager();

        LocalDateTime rangeStart =
                startDate.atStartOfDay();

        LocalDateTime rangeEndExclusive =
                endDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT p FROM Payment p " +
                                    "WHERE p.paymentDate >= :rangeStart " +
                                    "AND p.paymentDate < :rangeEnd " +
                                    "ORDER BY p.paymentDate",
                            Payment.class)
                    .setParameter(
                            "rangeStart",
                            rangeStart
                    )
                    .setParameter(
                            "rangeEnd",
                            rangeEndExclusive
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
