package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.AddOn;
import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.Reservation;
import ca.senecacollege.malibuluminahotel.models.ReservationItem;
import ca.senecacollege.malibuluminahotel.models.ReservationItemAddOn;
import ca.senecacollege.malibuluminahotel.models.Room;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;
import ca.senecacollege.malibuluminahotel.models.enums.ReservationStatus;
import ca.senecacollege.malibuluminahotel.models.enums.RoomStatus;
import ca.senecacollege.malibuluminahotel.repositories.IReservationRepository.ReservationItemDraft;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
            List<Reservation> reservations = em.createQuery(
                    "SELECT DISTINCT r FROM Reservation r " +
                    "LEFT JOIN FETCH r.guest " +
                    "LEFT JOIN FETCH r.reservationItems ri " +
                    "LEFT JOIN FETCH ri.room room " +
                    "LEFT JOIN FETCH room.roomType " +
                    "ORDER BY r.reservationId",
                    Reservation.class
            ).getResultList();

            reservations.forEach(r -> r.getReservationItems().size());
            return reservations;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Reservation> findByIdWithDetails(Long reservationId) {
        EntityManager em = createEntityManager();

        try {
            Optional<Reservation> reservation = em.createQuery(
                            "SELECT DISTINCT r FROM Reservation r " +
                            "LEFT JOIN FETCH r.guest " +
                            "LEFT JOIN FETCH r.bill " +
                            "LEFT JOIN FETCH r.reservationItems ri " +
                            "LEFT JOIN FETCH ri.room room " +
                            "LEFT JOIN FETCH room.roomType " +
                            "WHERE r.reservationId = :reservationId",
                            Reservation.class)
                    .setParameter("reservationId", reservationId)
                    .getResultStream()
                    .findFirst();

            if (reservation.isEmpty()) {
                return Optional.empty();
            }

            Reservation r = reservation.get();
            List<ReservationItem> items = r.getReservationItems();
            items.forEach(item -> item.getAddOns().size());

            if (!items.isEmpty()) {
                em.createQuery(
                                "SELECT DISTINCT ria FROM ReservationItemAddOn ria " +
                                "LEFT JOIN FETCH ria.addOn " +
                                "WHERE ria.reservationItem IN :items",
                                ReservationItemAddOn.class)
                        .setParameter("items", items)
                        .getResultList()
                        .forEach(line -> line.getAddOn().getName());
            }

            if (r.getBill() != null) {
                r.getBill().getPayments().size();
            }

            return Optional.of(r);
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation updateReservationDetails(ReservationEditDraft draft) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Reservation reservation = em.find(Reservation.class, draft.reservationId());
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            validateDates(draft.checkIn(), draft.checkOut());
            validateRoomAvailability(em, draft);

            reservation.setCheckInDate(draft.checkIn());
            reservation.setCheckOutDate(draft.checkOut());
            reservation.setAdults(draft.adults());
            reservation.setChildren(draft.children());
            reservation.setStatus(draft.status());

            List<ReservationItem> existingItems = new ArrayList<>(reservation.getReservationItems());
            for (ReservationItem item : existingItems) {
                reservation.removeReservationItem(item);
                em.remove(em.contains(item) ? item : em.merge(item));
            }
            em.flush();

            for (ReservationItemDraft itemDraft : draft.reservationItemDrafts()) {
                Room room = em.find(Room.class, itemDraft.roomId());
                if (room == null) {
                    throw new IllegalArgumentException("Selected room was not found.");
                }

                ReservationItem item = new ReservationItem(
                        reservation,
                        room,
                        itemDraft.nightlyRate(),
                        draft.adults() + draft.children());
                reservation.addReservationItem(item);
                em.persist(item);
                em.flush();

                for (Map.Entry<Long, Integer> entry : itemDraft.addOnQuantities().entrySet()) {
                    AddOn addOn = em.find(AddOn.class, entry.getKey());
                    if (addOn == null) {
                        throw new IllegalArgumentException("Selected add-on was not found.");
                    }

                    int quantity = entry.getValue();
                    BigDecimal lineTotal = addOn.getPrice().multiply(BigDecimal.valueOf(quantity));
                    ReservationItemAddOn line = new ReservationItemAddOn(item, addOn, quantity, lineTotal);
                    item.getAddOns().add(line);
                    em.persist(line);
                }
            }

            updateBillTotals(em, reservation, draft.subtotal(), draft.tax(), draft.total());

            tx.commit();
            return reservation;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Reservation checkoutReservation(Long reservationId, PaymentMethod paymentMethod, BigDecimal paymentAmount) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            Bill bill = reservation.getBill();
            if (bill == null) {
                throw new IllegalStateException("Reservation does not have a bill.");
            }

            BigDecimal required = safe(bill.getBalanceDue());
            if (paymentAmount.compareTo(required) != 0) {
                throw new IllegalArgumentException("Payment must equal the full balance due.");
            }

            Payment payment = new Payment();
            payment.setBill(bill);
            payment.setAmount(paymentAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setTransactionReference("CHK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            bill.addPayment(payment);
            bill.setBalanceDue(BigDecimal.ZERO);

            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            for (ReservationItem item : reservation.getReservationItems()) {
                if (item.getRoom() != null) {
                    item.getRoom().setStatus(RoomStatus.AVAILABLE);
                }
            }

            tx.commit();
            return reservation;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }

    private void validateRoomAvailability(EntityManager em, ReservationEditDraft draft) {
        EnumSet<ReservationStatus> inactiveStatuses = EnumSet.of(
                ReservationStatus.CANCELLED,
                ReservationStatus.CHECKED_OUT);

        for (ReservationItemDraft itemDraft : draft.reservationItemDrafts()) {
            Long conflicts = em.createQuery(
                            "SELECT COUNT(ri) FROM ReservationItem ri " +
                            "WHERE ri.room.roomId = :roomId " +
                            "AND ri.reservation.reservationId <> :reservationId " +
                            "AND ri.reservation.status NOT IN :inactiveStatuses " +
                            "AND ri.reservation.checkInDate < :checkOut " +
                            "AND ri.reservation.checkOutDate > :checkIn",
                            Long.class)
                    .setParameter("roomId", itemDraft.roomId())
                    .setParameter("reservationId", draft.reservationId())
                    .setParameter("inactiveStatuses", inactiveStatuses)
                    .setParameter("checkIn", draft.checkIn())
                    .setParameter("checkOut", draft.checkOut())
                    .getSingleResult();

            if (conflicts > 0) {
                throw new IllegalStateException("Selected room is not available for the edited dates.");
            }
        }
    }

    private void updateBillTotals(EntityManager em, Reservation reservation,
                                  BigDecimal subtotal, BigDecimal tax, BigDecimal total) {
        Bill bill = reservation.getBill();
        if (bill == null) {
            bill = new Bill();
            bill.setReservation(reservation);
            reservation.setBill(bill);
            em.persist(bill);
        }

        bill.setSubtotal(subtotal);
        bill.setDiscount(BigDecimal.ZERO);
        bill.setTax(tax);
        bill.setTotal(total);
        bill.setBalanceDue(total.subtract(sumPayments(bill)));
    }

    private BigDecimal sumPayments(Bill bill) {
        return bill.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
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
            List<ReservationItemDraft> reservationItemDrafts,
            BigDecimal subtotal,
            BigDecimal tax,
            BigDecimal total,
            Payment depositPayment) {

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

            // 3. Load rooms as managed entities and persist one reservation item per room
            for (ReservationItemDraft draft : reservationItemDrafts) {
                Room room = em.find(Room.class, draft.roomId());
                ReservationItem item = new ReservationItem(reservation, room, draft.nightlyRate(), 1);
                em.persist(item);
                em.flush();

                // 4. Persist selected add-on line items for this room
                for (Map.Entry<Long, Integer> entry : draft.addOnQuantities().entrySet()) {
                    AddOn addOn = em.find(AddOn.class, entry.getKey());
                    int qty = entry.getValue();
                    BigDecimal lineTotal = addOn.getPrice().multiply(BigDecimal.valueOf(qty));
                    ReservationItemAddOn lineItem = new ReservationItemAddOn(item, addOn, qty, lineTotal);
                    em.persist(lineItem);
                }
            }

            // 5. Persist bill linked to the reservation
            Bill bill = new Bill();
            bill.setReservation(reservation);
            bill.setSubtotal(subtotal);
            bill.setTax(tax);
            bill.setTotal(total);
            bill.setBalanceDue(total);
            bill.setDiscount(BigDecimal.ZERO);

            if (depositPayment != null) {
                bill.addPayment(depositPayment);
                bill.setBalanceDue(total.subtract(depositPayment.getAmount()));
            }

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
