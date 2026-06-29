package com.hostel.util;

import com.hostel.entity.User;
import com.hostel.entity.enums.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession         session  = request.getSession(false);

        String requestURI = request.getRequestURI();

        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }

        // Role-based access control
        if (requestURI.contains("/student/") && loggedInUser.getRole() != UserRole.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }
        if (requestURI.contains("/staff/") && loggedInUser.getRole() != UserRole.STAFF
                && loggedInUser.getRole() != UserRole.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }
        if (requestURI.contains("/admin/") && loggedInUser.getRole() != UserRole.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/login.xhtml");
            return;
        }

        chain.doFilter(req, res);
    }
}
