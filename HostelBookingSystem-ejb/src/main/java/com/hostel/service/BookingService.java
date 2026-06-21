package com.hostel.service;

import com.hostel.dao.BookingDAO;
import com.hostel.dao.RegistrationPeriodDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.UserDAO;
import com.hostel.entity.*;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.RoomStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
public class BookingService {

    @Inject private BookingDAO bookingDAO;
    @Inject private RoomDAO roomDAO;
    @Inject private UserDAO userDAO;
    @Inject private RegistrationPeriodDAO periodDAO;
    @Inject private NotificationService notificationService;

    public Booking submitBooking(Long studentId, Long roomId) {
        // Constraint: only during active registration period
        RegistrationPeriod period = periodDAO.findActivePeriod()
            .orElseThrow(() -> new IllegalStateException("Booking is not open. No active registration period."));

        // Constraint: student can only have one active booking
        if (bookingDAO.studentHasActiveBooking(studentId)) {
            throw new IllegalStateException("You already have an active booking. Cancel it before making a new one.");
        }

        User student = userDAO.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Room room = roomDAO.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // Constraint: prevent duplicate room bookings
        if (bookingDAO.roomHasActiveBooking(roomId)) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is already booked.");
        }

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is not available.");
        }

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setRoom(room);
        booking.setRegistrationPeriod(period);
        booking.setStatus(BookingStatus.PENDING);
        booking.setStartDate(period.getStartDate());
        booking.setEndDate(period.getEndDate());

        // Reserve the room
        room.setStatus(RoomStatus.RESERVED);
        roomDAO.update(room);

        Booking saved = bookingDAO.save(booking);

        notificationService.sendToUser(student,
            "Booking Submitted",
            "Your booking for Room " + room.getRoomNumber() + " has been submitted. Awaiting approval.");

        return saved;
    }

    public Booking approveBooking(Long bookingId, Long staffId) {
        Booking booking = findById(bookingId);
        User staff = userDAO.findById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        booking.setStatus(BookingStatus.APPROVED);
        booking.setProcessedBy(staff);
        booking.setProcessedAt(LocalDateTime.now());

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomDAO.update(room);

        Booking updated = bookingDAO.update(booking);

        notificationService.sendToUser(booking.getStudent(),
            "Booking Approved",
            "Your booking for Room " + room.getRoomNumber() + " has been approved. Please proceed to payment.");

        return updated;
    }

    public Booking rejectBooking(Long bookingId, Long staffId, String reason) {
        Booking booking = findById(bookingId);
        User staff = userDAO.findById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectionReason(reason);
        booking.setProcessedBy(staff);
        booking.setProcessedAt(LocalDateTime.now());

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomDAO.update(room);

        Booking updated = bookingDAO.update(booking);

        notificationService.sendToUser(booking.getStudent(),
            "Booking Rejected",
            "Your booking for Room " + room.getRoomNumber() + " was rejected. Reason: " + reason);

        return updated;
    }

    public Booking cancelBooking(Long bookingId, Long studentId) {
        Booking booking = findById(bookingId);

        if (!booking.getStudent().getId().equals(studentId)) {
            throw new SecurityException("You can only cancel your own bookings.");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel a booking after check-in.");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Room room = booking.getRoom();
        if (room.getStatus() == RoomStatus.RESERVED || room.getStatus() == RoomStatus.OCCUPIED) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
        }

        return bookingDAO.update(booking);
    }

    public List<Booking> getStudentBookings(Long studentId) {
        return bookingDAO.findByStudent(studentId);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingDAO.findByStatus(status);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public List<Booking> getPendingBookings() {
        return bookingDAO.findByStatus(BookingStatus.PENDING);
    }

    public long countByStatus(BookingStatus status) {
        return bookingDAO.countByStatus(status);
    }

    public long countTotal() {
        return bookingDAO.count();
    }

    private Booking findById(Long bookingId) {
        return bookingDAO.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }
}
