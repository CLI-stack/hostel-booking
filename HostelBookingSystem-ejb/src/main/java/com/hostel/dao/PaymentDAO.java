package com.hostel.dao;

import com.hostel.entity.Payment;
import com.hostel.entity.enums.PaymentStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Stateless
public class PaymentDAO extends AbstractDAO<Payment, Long> {

    public PaymentDAO() {
        super(Payment.class);
    }

    public Optional<Payment> findByBooking(Long bookingId) {
        try {
            TypedQuery<Payment> q = em.createNamedQuery("Payment.findByBooking", Payment.class);
            q.setParameter("bookingId", bookingId);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        TypedQuery<Payment> q = em.createNamedQuery("Payment.findByStatus", Payment.class);
        q.setParameter("status", status);
        return q.getResultList();
    }

    public List<Payment> findAll() {
        return em.createNamedQuery("Payment.findAll", Payment.class).getResultList();
    }

    public BigDecimal getTotalRevenue() {
        TypedQuery<BigDecimal> q = em.createNamedQuery("Payment.totalRevenue", BigDecimal.class);
        BigDecimal result = q.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
}
