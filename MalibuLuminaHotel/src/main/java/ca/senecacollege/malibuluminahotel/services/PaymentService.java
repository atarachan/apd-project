package ca.senecacollege.malibuluminahotel.services;

import ca.senecacollege.malibuluminahotel.models.Bill;
import ca.senecacollege.malibuluminahotel.models.Guest;
import ca.senecacollege.malibuluminahotel.models.LoyaltyAccount;
import ca.senecacollege.malibuluminahotel.models.Payment;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentMethod;
import ca.senecacollege.malibuluminahotel.models.enums.PaymentStatus;
import ca.senecacollege.malibuluminahotel.repositories.IBillRepository;
import ca.senecacollege.malibuluminahotel.repositories.IPaymentRepository;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for processing payments, refunds, and deposits.
 */
public class PaymentService implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final IBillRepository billRepository;
    private final ILoyaltyService loyaltyService;

    @Inject
    public PaymentService(IPaymentRepository paymentRepository,
                          IBillRepository billRepository,
                          ILoyaltyService loyaltyService) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.loyaltyService = loyaltyService;
    }

    /**
     * Process a payment for a bill.
     *
     * @param bill   The bill to apply payment to
     * @param method Payment method
     * @param amount Payment amount
     * @return The created payment
     * @throws IllegalArgumentException if amount exceeds balance due
     */
    @Override
    public Payment processPayment(Bill bill, PaymentMethod method, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        if (amount.compareTo(bill.getBalanceDue()) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount (" + amount + ") exceeds balance due (" + bill.getBalanceDue() + ")");
        }

        // Create payment record
        String transactionRef = generateTransactionReference();
        Payment payment = new Payment(bill, amount, method, PaymentStatus.COMPLETED, transactionRef);
        payment.setPaymentDate(LocalDateTime.now());

        // Update bill balance
        BigDecimal newBalance = bill.getBalanceDue().subtract(amount);
        bill.setBalanceDue(newBalance);

        // Save payment and update bill
        paymentRepository.save(payment);
        billRepository.save(bill);

        // Award loyalty points if guest is enrolled
        awardLoyaltyPoints(bill, amount);

        return payment;
    }

    /**
     * Process a refund for a payment.
     *
     * @param payment The original payment to refund
     * @param amount  Refund amount
     * @return The refund payment record
     * @throws IllegalArgumentException if amount exceeds original payment
     */
    @Override
    public Payment processRefund(Payment payment, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Refund amount (" + amount + ") exceeds original payment (" + payment.getAmount() + ")");
        }

        // Create refund payment record (negative amount)
        String transactionRef = generateTransactionReference();
        Payment refund = new Payment(
                payment.getBill(),
                amount.negate(),
                payment.getPaymentMethod(),
                PaymentStatus.REFUNDED,
                transactionRef);

        // Update bill balance (add refund amount back)
        Bill bill = payment.getBill();
        BigDecimal newBalance = bill.getBalanceDue().add(amount);
        bill.setBalanceDue(newBalance);

        // Save refund and update bill
        paymentRepository.save(refund);
        billRepository.save(bill);

        return refund;
    }

    /**
     * Process a deposit payment.
     *
     * @param bill   The bill to apply deposit to
     * @param amount Deposit amount
     * @return The deposit payment
     */
    @Override
    public Payment processDeposit(Bill bill, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        // Create deposit payment
        String transactionRef = generateTransactionReference();
        Payment deposit = new Payment(bill, amount, PaymentMethod.CREDIT_CARD, PaymentStatus.COMPLETED, transactionRef);
        deposit.setPaymentDate(LocalDateTime.now());

        // Update bill balance
        BigDecimal newBalance = bill.getBalanceDue().subtract(amount);
        bill.setBalanceDue(newBalance);

        // Save deposit and update bill
        paymentRepository.save(deposit);
        billRepository.save(bill);

        return deposit;
    }

    /**
     * Get total payments for a bill.
     */
    @Override
    public BigDecimal getTotalPayments(Bill bill) {
        return paymentRepository.findByBill(bill).stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Generate a unique transaction reference.
     */
    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Award loyalty points for payment if guest is enrolled.
     */
    private void awardLoyaltyPoints(Bill bill, BigDecimal paymentAmount) {
        try {
            // Get guest from bill's reservation
            Guest guest = bill.getReservation().getGuest();

            // Check if guest has loyalty account
            Optional<LoyaltyAccount> accountOpt = loyaltyService.getAccountByGuest(guest);
            if (accountOpt.isPresent()) {
                loyaltyService.earnPoints(accountOpt.get(), paymentAmount);
            }
        } catch (Exception e) {
            // Log error but don't fail payment processing
            System.err.println("Failed to award loyalty points: " + e.getMessage());
        }
    }
}
