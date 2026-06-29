package com.hostel.bean;

import com.hostel.entity.Booking;
import com.hostel.entity.CheckInOut;
import com.hostel.entity.Payment;
import com.hostel.entity.User;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.PaymentStatus;
import com.hostel.service.BookingService;
import com.hostel.service.CheckInOutService;
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
public class StaffBookingBean implements Serializable {

    @Inject private BookingService bookingService;
    @Inject private CheckInOutService checkInOutService;
    @Inject private PaymentService paymentService;

    private List<Booking> pendingBookings;
    private List<Booking> allBookings;
    private List<CheckInOut> checkedInRecords;
    private Booking selectedBooking;
    private String rejectionReason;
    private String roomCondition;
    private String notes;

    @PostConstruct
    public void init() {
        pendingBookings = bookingService.getPendingBookings();
        allBookings = bookingService.getAllBookings();
        checkedInRecords = checkInOutService.getCurrentlyCheckedIn();
    }

    public void approveBooking(Long bookingId) {
        User staff = getStaff();
        if (staff == null) return;
        try {
            bookingService.approveBooking(bookingId, staff.getId());
            refresh();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Booking Approved", "Booking #" + bookingId + " approved."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void rejectBooking(Long bookingId) {
        User staff = getStaff();
        if (staff == null) return;
        try {
            bookingService.rejectBooking(bookingId, staff.getId(), rejectionReason);
            rejectionReason = null;
            refresh();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Booking Rejected", "Booking #" + bookingId + " rejected."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void checkIn(Long bookingId) {
        User staff = getStaff();
        if (staff == null) return;

        // Enforce: payment must be VERIFIED before check-in is allowed
        if (!isPaymentVerified(bookingId)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Cannot Check In — Payment Not Verified",
                    "The student's payment must be VERIFIED before check-in. "
                    + "Current payment status: " + getPaymentStatusLabel(bookingId)));
            return;
        }

        try {
            checkInOutService.checkIn(bookingId, staff.getId(), roomCondition, notes);
            refresh();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Checked In", "Student checked in successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    /** Returns true only if the booking has a VERIFIED payment */
    public boolean isPaymentVerified(Long bookingId) {
        Optional<Payment> p = paymentService.getPaymentByBooking(bookingId);
        return p.isPresent() && p.get().getStatus() == PaymentStatus.VERIFIED;
    }

    /** Returns the human-readable payment status for a booking */
    public String getPaymentStatusLabel(Long bookingId) {
        Optional<Payment> p = paymentService.getPaymentByBooking(bookingId);
        if (p.isEmpty()) return "NOT PAID";
        return p.get().getStatus().name();
    }

    public void checkOut(Long bookingId) {
        User staff = getStaff();
        if (staff == null) return;
        try {
            checkInOutService.checkOut(bookingId, staff.getId(), roomCondition, notes);
            refresh();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Checked Out", "Student checked out successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    private void refresh() {
        pendingBookings = bookingService.getPendingBookings();
        allBookings = bookingService.getAllBookings();
        checkedInRecords = checkInOutService.getCurrentlyCheckedIn();
    }

    private User getStaff() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    /** Returns only APPROVED bookings not yet checked in — for the check-in table */
    public List<Booking> getPendingCheckInBookings() {
        if (allBookings == null) return java.util.Collections.emptyList();
        return allBookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.APPROVED)
            .toList();
    }

    public List<Booking> getPendingBookings() { return pendingBookings; }
    public List<Booking> getAllBookings() { return allBookings; }
    public List<CheckInOut> getCheckedInRecords() { return checkedInRecords; }
    public Booking getSelectedBooking() { return selectedBooking; }
    public void setSelectedBooking(Booking selectedBooking) { this.selectedBooking = selectedBooking; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getRoomCondition() { return roomCondition; }
    public void setRoomCondition(String roomCondition) { this.roomCondition = roomCondition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BookingStatus[] getBookingStatuses() { return BookingStatus.values(); }
}
