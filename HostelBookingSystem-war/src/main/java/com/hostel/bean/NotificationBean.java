package com.hostel.bean;

import com.hostel.entity.Notification;
import com.hostel.entity.User;
import com.hostel.service.NotificationService;
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
public class NotificationBean implements Serializable {

    @Inject private NotificationService notificationService;

    private List<Notification> notifications;
    private long unreadCount;

    @PostConstruct
    public void init() {
        loadNotifications();
    }

    private void loadNotifications() {
        User user = getLoggedInUser();
        if (user != null) {
            notifications = notificationService.getUserNotifications(user.getId());
            unreadCount = notificationService.countUnread(user.getId());
        }
    }

    public void markAllRead() {
        User user = getLoggedInUser();
        if (user != null) {
            notificationService.markAllAsRead(user.getId());
            loadNotifications();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "All marked as read", null));
        }
    }

    public void markRead(Long notificationId) {
        notificationService.markAsRead(notificationId);
        loadNotifications();
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Notification> getNotifications() { return notifications; }
    public long getUnreadCount() { return unreadCount; }
}
