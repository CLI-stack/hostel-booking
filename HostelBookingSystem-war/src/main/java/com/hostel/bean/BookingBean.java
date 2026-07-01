package com.hostel.bean;

import com.hostel.entity.Booking;
import com.hostel.entity.User;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.service.BookingService;
import com.hostel.service.RegistrationPeriodService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named
@ViewScoped
public class BookingBean implements Serializable {
  

    @Inject private BookingService bookingService;
    @Inject private RegistrationPeriodService periodService;

    private List<Booking> myBookings;
    private Long selectedRoomId;
    private Booking selectedBooking;
    private boolean registrationOpen;

    // Callback params for XHTML popup logic
    private String lastBookedRoomNumber;

    @PostConstruct
    public void init() {
        User user = getLoggedInUser();
        if (user != null) {
            myBookings = bookingService.getStudentBookings(user.getId());
        }
        registrationOpen = periodService.isRegistrationOpen();
    }
  

    public void submitBooking() {
           System.out.println("###### SUBMITBOOKING DEBUG VERSION 99 RUNNING ######");
        User user = getLoggedInUser();
        if (user == null) return;

        // Refresh booking status before checking — avoid stale @ViewScoped data
        // (e.g. staff may have checked the student in since this view was created)
        myBookings = bookingService.getStudentBookings(user.getId());

        // Check for existing APPROVED or CHECKED_IN booking before calling the service
        boolean hasActive = myBookings != null && myBookings.stream()
            .anyMatch(b -> b.getStatus() == BookingStatus.APPROVED
                        || b.getStatus() == BookingStatus.CHECKED_IN);

        if (hasActive) {
            // Tell the UI to show the "already has active/checked-in booking" popup
            PrimeFaces.current().ajax().addCallbackParam("bookingResult", "ALREADY_APPROVED");
            return;
        }

        try {
            Booking booking = bookingService.submitBooking(user.getId(), selectedRoomId);
            lastBookedRoomNumber = booking.getRoom().getRoomNumber();
            myBookings = bookingService.getStudentBookings(user.getId());
            // Single success callback — XHTML decides which popup to show
            PrimeFaces.current().ajax().addCallbackParam("bookingResult", "SUCCESS");
            PrimeFaces.current().ajax().addCallbackParam("roomNumber", lastBookedRoomNumber);
        } catch (Exception e) {
            // Walk the full cause chain to find the real business exception message,
            // since EJB containers wrap unchecked exceptions in EJBException (and
            // sometimes other wrappers), hiding the original message.
            Throwable t = e;
            String msg = null;
            while (t != null) {
                if (t.getMessage() != null) {
                    msg = t.getMessage();
                }
                t = t.getCause();
            }
            handleBookingError(msg);
        }
    }

    private void handleBookingError(String msg) {
        if (msg != null && msg.contains("active booking")) {
            PrimeFaces.current().ajax().addCallbackParam("bookingResult", "ALREADY_ACTIVE");
        } else if (msg != null && msg.contains("not open")) {
            PrimeFaces.current().ajax().addCallbackParam("bookingResult", "PERIOD_CLOSED");
        } else {
            PrimeFaces.current().ajax().addCallbackParam("bookingResult", "ERROR");
            PrimeFaces.current().ajax().addCallbackParam("errorMsg",
                msg != null ? msg : "An unexpected error occurred.");
        }
    }

    public void cancelBooking(Long bookingId) {
        User user = getLoggedInUser();
        if (user == null) return;
        try {
            bookingService.cancelBooking(bookingId, user.getId());
            myBookings = bookingService.getStudentBookings(user.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Booking Cancelled", "Your booking has been cancelled."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public boolean hasApprovedBooking() {
        return myBookings != null && myBookings.stream()
            .anyMatch(b -> b.getStatus() == BookingStatus.APPROVED
                        || b.getStatus() == BookingStatus.CHECKED_IN);
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Booking> getMyBookings() { return myBookings; }
    public Long getSelectedRoomId() { return selectedRoomId; }
    public void setSelectedRoomId(Long v) { this.selectedRoomId = v; }
    public Booking getSelectedBooking() { return selectedBooking; }
    public void setSelectedBooking(Booking v) { this.selectedBooking = v; }
    public boolean isRegistrationOpen() { return registrationOpen; }
    public String getLastBookedRoomNumber() { return lastBookedRoomNumber; }
}