package com.hostel.bean;

import com.hostel.entity.Booking;
import com.hostel.entity.Payment;
import com.hostel.entity.User;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.PaymentStatus;
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
import org.primefaces.PrimeFaces;

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
        loadApprovedBookings();
    }

    private void loadApprovedBookings() {
        User user = getLoggedInUser();
        if (user != null) {
            approvedBookings = bookingService.getBookingsByStatus(BookingStatus.APPROVED)
                .stream()
                .filter(b -> b.getStudent().getId().equals(user.getId()))
                .toList();
        }
    }

    public void initiatePayment() {
        if (selectedBookingId == null) {
            addError("Please select a booking first.");
            return;
        }
        try {
            Payment payment = paymentService.initiatePayment(selectedBookingId, paymentMethod);
            this.currentPayment = payment;

            if (payment.getStatus() == PaymentStatus.VERIFIED) {
                // Already verified — inform user, no need to verify again
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Payment Already Verified",
                        "This booking has already been paid. Transaction ID: "
                        + payment.getTransactionId()));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Payment Initiated",
                        "Transaction ID: " + payment.getTransactionId()
                        + ". Click \"Verify Payment\" to complete."));
            }
            PrimeFaces.current().ajax().addCallbackParam("initiated", true);
        } catch (Exception e) {
            // Extract the root cause message from EJBException if needed
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            addError(msg != null ? msg : "An error occurred. Please try again.");
            PrimeFaces.current().ajax().addCallbackParam("initiated", false);
        }
    }

    /**
     * Calls PaymentWebServiceImpl.verifyPayment() via PaymentService,
     * then refreshes currentPayment so the UI reflects the new status.
     */
    public void verifyPayment(Long paymentId) {
        try {
            Payment result = paymentService.processAndVerifyPayment(paymentId);

            // Update currentPayment so the UI reflects the new status immediately
            this.currentPayment = result;

            if (result.getStatus() == PaymentStatus.VERIFIED) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Payment Verified",
                        "Your payment has been successfully verified via the Payment Web Service. "
                        + "Transaction ID: " + result.getTransactionId()));
                PrimeFaces.current().ajax().addCallbackParam("verifySuccess", true);
                // Refresh approved bookings — paid booking may no longer need payment
                loadApprovedBookings();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Payment Failed",
                        "Verification failed. Please try again or contact support."));
                PrimeFaces.current().ajax().addCallbackParam("verifySuccess", false);
            }
        } catch (Exception e) {
            addError("Verification error: " + e.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("verifySuccess", false);
        }
    }

    public Optional<Payment> getPaymentForBooking(Long bookingId) {
        return paymentService.getPaymentByBooking(bookingId);
    }

    public boolean isCurrentPaymentPending() {
        return currentPayment != null && currentPayment.getStatus() == PaymentStatus.PENDING;
    }

    public boolean isCurrentPaymentVerified() {
        return currentPayment != null && currentPayment.getStatus() == PaymentStatus.VERIFIED;
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Booking> getApprovedBookings() { return approvedBookings; }
    public Long getSelectedBookingId() { return selectedBookingId; }
    public void setSelectedBookingId(Long v) { this.selectedBookingId = v; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public Payment getCurrentPayment() { return currentPayment; }
}
