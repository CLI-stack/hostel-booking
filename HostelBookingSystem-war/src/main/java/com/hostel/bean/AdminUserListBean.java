package com.hostel.bean;

import com.hostel.dao.UserDAO;
import com.hostel.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AdminUserListBean implements Serializable {

    @Inject private UserDAO userDAO;

    private List<User> allUsers;

    @PostConstruct
    public void init() {
        allUsers = userDAO.findAll();
    }

    public List<User> getAllUsers() { return allUsers; }
}
