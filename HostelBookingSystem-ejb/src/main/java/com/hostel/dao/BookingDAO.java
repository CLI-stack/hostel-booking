package com.hostel.dao;

import com.hostel.entity.Booking;
import com.hostel.entity.enums.BookingStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class BookingDAO extends AbstractDAO<Booking, Long> {

    public BookingDAO() {
        super(Booking.class);
    }

    public List<Booking> findByStudent(Long studentId) {
        TypedQuery<Booking> q = em.createNamedQuery("Booking.findByStudent", Booking.class);
        q.setParameter("studentId", studentId);
        return q.getResultList();
    }

    public List<Booking> findActiveByStudent(Long studentId) {
        TypedQuery<Booking> q = em.createNamedQuery("Booking.findActiveByStudent", Booking.class);
        q.setParameter("studentId", studentId);
        return q.getResultList();
    }

    public boolean studentHasActiveBooking(Long studentId) {
        return !findActiveByStudent(studentId).isEmpty();
    }

    public List<Booking> findByRoom(Long roomId) {
        TypedQuery<Booking> q = em.createNamedQuery("Booking.findByRoom", Booking.class);
        q.setParameter("roomId", roomId);
        return q.getResultList();
    }

    public List<Booking> findActiveByRoom(Long roomId) {
        TypedQuery<Booking> q = em.createNamedQuery("Booking.findActiveByRoom", Booking.class);
        q.setParameter("roomId", roomId);
        return q.getResultList();
    }

    public boolean roomHasActiveBooking(Long roomId) {
        return !findActiveByRoom(roomId).isEmpty();
    }

    public List<Booking> findByStatus(BookingStatus status) {
        TypedQuery<Booking> q = em.createNamedQuery("Booking.findByStatus", Booking.class);
        q.setParameter("status", status);
        return q.getResultList();
    }

    public List<Booking> findAll() {
        return em.createNamedQuery("Booking.findAll", Booking.class).getResultList();
    }

    public long countByStatus(BookingStatus status) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(b) FROM Booking b WHERE b.status = :status", Long.class);
        q.setParameter("status", status);
        return q.getSingleResult();
    }
}
