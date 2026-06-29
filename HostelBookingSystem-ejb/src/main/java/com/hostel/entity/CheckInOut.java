package com.hostel.entity;

import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_out")
@NamedQueries({
    @NamedQuery(name = "CheckInOut.findByBooking",
        query = "SELECT c FROM CheckInOut c WHERE c.booking.id = :bookingId"),
    @NamedQuery(name = "CheckInOut.findAll",
        query = "SELECT c FROM CheckInOut c ORDER BY c.checkInDate DESC"),
    @NamedQuery(name = "CheckInOut.findCurrentlyCheckedIn",
        query = "SELECT c FROM CheckInOut c WHERE c.checkOutDate IS NULL ORDER BY c.checkInDate DESC")
})
public class CheckInOut implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", allocationSize = 1)
    private Long id;

    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    private LocalDateTime checkOutDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_by")
    private User checkInBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_out_by")
    private User checkOutBy;

    @Column(name = "check_in_notes", length = 500)
    private String checkInNotes;

    @Column(name = "check_out_notes", length = 500)
    private String checkOutNotes;

    @Column(name = "room_condition_on_checkin", length = 200)
    private String roomConditionOnCheckin;

    @Column(name = "room_condition_on_checkout", length = 200)
    private String roomConditionOnCheckout;

    public CheckInOut() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public LocalDateTime getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDateTime checkInDate) { this.checkInDate = checkInDate; }

    public LocalDateTime getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDateTime checkOutDate) { this.checkOutDate = checkOutDate; }

    public User getCheckInBy() { return checkInBy; }
    public void setCheckInBy(User checkInBy) { this.checkInBy = checkInBy; }

    public User getCheckOutBy() { return checkOutBy; }
    public void setCheckOutBy(User checkOutBy) { this.checkOutBy = checkOutBy; }

    public String getCheckInNotes() { return checkInNotes; }
    public void setCheckInNotes(String checkInNotes) { this.checkInNotes = checkInNotes; }

    public String getCheckOutNotes() { return checkOutNotes; }
    public void setCheckOutNotes(String checkOutNotes) { this.checkOutNotes = checkOutNotes; }

    public String getRoomConditionOnCheckin() { return roomConditionOnCheckin; }
    public void setRoomConditionOnCheckin(String roomConditionOnCheckin) { this.roomConditionOnCheckin = roomConditionOnCheckin; }

    public String getRoomConditionOnCheckout() { return roomConditionOnCheckout; }
    public void setRoomConditionOnCheckout(String roomConditionOnCheckout) { this.roomConditionOnCheckout = roomConditionOnCheckout; }
}
