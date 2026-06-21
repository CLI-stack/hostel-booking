package com.hostel.bean;

import com.hostel.entity.Booking;
import com.hostel.entity.User;
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

@Named
@ViewScoped
public class BookingBean implements Serializable {

    @Inject private BookingService bookingService;
    @Inject private RegistrationPeriodService periodService;

    private List<Booking> myBookings;
    private Long selectedRoomId;
    private Booking selectedBooking;
    private boolean registrationOpen;

    @PostConstruct
    public void init() {
        User user = getLoggedInUser();
        if (user != null) {
            myBookings = bookingService.getStudentBookings(user.getId());
        }
        registrationOpen = periodService.isRegistrationOpen();
    }

    public void submitBooking() {
        User user = getLoggedInUser();
        if (user == null) return;

        try {
            Booking booking = bookingService.submitBooking(user.getId(), selectedRoomId);
            myBookings = bookingService.getStudentBookings(user.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Booking Submitted",
                    "Your booking for Room " + booking.getRoom().getRoomNumber() +
                    " is pending approval."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Booking Failed", e.getMessage()));
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

    public boolean hasActiveBooking() {
        User user = getLoggedInUser();
        if (user == null) return false;
        return myBookings != null && myBookings.stream()
            .anyMatch(b -> b.getStatus() != com.hostel.entity.enums.BookingStatus.REJECTED
                        && b.getStatus() != com.hostel.entity.enums.BookingStatus.CANCELLED
                        && b.getStatus() != com.hostel.entity.enums.BookingStatus.CHECKED_OUT);
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Booking> getMyBookings() { return myBookings; }
    public Long getSelectedRoomId() { return selectedRoomId; }
    public void setSelectedRoomId(Long selectedRoomId) { this.selectedRoomId = selectedRoomId; }
    public Booking getSelectedBooking() { return selectedBooking; }
    public void setSelectedBooking(Booking selectedBooking) { this.selectedBooking = selectedBooking; }
    public boolean isRegistrationOpen() { return registrationOpen; }
}
