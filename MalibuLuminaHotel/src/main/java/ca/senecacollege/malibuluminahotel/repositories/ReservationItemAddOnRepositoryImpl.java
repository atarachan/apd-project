package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.ReservationItemAddOn;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ReservationItemAddOnRepositoryImpl extends AbstractRepository<ReservationItemAddOn, Long> implements IReservationItemAddOnRepository {

    public ReservationItemAddOnRepositoryImpl() {
        super(ReservationItemAddOn.class);
    }

    @Override
    public List<ReservationItemAddOn> findByReservationItem(ReservationItem reservationItem) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ria FROM ReservationItemAddOn ria " +
                                    "WHERE ria.reservationItem = :reservationItem " +
                                    "ORDER BY ria.reservationAddOnId",
                            ReservationItemAddOn.class)
                    .setParameter("reservationItem", reservationItem)
                    .getResultList();

        }
        finally {
            em.close();
        }
    }

    @Override
    public List<ReservationItemAddOn> findByAddOn(AddOn addOn) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ria FROM ReservationItemAddOn ria " +
                                    "WHERE ria.addOn = :addOn " +
                                    "ORDER BY ria.reservationAddOnId",
                            ReservationItemAddOn.class)
                    .setParameter("addOn", addOn)
                    .getResultList();

        }
        finally {
            em.close();
        }
    }

    @Override
    public Optional<ReservationItemAddOn> findByReservationItemAndAddOn(ReservationItem reservationItem, AddOn addOn) {

        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT ria FROM ReservationItemAddOn ria " +
                                    "WHERE ria.reservationItem = :reservationItem " +
                                    "AND ria.addOn = :addOn",
                            ReservationItemAddOn.class)
                    .setParameter("reservationItem", reservationItem)
                    .setParameter("addOn", addOn)
                    .getResultStream()
                    .findFirst();

        }
        finally {
            em.close();
        }
    }
}
