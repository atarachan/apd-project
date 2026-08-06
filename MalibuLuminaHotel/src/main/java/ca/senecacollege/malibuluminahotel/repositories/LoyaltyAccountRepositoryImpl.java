package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class LoyaltyAccountRepositoryImpl
        extends AbstractRepository<LoyaltyAccount, Long>
        implements ILoyaltyAccountRepository {

    public LoyaltyAccountRepositoryImpl() {
        super(LoyaltyAccount.class);
    }

    @Override
    public Optional<LoyaltyAccount> findByGuest(
            Guest guest) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT la FROM LoyaltyAccount la WHERE la.guest = :guest",
                            LoyaltyAccount.class)
                    .setParameter("guest", guest)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public Optional<LoyaltyAccount> findByMemberNumber(
            String memberNumber) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT la FROM LoyaltyAccount la WHERE la.memberNumber = :memberNumber",
                            LoyaltyAccount.class)
                    .setParameter("memberNumber", memberNumber)
                    .getResultStream()
                    .findFirst();

        } finally {
            em.close();
        }
    }

    @Override
    public List<LoyaltyAccount> findByMinimumPoints(
            int minimumPoints) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT la FROM LoyaltyAccount la " +
                                    "WHERE la.currentPoints >= :minimumPoints " +
                                    "ORDER BY la.currentPoints DESC",
                            LoyaltyAccount.class)
                    .setParameter("minimumPoints", minimumPoints)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<LoyaltyAccount> findAll() {

        EntityManager em = createEntityManager();

        try {

            return em.createQuery(
                    "SELECT la FROM LoyaltyAccount la " +
                            "LEFT JOIN FETCH la.guest " +
                            "ORDER BY la.memberNumber",
                    LoyaltyAccount.class
            ).getResultList();

        } finally {

            em.close();

        }
    }
}
