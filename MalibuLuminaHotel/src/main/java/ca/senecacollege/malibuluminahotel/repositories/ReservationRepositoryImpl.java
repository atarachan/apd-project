package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.ReservationItemAddOn;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReservationRepositoryImpl extends AbstractRepository<Reservation, Long>
        implements IReservationRepository {

    public ReservationRepositoryImpl() {
        super(Reservation.class);
    }

    @Override
    public List<Reservation> findByGuest(Guest guest) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.guest = :guest " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("guest", guest)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.status = :status " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("status", status)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByCheckInDate(LocalDate checkInDate) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.checkInDate = :checkInDate " +
                                    "ORDER BY r.createdDate",
                            Reservation.class
                    )
                    .setParameter("checkInDate", checkInDate)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = createEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reservation r " +
                                    "WHERE r.checkInDate BETWEEN :startDate AND :endDate " +
                                    "ORDER BY r.checkInDate",
                            Reservation.class
                    )
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public List<Reservation> findAllWithDetails() {
        EntityManager em = createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT r FROM Reservation r LEFT JOIN FETCH r.reservationItems",
                    Reservation.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    /*
     * Persists the full booking in a single transaction.
     *
     * All entity references (Room, AddOn) are loaded fresh from this EntityManager
     * so there are no detached-entity errors — the caller only passes IDs for
     * anything that was fetched in a prior transaction.
     */
    @Override
    public Reservation saveFullBooking(
            Guest guest,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children,
            Long roomId,
            BigDecimal nightlyRate,
            Map<Long, Integer> addOnQuantities,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total) {

        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. Persist or re-attach guest
            Guest managedGuest;
            if (guest.getGuestId() == null) {
                em.persist(guest);
                managedGuest = guest;
            } else {
                managedGuest = em.find(Guest.class, guest.getGuestId());
            }

            // 2. Persist reservation
            Reservation reservation = new Reservation(managedGuest, checkIn, checkOut, adults, children);
            em.persist(reservation);
            em.flush();

            // 3. Load room as a managed entity and persist the reservation item
            Room room = em.find(Room.class, roomId);
            ReservationItem item = new ReservationItem(reservation, room, nightlyRate, 1);
            em.persist(item);
            em.flush();

            // 4. Persist selected add-on line items
            for (Map.Entry<Long, Integer> entry : addOnQuantities.entrySet()) {
                AddOn addOn = em.find(AddOn.class, entry.getKey());
                int qty = entry.getValue();
                BigDecimal lineTotal = addOn.getPrice().multiply(BigDecimal.valueOf(qty));
                ReservationItemAddOn lineItem = new ReservationItemAddOn(item, addOn, qty, lineTotal);
                em.persist(lineItem);
            }

            // 5. Persist bill linked to the reservation
            Bill bill = new Bill();
            bill.setReservation(reservation);
            bill.setSubtotal(subtotal);
            bill.setTax(tax);
            bill.setTotal(total);
            bill.setBalanceDue(total);
            bill.setDiscount(BigDecimal.ZERO);
            em.persist(bill);

            tx.commit();

            return reservation;

        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
