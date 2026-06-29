package com.hostel.entity;

import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_periods")
@NamedQueries({
    @NamedQuery(name = "RegistrationPeriod.findActive",
        query = "SELECT r FROM RegistrationPeriod r WHERE r.active = true AND r.startDate <= :today AND r.endDate >= :today"),
    @NamedQuery(name = "RegistrationPeriod.findAll",
        query = "SELECT r FROM RegistrationPeriod r ORDER BY r.startDate DESC")
})
public class RegistrationPeriod implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Period name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "semester", length = 20)
    private String semester;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public RegistrationPeriod() {}

    public boolean isCurrentlyOpen() {
        LocalDate today = LocalDate.now();
        return active && !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
