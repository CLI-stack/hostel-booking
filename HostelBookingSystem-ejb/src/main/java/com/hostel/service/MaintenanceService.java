package com.hostel.service;

import com.hostel.dao.MaintenanceDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.UserDAO;
import com.hostel.entity.MaintenanceRequest;
import com.hostel.entity.Room;
import com.hostel.entity.User;
import com.hostel.entity.enums.MaintenanceStatus;
import com.hostel.entity.enums.RoomStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class MaintenanceService {

    @Inject private MaintenanceDAO maintenanceDAO;
    @Inject private RoomDAO roomDAO;
    @Inject private UserDAO userDAO;

    public MaintenanceRequest logRequest(Long roomId, Long reportedById, String category, String issueDescription) {
        Room room = roomDAO.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        MaintenanceRequest request = new MaintenanceRequest();
        request.setRoom(room);
        request.setIssueDescription(issueDescription);
        request.setCategory(category);
        request.setStatus(MaintenanceStatus.PENDING);

        if (reportedById != null) {
            userDAO.findById(reportedById).ifPresent(request::setReportedBy);
        }

        // Room goes into maintenance when request is created
        room.setStatus(RoomStatus.MAINTENANCE);
        roomDAO.update(room);

        return maintenanceDAO.save(request);
    }

    public MaintenanceRequest updateStatus(Long requestId, Long staffId, MaintenanceStatus newStatus, String staffNotes) {
        MaintenanceRequest request = maintenanceDAO.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Maintenance request not found"));

        request.setStatus(newStatus);
        request.setStaffNotes(staffNotes);

        if (staffId != null) {
            userDAO.findById(staffId).ifPresent(request::setAssignedTo);
        }

        if (newStatus == MaintenanceStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
            // Release room back to available
            Room room = request.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);
        }

        return maintenanceDAO.update(request);
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceDAO.findAll();
    }

    public List<MaintenanceRequest> getRequestsByStatus(MaintenanceStatus status) {
        return maintenanceDAO.findByStatus(status);
    }

    public List<MaintenanceRequest> getRequestsByRoom(Long roomId) {
        return maintenanceDAO.findByRoom(roomId);
    }

    public long countByStatus(MaintenanceStatus status) {
        return maintenanceDAO.countByStatus(status);
    }
}
