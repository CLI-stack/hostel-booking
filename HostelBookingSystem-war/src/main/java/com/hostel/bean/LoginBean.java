package com.hostel.bean;

import com.hostel.entity.User;
import com.hostel.service.AuthService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

@Named
@RequestScoped
public class LoginBean implements Serializable {

    @Inject private AuthService authService;

    private String username;
    private String password;

    public String login() {
        Optional<User> userOpt = authService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            HttpSession session = (HttpSession)
                FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            session.setAttribute("loggedInUser", user);
            return authService.getRedirectForRole(user.getRole());
        }
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Login Failed", "Invalid username or password."));
        return null;
    }

    public String logout() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) session.invalidate();
        return "logout";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
