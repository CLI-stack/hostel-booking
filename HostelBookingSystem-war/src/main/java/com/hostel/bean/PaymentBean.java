package com.hostel.bean;

import com.hostel.entity.Booking;
import com.hostel.entity.Payment;
import com.hostel.entity.User;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.service.BookingService;
import com.hostel.service.PaymentService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named
@ViewScoped
public class PaymentBean implements Serializable {

    @Inject private PaymentService paymentService;
    @Inject private BookingService bookingService;

    private List<Booking> approvedBookings;
    private Long selectedBookingId;
    private String paymentMethod = "FPX";
    private Payment currentPayment;

    @PostConstruct
    public void init() {
        User user = getLoggedInUser();
        if (user != null) {
            approvedBookings = bookingService.getBookingsByStatus(BookingStatus.APPROVED)
                .stream()
                .filter(b -> b.getStudent().getId().equals(user.getId()))
                .toList();
        }
    }

    public void initiatePayment() {
        try {
            Payment payment = paymentService.initiatePayment(selectedBookingId, paymentMethod);
            this.currentPayment = payment;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Payment Initiated",
                    "Transaction ID: " + payment.getTransactionId() + ". Click Verify to complete."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Payment Error", e.getMessage()));
        }
    }

    public void verifyPayment(Long paymentId) {
        try {
            Payment result = paymentService.processAndVerifyPayment(paymentId);
            if (result.getStatus().name().equals("VERIFIED")) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Payment Verified", "Your payment has been successfully verified."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Payment Failed", "Verification failed. Please try again."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public Optional<Payment> getPaymentForBooking(Long bookingId) {
        return paymentService.getPaymentByBooking(bookingId);
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Booking> getApprovedBookings() { return approvedBookings; }
    public Long getSelectedBookingId() { return selectedBookingId; }
    public void setSelectedBookingId(Long selectedBookingId) { this.selectedBookingId = selectedBookingId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public Payment getCurrentPayment() { return currentPayment; }
}
