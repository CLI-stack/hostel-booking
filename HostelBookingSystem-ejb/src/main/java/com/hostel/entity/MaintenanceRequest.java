package com.hostel.entity;

import com.hostel.entity.enums.MaintenanceStatus;
import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_requests")
@NamedQueries({
    @NamedQuery(name = "MaintenanceRequest.findAll",
        query = "SELECT m FROM MaintenanceRequest m ORDER BY m.createdAt DESC"),
    @NamedQuery(name = "MaintenanceRequest.findByStatus",
        query = "SELECT m FROM MaintenanceRequest m WHERE m.status = :status ORDER BY m.createdAt DESC"),
    @NamedQuery(name = "MaintenanceRequest.findByRoom",
        query = "SELECT m FROM MaintenanceRequest m WHERE m.room.id = :roomId ORDER BY m.createdAt DESC"),
    @NamedQuery(name = "MaintenanceRequest.countByStatus",
        query = "SELECT m.status, COUNT(m) FROM MaintenanceRequest m GROUP BY m.status")
})
public class MaintenanceRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", allocationSize = 1)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @NotBlank(message = "Issue description is required")
    @Column(name = "issue_description", nullable = false, length = 1000)
    private String issueDescription;

    @Column(name = "category", length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private MaintenanceStatus status = MaintenanceStatus.PENDING;

    @Column(name = "staff_notes", length = 1000)
    private String staffNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MaintenanceRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public MaintenanceStatus getStatus() { return status; }
    public void setStatus(MaintenanceStatus status) { this.status = status; }

    public String getStaffNotes() { return staffNotes; }
    public void setStaffNotes(String staffNotes) { this.staffNotes = staffNotes; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
