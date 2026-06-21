package com.hostel.service;

import com.hostel.dao.BookingDAO;
import com.hostel.dao.UserDAO;
import com.hostel.entity.Booking;
import com.hostel.entity.CheckInOut;
import com.hostel.entity.User;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.RoomStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CheckInOutService {

    @PersistenceContext(unitName = "hostelPU")
    private EntityManager em;

    @Inject private BookingDAO bookingDAO;
    @Inject private UserDAO userDAO;
    @Inject private NotificationService notificationService;
    @Inject private RoomService roomService;

    public CheckInOut checkIn(Long bookingId, Long staffId, String roomCondition, String notes) {
        Booking booking = bookingDAO.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Only approved bookings can be checked in.");
        }

        User staff = userDAO.findById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        CheckInOut record = new CheckInOut();
        record.setBooking(booking);
        record.setCheckInDate(LocalDateTime.now());
        record.setCheckInBy(staff);
        record.setRoomConditionOnCheckin(roomCondition);
        record.setCheckInNotes(notes);

        booking.setStatus(BookingStatus.CHECKED_IN);
        bookingDAO.update(booking);

        em.persist(record);
        em.flush();

        notificationService.sendToUser(booking.getStudent(),
            "Checked In",
            "You have successfully checked into Room " + booking.getRoom().getRoomNumber() + ".");

        return record;
    }

    public CheckInOut checkOut(Long bookingId, Long staffId, String roomCondition, String notes) {
        Booking booking = bookingDAO.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Only checked-in bookings can be checked out.");
        }

        User staff = userDAO.findById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        CheckInOut record = em.createQuery(
            "SELECT c FROM CheckInOut c WHERE c.booking.id = :bookingId", CheckInOut.class)
            .setParameter("bookingId", bookingId)
            .getSingleResult();

        record.setCheckOutDate(LocalDateTime.now());
        record.setCheckOutBy(staff);
        record.setRoomConditionOnCheckout(roomCondition);
        record.setCheckOutNotes(notes);

        booking.setStatus(BookingStatus.CHECKED_OUT);
        bookingDAO.update(booking);

        // Room is now available again
        roomService.updateRoomStatus(booking.getRoom().getId(), RoomStatus.AVAILABLE);

        em.merge(record);
        em.flush();

        notificationService.sendToUser(booking.getStudent(),
            "Checked Out",
            "You have checked out from Room " + booking.getRoom().getRoomNumber() + ". Thank you!");

        return record;
    }

    public List<CheckInOut> getAllRecords() {
        return em.createNamedQuery("CheckInOut.findAll", CheckInOut.class).getResultList();
    }

    public List<CheckInOut> getCurrentlyCheckedIn() {
        return em.createNamedQuery("CheckInOut.findCurrentlyCheckedIn", CheckInOut.class).getResultList();
    }
}
