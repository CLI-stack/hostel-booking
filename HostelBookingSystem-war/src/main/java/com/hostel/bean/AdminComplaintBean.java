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
public class AdminComplaintBean implements Serializable {

    @Inject private ComplaintService complaintService;

    private List<Complaint> complaints;
    private Complaint selectedComplaint;
    private String adminResponse;
    private String newStatus;

    @PostConstruct
    public void init() {
        complaints = complaintService.getAllComplaints();
    }

    public void respond() {
        User admin = getAdmin();
        if (admin == null || selectedComplaint == null) return;
        try {
            ComplaintStatus status = ComplaintStatus.valueOf(newStatus);
            complaintService.respondToComplaint(selectedComplaint.getId(), admin.getId(), adminResponse, status);
            complaints = complaintService.getAllComplaints();
            adminResponse = null;
            newStatus = null;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Response Sent", "Complaint updated."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public long getOpenCount() { return complaintService.countByStatus(ComplaintStatus.OPEN); }
    public long getInProgressCount() { return complaintService.countByStatus(ComplaintStatus.IN_PROGRESS); }
    public long getResolvedCount() { return complaintService.countByStatus(ComplaintStatus.RESOLVED); }

    private User getAdmin() {
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session != null ? (User) session.getAttribute("loggedInUser") : null;
    }

    public List<Complaint> getComplaints() { return complaints; }
    public Complaint getSelectedComplaint() { return selectedComplaint; }
    public void setSelectedComplaint(Complaint c) { this.selectedComplaint = c; }
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String r) { this.adminResponse = r; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String s) { this.newStatus = s; }
    public ComplaintStatus[] getComplaintStatuses() { return ComplaintStatus.values(); }
}
