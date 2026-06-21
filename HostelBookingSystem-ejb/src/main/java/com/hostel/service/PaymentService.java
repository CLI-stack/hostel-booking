package com.hostel.service;

import com.hostel.dao.BookingDAO;
import com.hostel.dao.PaymentDAO;
import com.hostel.entity.Booking;
import com.hostel.entity.Payment;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.PaymentStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class.getName());

    @Inject private PaymentDAO paymentDAO;
    @Inject private BookingDAO bookingDAO;
    @Inject private NotificationService notificationService;

    public Payment initiatePayment(Long bookingId, String paymentMethod) {
        Booking booking = bookingDAO.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Payment can only be made for approved bookings.");
        }

        Optional<Payment> existing = paymentDAO.findByBooking(bookingId);
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.VERIFIED) {
            throw new IllegalStateException("Payment already verified for this booking.");
        }

        Payment payment = existing.orElse(new Payment());
        payment.setBooking(booking);
        payment.setAmount(booking.getRoom().getPricePerSemester());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString().replace("-", "").toUpperCase());

        return existing.isPresent() ? paymentDAO.update(payment) : paymentDAO.save(payment);
    }

    public Payment processAndVerifyPayment(Long paymentId) {
        Payment payment = paymentDAO.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Verify payment — in production this would call the external JAX-WS
        // PaymentWebService endpoint over HTTPS. The web service is available
        // for external clients at: /PaymentWebServiceImplService?wsdl
        boolean verified = verifyInternally(payment.getTransactionId(), payment.getAmount());

        if (verified) {
            payment.setStatus(PaymentStatus.VERIFIED);
            payment.setVerifiedAt(LocalDateTime.now());

            notificationService.sendToUser(payment.getBooking().getStudent(),
                "Payment Confirmed",
                "Your payment of RM " + payment.getAmount() +
                " for Room " + payment.getBooking().getRoom().getRoomNumber() +
                " has been verified. Transaction ID: " + payment.getTransactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);

            notificationService.sendToUser(payment.getBooking().getStudent(),
                "Payment Failed",
                "Your payment could not be verified. Please try again or contact support.");
        }

        return paymentDAO.update(payment);
    }

    public Payment refundPayment(Long paymentId, String reason) {
        Payment payment = paymentDAO.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.VERIFIED) {
            throw new IllegalStateException("Only verified payments can be refunded.");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRemarks(reason);

        notificationService.sendToUser(payment.getBooking().getStudent(),
            "Payment Refunded",
            "Your payment of RM " + payment.getAmount() + " has been refunded. Reason: " + reason);

        return paymentDAO.update(payment);
    }

    public Optional<Payment> getPaymentByBooking(Long bookingId) {
        return paymentDAO.findByBooking(bookingId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }

    public BigDecimal getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }

    // Replicates the same logic as PaymentWebServiceImpl — shared verification rules
    private boolean verifyInternally(String transactionId, BigDecimal amount) {
        if (transactionId == null || transactionId.isBlank()) return false;
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (transactionId.startsWith("FAIL")) return false;
        LOG.info("Payment verified internally: txn=" + transactionId + " amount=" + amount);
        return true;
    }
}
