package com.hostel.entity;

import com.hostel.entity.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@NamedQueries({
    @NamedQuery(name = "Booking.findByStudent",
        query = "SELECT b FROM Booking b WHERE b.student.id = :studentId ORDER BY b.bookingDate DESC"),
    @NamedQuery(name = "Booking.findActiveByStudent",
        query = "SELECT b FROM Booking b WHERE b.student.id = :studentId " +
                "AND b.status NOT IN (com.hostel.entity.enums.BookingStatus.REJECTED, " +
                "com.hostel.entity.enums.BookingStatus.CANCELLED, " +
                "com.hostel.entity.enums.BookingStatus.CHECKED_OUT)"),
    @NamedQuery(name = "Booking.findByRoom",
        query = "SELECT b FROM Booking b WHERE b.room.id = :roomId ORDER BY b.bookingDate DESC"),
    @NamedQuery(name = "Booking.findByStatus",
        query = "SELECT b FROM Booking b WHERE b.status = :status ORDER BY b.bookingDate DESC"),
    @NamedQuery(name = "Booking.findAll",
        query = "SELECT b FROM Booking b ORDER BY b.bookingDate DESC"),
    @NamedQuery(name = "Booking.countByStatus",
        query = "SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status"),
    @NamedQuery(name = "Booking.findActiveByRoom",
        query = "SELECT b FROM Booking b WHERE b.room.id = :roomId " +
                "AND b.status IN (com.hostel.entity.enums.BookingStatus.APPROVED, " +
                "com.hostel.entity.enums.BookingStatus.CHECKED_IN)")
})
public class Booking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", allocationSize = 1)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull(message = "Registration period is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_period_id", nullable = false)
    private RegistrationPeriod registrationPeriod;

    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDateTime bookingDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CheckInOut checkInOut;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Complaint complaint;

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
    }

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public RegistrationPeriod getRegistrationPeriod() { return registrationPeriod; }
    public void setRegistrationPeriod(RegistrationPeriod registrationPeriod) { this.registrationPeriod = registrationPeriod; }

    public LocalDateTime getBookingDate() { return bookingDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public User getProcessedBy() { return processedBy; }
    public void setProcessedBy(User processedBy) { this.processedBy = processedBy; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public CheckInOut getCheckInOut() { return checkInOut; }
    public void setCheckInOut(CheckInOut checkInOut) { this.checkInOut = checkInOut; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
}
