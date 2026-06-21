package com.hostel.service;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import com.hostel.entity.enums.UserRole;
import com.hostel.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Optional;

@Stateless
public class AuthService {

    @Inject
    private UserDAO userDAO;

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isActive() && PasswordUtil.verify(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public User register(String username, String password, String fullName,
                         String email, String phone, String matricNumber) {
        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setMatricNumber(matricNumber);
        user.setRole(UserRole.STUDENT);
        user.setActive(true);
        return userDAO.save(user);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!PasswordUtil.verify(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(PasswordUtil.hash(newPassword));
        userDAO.update(user);
    }

    // Java 18 switch expression
    public String getRedirectForRole(UserRole role) {
        return switch (role) {
            case STUDENT -> "student-dashboard";
            case STAFF   -> "staff-dashboard";
            case ADMIN   -> "admin-dashboard";
        };
    }
}
