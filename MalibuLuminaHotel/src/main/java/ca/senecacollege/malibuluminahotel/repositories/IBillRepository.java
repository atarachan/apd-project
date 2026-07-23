package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BillRepositoryImpl extends AbstractRepository<Bill, Long> implements IBillRepository {

    public BillRepositoryImpl() {
        super(Bill.class);
    }

    @Override
    public Optional<Bill> findByReservation(Reservation reservation) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT b FROM Bill b WHERE b.reservation = :reservation",
                            Bill.class)
                    .setParameter("reservation", reservation)
                    .getResultStream()
                    .findFirst();

        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Bill> findByBillDate(LocalDate billDate) {
        EntityManager em = createEntityManager();

        LocalDateTime startOfDay = billDate.atStartOfDay();

        LocalDateTime startOfNextDay = billDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT b FROM Bill b " +
                                    "WHERE b.billDate >= :startOfDay " +
                                    "AND b.billDate < :startOfNextDay " +
                                    "ORDER BY b.billDate",
                            Bill.class)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("startOfNextDay", startOfNextDay)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Bill> findByBillDateRange(LocalDate startDate, LocalDate endDate) {

        EntityManager em = createEntityManager();

        LocalDateTime rangeStart = startDate.atStartOfDay();

        LocalDateTime rangeEndExclusive = endDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT b FROM Bill b " +
                                    "WHERE b.billDate >= :rangeStart " +
                                    "AND b.billDate < :rangeEnd " +
                                    "ORDER BY b.billDate",
                            Bill.class)
                    .setParameter("rangeStart", rangeStart)
                    .setParameter("rangeEnd", rangeEndExclusive).getResultList();

        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Bill> findOutstandingBills() {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT b FROM Bill b " +
                                    "WHERE b.balanceDue > 0 " +
                                    "ORDER BY b.billDate",
                            Bill.class).getResultList();

        } finally {
            em.close();
        }

    }

    @Override
    public List<Bill> findByMinimumBalanceDue(BigDecimal minimumBalance) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT b FROM Bill b " +
                                    "WHERE b.balanceDue >= :minimumBalance " +
                                    "ORDER BY b.balanceDue DESC",
                            Bill.class)
                    .setParameter(
                            "minimumBalance",
                            minimumBalance
                    )
                    .getResultList();

        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Bill> findAllWithDetails() {
        EntityManager em = createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT b FROM Bill b " +
                    "LEFT JOIN FETCH b.reservation r " +
                    "LEFT JOIN FETCH r.guest " +
                    "ORDER BY b.billId",
                    Bill.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
