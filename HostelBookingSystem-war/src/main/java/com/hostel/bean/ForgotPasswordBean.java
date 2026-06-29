package com.hostel.bean;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import com.hostel.util.PasswordUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Optional;

@Named
@RequestScoped
public class ForgotPasswordBean implements Serializable {

    @Inject private UserDAO userDAO;

    private String email;
    private String newPassword;
    private String confirmPassword;
    private boolean emailVerified = false;
    private Long verifiedUserId;

    public void verifyEmail() {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            emailVerified = true;
            verifiedUserId = userOpt.get().getId();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Email Verified",
                    "Please enter your new password below."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Email Not Found",
                    "No account is registered with this email address."));
        }
    }

    public String resetPassword() {
        if (!emailVerified || verifiedUserId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Please verify your email first."));
            return null;
        }
        if (!newPassword.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Password Mismatch", "Passwords do not match."));
            return null;
        }
        if (newPassword.length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Too Short", "Password must be at least 6 characters."));
            return null;
        }
        userDAO.findById(verifiedUserId).ifPresent(user -> {
            user.setPassword(PasswordUtil.hash(newPassword));
            userDAO.update(user);
        });
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Password Reset Successful",
                "Your password has been updated. Please log in with your new password."));
        return "login?faces-redirect=true";
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public boolean isEmailVerified() { return emailVerified; }
}
