package com.hostel.bean;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import com.hostel.entity.enums.UserRole;
import com.hostel.util.PasswordUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

/**
 * Dedicated bean for Admin User Management page.
 * Handles creating Staff/Admin/Student accounts from the admin panel.
 * Keeps admin user creation completely separate from public self-registration.
 */
@Named
@ViewScoped
public class AdminCreateUserBean implements Serializable {

    @Inject private UserDAO userDAO;

    // Form fields
    private String selectedRole = "STAFF";
    private String fullName;
    private String email;
    private String phone;
    private String username;
    private String matricNumber;
    private String password;
    private String confirmPassword;

    // User list
    private List<User> allUsers;

    private static final String STUDENT_DOMAIN = "@student.upm.edu.my";
    private static final String STAFF_DOMAIN   = "@staff.upm.edu.my";
    private static final String ADMIN_DOMAIN   = "@admin.upm.edu.my";

    @PostConstruct
    public void init() {
        refreshUsers();
    }

    public void createUser() {
        User admin = getLoggedInUser();
        if (admin == null || admin.getRole() != UserRole.ADMIN) {
            addError("Only Administrators can create user accounts.");
            return;
        }
        if (password == null || !password.equals(confirmPassword)) {
            addError("Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            addError("Password must be at least 6 characters.");
            return;
        }
        if (!validateEmailDomain()) return;
        if (userDAO.existsByUsername(username)) {
            addError("Username '" + username + "' already exists.");
            return;
        }
        if (userDAO.existsByEmail(email)) {
            addError("Email '" + email + "' is already registered.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setMatricNumber(matricNumber);
        user.setRole(UserRole.valueOf(selectedRole));
        user.setActive(true);
        userDAO.save(user);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "User Created Successfully",
                fullName + " (" + selectedRole + ") has been created."));

        resetForm();
        refreshUsers();
    }

    private boolean validateEmailDomain() {
        if (email == null || email.isBlank()) {
            addError("Email is required.");
            return false;
        }
        String required = switch (selectedRole) {
            case "STUDENT" -> STUDENT_DOMAIN;
            case "STAFF"   -> STAFF_DOMAIN;
            case "ADMIN"   -> ADMIN_DOMAIN;
            default        -> null;
        };
        if (required != null && !email.toLowerCase().endsWith(required)) {
            addError("Invalid email domain for " + selectedRole +
                " role. Email must end with " + required);
            return false;
        }
        return true;
    }

    private void resetForm() {
        fullName = email = phone = username = matricNumber = password = confirmPassword = null;
        selectedRole = "STAFF";
    }

    public void refreshUsers() {
        allUsers = userDAO.findAll();
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private User getLoggedInUser() {
        HttpSession s = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return s != null ? (User) s.getAttribute("loggedInUser") : null;
    }

    // Getters & setters
    public String getSelectedRole()   { return selectedRole; }
    public void setSelectedRole(String v) { this.selectedRole = v; }
    public String getFullName()   { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail()   { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone()   { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getUsername()   { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getMatricNumber()   { return matricNumber; }
    public void setMatricNumber(String v) { this.matricNumber = v; }
    public String getPassword()   { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getConfirmPassword()   { return confirmPassword; }
    public void setConfirmPassword(String v) { this.confirmPassword = v; }
    public List<User> getAllUsers()   { return allUsers; }
    public String getStudentDomain() { return STUDENT_DOMAIN; }
    public String getStaffDomain()   { return STAFF_DOMAIN; }
    public String getAdminDomain()   { return ADMIN_DOMAIN; }
}
