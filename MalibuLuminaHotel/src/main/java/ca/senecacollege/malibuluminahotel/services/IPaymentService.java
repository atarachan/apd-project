package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;

import java.math.BigDecimal;

public interface IPaymentService {

    Payment processPayment(Bill bill, PaymentMethod method, BigDecimal amount);

    Payment processRefund(Payment payment, BigDecimal amount);

    Payment processDeposit(Bill bill, BigDecimal amount);

    BigDecimal getTotalPayments(Bill bill);
}
