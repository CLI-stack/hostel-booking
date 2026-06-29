package com.hostel.service;

import com.hostel.dao.NotificationDAO;
import com.hostel.entity.Notification;
import com.hostel.entity.User;
import com.hostel.entity.enums.NotificationType;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class NotificationService {

    @Inject
    private NotificationDAO notificationDAO;

    public void sendToUser(User user, String title, String message) {
        Notification notification = new Notification(user, title, message, NotificationType.PUSH);
        notificationDAO.save(notification);
    }

    public void sendEmailNotification(User user, String title, String message) {
        Notification notification = new Notification(user, title, message, NotificationType.EMAIL);
        notificationDAO.save(notification);
        // In production: integrate real email service (JavaMail / SMTP)
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationDAO.findByUser(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationDAO.findUnreadByUser(userId);
    }

    public long countUnread(Long userId) {
        return notificationDAO.countUnreadByUser(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationDAO.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationDAO.update(n);
        });
    }

    public void markAllAsRead(Long userId) {
        notificationDAO.findUnreadByUser(userId).forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationDAO.update(n);
        });
    }
}
