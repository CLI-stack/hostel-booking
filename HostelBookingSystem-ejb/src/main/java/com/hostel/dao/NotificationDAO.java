package com.hostel.dao;

import com.hostel.entity.Notification;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class NotificationDAO extends AbstractDAO<Notification, Long> {

    public NotificationDAO() {
        super(Notification.class);
    }

    public List<Notification> findByUser(Long userId) {
        TypedQuery<Notification> q = em.createNamedQuery("Notification.findByUser", Notification.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    public List<Notification> findUnreadByUser(Long userId) {
        TypedQuery<Notification> q = em.createNamedQuery("Notification.findUnreadByUser", Notification.class);
        q.setParameter("userId", userId);
        return q.getResultList();
    }

    public long countUnreadByUser(Long userId) {
        TypedQuery<Long> q = em.createNamedQuery("Notification.countUnreadByUser", Long.class);
        q.setParameter("userId", userId);
        return q.getSingleResult();
    }
}
