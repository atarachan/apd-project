package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.models.LoyaltyTransaction;
import ca.senecacollege.malibuluminahotel.models.enums.LoyaltyTransactionType;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoyaltyTransactionRepositoryImpl extends AbstractRepository<LoyaltyTransaction, Long> implements ILoyaltyTransactionRepository {

    public LoyaltyTransactionRepositoryImpl() {
        super(LoyaltyTransaction.class);
    }

    @Override
    public List<LoyaltyTransaction> findByLoyaltyAccount(
            LoyaltyAccount loyaltyAccount) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT lt FROM LoyaltyTransaction lt " +
                                    "WHERE lt.loyaltyAccount = :loyaltyAccount " +
                                    "ORDER BY lt.transactionDate DESC",
                            LoyaltyTransaction.class)
                    .setParameter(
                            "loyaltyAccount",
                            loyaltyAccount
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<LoyaltyTransaction> findByTransactionType(
            LoyaltyTransactionType transactionType) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT lt FROM LoyaltyTransaction lt " +
                                    "WHERE lt.transactionType = :transactionType " +
                                    "ORDER BY lt.transactionDate DESC",
                            LoyaltyTransaction.class)
                    .setParameter(
                            "transactionType",
                            transactionType
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<LoyaltyTransaction> findByLoyaltyAccountAndType(
            LoyaltyAccount loyaltyAccount,
            LoyaltyTransactionType transactionType) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT lt FROM LoyaltyTransaction lt " +
                                    "WHERE lt.loyaltyAccount = :loyaltyAccount " +
                                    "AND lt.transactionType = :transactionType " +
                                    "ORDER BY lt.transactionDate DESC",
                            LoyaltyTransaction.class)
                    .setParameter(
                            "loyaltyAccount",
                            loyaltyAccount
                    )
                    .setParameter(
                            "transactionType",
                            transactionType
                    )
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<LoyaltyTransaction> findByTransactionDate(
            LocalDate transactionDate) {

        EntityManager em = createEntityManager();

        LocalDateTime startOfDay =
                transactionDate.atStartOfDay();

        LocalDateTime startOfNextDay =
                transactionDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT lt FROM LoyaltyTransaction lt " +
                                    "WHERE lt.transactionDate >= :startOfDay " +
                                    "AND lt.transactionDate < :startOfNextDay " +
                                    "ORDER BY lt.transactionDate",
                            LoyaltyTransaction.class)
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
    public List<LoyaltyTransaction> findByTransactionDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        EntityManager em = createEntityManager();

        LocalDateTime rangeStart =
                startDate.atStartOfDay();

        LocalDateTime rangeEndExclusive =
                endDate.plusDays(1).atStartOfDay();

        try {
            return em.createQuery(
                            "SELECT lt FROM LoyaltyTransaction lt " +
                                    "WHERE lt.transactionDate >= :rangeStart " +
                                    "AND lt.transactionDate < :rangeEnd " +
                                    "ORDER BY lt.transactionDate",
                            LoyaltyTransaction.class)
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
