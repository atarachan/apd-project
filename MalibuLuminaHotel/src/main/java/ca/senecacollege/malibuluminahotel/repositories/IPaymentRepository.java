package ca.senecacollege.malibuluminahotel.repositories;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPaymentRepository extends IRepository<Payment, Long> {

    List<Payment> findByBill (
            Bill bill
    );

    List<Payment> findByPaymentMethod (
            PaymentMethod paymentMethod
    );

    List<Payment> findByPaymentStatus (
            PaymentStatus paymentStatus
    );

    Optional<Payment> findByTransactionReference (
            String transactionReference
    );

    List<Payment> findByPaymentDate (
            LocalDate paymentDate
    );

    List<Payment> findByPaymentDateRange (
            LocalDate startDate, LocalDate endDate
    );
}
