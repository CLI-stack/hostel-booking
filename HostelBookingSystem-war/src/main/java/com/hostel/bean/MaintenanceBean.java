package com.hostel.bean;

import com.hostel.entity.MaintenanceRequest;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.entity.enums.MaintenanceStatus;
import com.hostel.service.MaintenanceService;
import com.hostel.service.RoomService;
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
public class MaintenanceBean implements Serializable {

    @Inject private MaintenanceService maintenanceService;
    @Inject private RoomService roomService;

    private List<MaintenanceRequest> allRequests;
    private List<Room> allRooms;
    private MaintenanceRequest selectedRequest;
    private Long selectedRoomId;
    private String category;
    private String issueDescription;
    private String staffNotes;
    private String newStatus;

    @PostConstruct
    public void init() {
        allRequests = maintenanceService.getAllRequests();
        allRooms = roomService.getAllRooms();
    }

    public void logRequest() {
        User user = getLoggedInUser();
        if (user == null) return;
        try {
            maintenanceService.logRequest(selectedRoomId, user.getId(), category, issueDescription);
            selectedRoomId = null;
            category = null;
            issueDescription = null;
            allRequests = maintenanceService.getAllRequests();
            allRooms = roomService.getAllRooms();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Request Logged", "Maintenance request submitted successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void updateStatus(Long requestId) {
        User user = getLoggedInUser();
        if (user == null) return;
        try {
            MaintenanceStatus status = MaintenanceStatus.valueOf(newStatus);
            maintenanceService.updateStatus(requestId, user.getId(), status, staffNotes);
            allRequests = maintenanceService.getAllRequests();
            allRooms = roomService.getAllRooms();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated", "Maintenance status updated."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    private User getLoggedInUser() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<MaintenanceRequest> getAllRequests() { return allRequests; }
    public List<Room> getAllRooms() { return allRooms; }
    public MaintenanceRequest getSelectedRequest() { return selectedRequest; }
    public void setSelectedRequest(MaintenanceRequest r) { this.selectedRequest = r; }
    public Long getSelectedRoomId() { return selectedRoomId; }
    public void setSelectedRoomId(Long id) { this.selectedRoomId = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String d) { this.issueDescription = d; }
    public String getStaffNotes() { return staffNotes; }
    public void setStaffNotes(String n) { this.staffNotes = n; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String s) { this.newStatus = s; }
    public MaintenanceStatus[] getMaintenanceStatuses() { return MaintenanceStatus.values(); }
}
