package com.hostel.bean;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import com.hostel.util.PasswordUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@ViewScoped
public class ProfileBean implements Serializable {

    @Inject private UserDAO userDAO;

    private String fullName;
    private String email;
    private String phone;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    @PostConstruct
    public void init() {
        User user = getLoggedInUser();
        if (user != null) {
            fullName = user.getFullName();
            email    = user.getEmail();
            phone    = user.getPhone();
        }
    }

    public void updateProfile() {
        User user = getLoggedInUser();
        if (user == null) return;
        try {
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            userDAO.update(user);

            // Refresh session user
            HttpSession session = (HttpSession)
                FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null) session.setAttribute("loggedInUser", user);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Profile Updated", "Your profile has been saved successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void changePassword() {
        User user = getLoggedInUser();
        if (user == null) return;
        if (!PasswordUtil.verify(currentPassword, user.getPassword())) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Wrong Password", "Current password is incorrect."));
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Mismatch", "New passwords do not match."));
            return;
        }
        if (newPassword.length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Too Short", "Password must be at least 6 characters."));
            return;
        }
        user.setPassword(PasswordUtil.hash(newPassword));
        userDAO.update(user);
        currentPassword = null;
        newPassword = null;
        confirmNewPassword = null;
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Password Changed", "Your password has been updated successfully."));
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String v) { this.currentPassword = v; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String v) { this.newPassword = v; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String v) { this.confirmNewPassword = v; }
}
