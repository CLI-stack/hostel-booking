package com.hostel.service;

import com.hostel.dao.BookingDAO;
import com.hostel.dao.ComplaintDAO;
import com.hostel.dao.UserDAO;
import com.hostel.entity.Booking;
import com.hostel.entity.Complaint;
import com.hostel.entity.User;
import com.hostel.entity.enums.ComplaintStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ComplaintService {

    @Inject private ComplaintDAO complaintDAO;
    @Inject private UserDAO userDAO;
    @Inject private BookingDAO bookingDAO;
    @Inject private NotificationService notificationService;

    public Complaint submitComplaint(Long studentId, Long bookingId, String subject, String description) {
        User student = userDAO.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setStatus(ComplaintStatus.OPEN);

        if (bookingId != null) {
            bookingDAO.findById(bookingId).ifPresent(complaint::setBooking);
        }

        return complaintDAO.save(complaint);
    }

    public Complaint respondToComplaint(Long complaintId, Long adminId, String response, ComplaintStatus newStatus) {
        Complaint complaint = complaintDAO.findById(complaintId)
            .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        User admin = userDAO.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        complaint.setAdminResponse(response);
        complaint.setStatus(newStatus);

        if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.CLOSED) {
            complaint.setResolvedBy(admin);
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint updated = complaintDAO.update(complaint);

        notificationService.sendToUser(complaint.getStudent(),
            "Complaint Update",
            "Your complaint \"" + complaint.getSubject() + "\" status: " + newStatus.name() +
            ". Response: " + response);

        return updated;
    }

    public List<Complaint> getStudentComplaints(Long studentId) {
        return complaintDAO.findByStudent(studentId);
    }

    public List<Complaint> getAllComplaints() {
        return complaintDAO.findAll();
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintDAO.findByStatus(status);
    }

    public long countByStatus(ComplaintStatus status) {
        return complaintDAO.countByStatus(status);
    }
}
