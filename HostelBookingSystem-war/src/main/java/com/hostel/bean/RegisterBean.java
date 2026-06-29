package com.hostel.bean;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import com.hostel.entity.enums.UserRole;
import com.hostel.service.AuthService;
import com.hostel.util.PasswordUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@RequestScoped
public class RegisterBean implements Serializable {

    @Inject private AuthService authService;
    @Inject private UserDAO userDAO;

    private String username;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String email;
    private String phone;
    private String matricNumber;
    private String selectedRole = "STUDENT";

    private static final String STUDENT_DOMAIN = "@student.upm.edu.my";
    private static final String STAFF_DOMAIN   = "@staff.upm.edu.my";
    private static final String ADMIN_DOMAIN   = "@admin.upm.edu.my";

    /** Public self-registration — Students only */
    public String register() {
        if (!password.equals(confirmPassword)) {
            addError(null, "Passwords do not match.");
            return null;
        }
        if (!"STUDENT".equals(selectedRole)) {
            addError(null, "Only Student accounts can be self-registered. " +
                "Admin and Staff accounts must be created by an Administrator.");
            return null;
        }
        if (!validateEmailDomain("STUDENT")) return null;
        try {
            authService.register(username, password, fullName, email, phone, matricNumber);
            addInfo("Registration Successful", "You can now log in with your credentials.");
            return "login?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            addError(null, e.getMessage());
            return null;
        }
    }

    /** Admin-only: create any role account */
    public String adminCreateUser() {
        User admin = getLoggedInUser();
        if (admin == null || admin.getRole() != UserRole.ADMIN) {
            addError(null, "Only Administrators can create Staff and Admin accounts.");
            return null;
        }
        if (!password.equals(confirmPassword)) {
            addError(null, "Passwords do not match.");
            return null;
        }
        if (!validateEmailDomain(selectedRole)) return null;
        if (userDAO.existsByUsername(username)) {
            addError(null, "Username already exists.");
            return null;
        }
        if (userDAO.existsByEmail(email)) {
            addError(null, "Email already registered.");
            return null;
        }
        UserRole role = UserRole.valueOf(selectedRole);
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setMatricNumber(matricNumber);
        user.setRole(role);
        user.setActive(true);
        userDAO.save(user);
        addInfo("User Created", fullName + " (" + role + ") created successfully.");
        username = password = confirmPassword = fullName = email = phone = matricNumber = null;
        selectedRole = "STUDENT";
        return null;
    }

    private boolean validateEmailDomain(String role) {
        if (email == null || email.isBlank()) {
            addError(null, "Email is required.");
            return false;
        }
        String required = switch (role) {
            case "STUDENT" -> STUDENT_DOMAIN;
            case "STAFF"   -> STAFF_DOMAIN;
            case "ADMIN"   -> ADMIN_DOMAIN;
            default        -> null;
        };
        if (required != null && !email.toLowerCase().endsWith(required)) {
            addError(null, "Invalid email domain for the selected role. " +
                role + " accounts must use an email ending with " + required);
            return false;
        }
        return true;
    }

    private void addError(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void addInfo(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private User getLoggedInUser() {
        HttpSession s = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return s != null ? (User) s.getAttribute("loggedInUser") : null;
    }

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String v) { this.confirmPassword = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getMatricNumber() { return matricNumber; }
    public void setMatricNumber(String v) { this.matricNumber = v; }
    public String getSelectedRole() { return selectedRole; }
    public void setSelectedRole(String v) { this.selectedRole = v; }
    public String getStudentDomain() { return STUDENT_DOMAIN; }
    public String getStaffDomain()   { return STAFF_DOMAIN; }
    public String getAdminDomain()   { return ADMIN_DOMAIN; }
}
