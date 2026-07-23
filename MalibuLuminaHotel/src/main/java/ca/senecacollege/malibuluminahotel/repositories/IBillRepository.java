package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IBillRepository extends IRepository<Bill, Long> {

    Optional<Bill> findByReservation(
            Reservation reservation
    );

    List<Bill> findByBillDate(
            LocalDate billDate
    );

    List<Bill> findByBillDateRange(
            LocalDate startDate,
            LocalDate endDate
    );

    List<Bill> findOutstandingBills();

    List<Bill> findByMinimumBalanceDue(
            BigDecimal minimumBalance
    );
}
