package com.hostel.bean;

import com.hostel.entity.User;
import com.hostel.service.NotificationService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * Session-scoped bean providing the currently logged-in user to all pages.
 */
@Named
@SessionScoped
public class SessionBean implements Serializable {

    @Inject private NotificationService notificationService;

    public User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute("loggedInUser");
    }

    public long getUnreadNotificationCount() {
        User user = getLoggedInUser();
        if (user == null) return 0;
        return notificationService.countUnread(user.getId());
    }

    public boolean isLoggedIn() {
        return getLoggedInUser() != null;
    }

    public boolean isStudent() {
        User u = getLoggedInUser();
        return u != null && u.getRole().name().equals("STUDENT");
    }

    public boolean isStaff() {
        User u = getLoggedInUser();
        return u != null && u.getRole().name().equals("STAFF");
    }

    public boolean isAdmin() {
        User u = getLoggedInUser();
        return u != null && u.getRole().name().equals("ADMIN");
    }
}
