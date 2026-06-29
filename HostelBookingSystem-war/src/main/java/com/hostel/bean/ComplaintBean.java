package com.hostel.bean;

import com.hostel.entity.Complaint;
import com.hostel.entity.User;
import com.hostel.entity.enums.ComplaintStatus;
import com.hostel.service.ComplaintService;
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
public class ComplaintBean implements Serializable {

    @Inject private ComplaintService complaintService;

    private List<Complaint> myComplaints;
    private String subject;
    private String description;
    private Long selectedBookingId;
    private Complaint selectedComplaint;
    private String adminResponse;
    private String newStatus;

    @PostConstruct
    public void init() {
        loadMyComplaints();
    }

    private void loadMyComplaints() {
        User user = getLoggedInUser();
        if (user != null) {
            myComplaints = complaintService.getStudentComplaints(user.getId());
        }
    }

    public void submitComplaint() {
        User user = getLoggedInUser();
        if (user == null) return;
        try {
            complaintService.submitComplaint(user.getId(), selectedBookingId, subject, description);
            subject = null;
            description = null;
            selectedBookingId = null;
            loadMyComplaints();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Complaint Submitted", "Your complaint has been submitted successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void respondToComplaint() {
        User user = getLoggedInUser();
        if (user == null || selectedComplaint == null) return;
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(newStatus);
            complaintService.respondToComplaint(selectedComplaint.getId(), user.getId(), adminResponse, status);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Response Saved", "Complaint updated."));
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

    public List<Complaint> getMyComplaints() { return myComplaints; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getSelectedBookingId() { return selectedBookingId; }
    public void setSelectedBookingId(Long selectedBookingId) { this.selectedBookingId = selectedBookingId; }
    public Complaint getSelectedComplaint() { return selectedComplaint; }
    public void setSelectedComplaint(Complaint selectedComplaint) { this.selectedComplaint = selectedComplaint; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public ComplaintStatus[] getComplaintStatuses() { return ComplaintStatus.values(); }
}
