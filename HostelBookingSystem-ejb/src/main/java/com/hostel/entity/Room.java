package com.hostel.entity;

import com.hostel.entity.enums.RoomStatus;
import com.hostel.entity.enums.RoomType;
import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rooms", uniqueConstraints = {
    @UniqueConstraint(columnNames = "room_number")
})
@NamedQueries({
    @NamedQuery(name = "Room.findAll",
        query = "SELECT r FROM Room r ORDER BY r.roomNumber"),
    @NamedQuery(name = "Room.findAvailable",
        query = "SELECT r FROM Room r WHERE r.status = com.hostel.entity.enums.RoomStatus.AVAILABLE ORDER BY r.roomNumber"),
    @NamedQuery(name = "Room.findByType",
        query = "SELECT r FROM Room r WHERE r.type = :type AND r.status = com.hostel.entity.enums.RoomStatus.AVAILABLE ORDER BY r.roomNumber"),
    @NamedQuery(name = "Room.findByRoomNumber",
        query = "SELECT r FROM Room r WHERE r.roomNumber = :roomNumber"),
    @NamedQuery(name = "Room.countByStatus",
        query = "SELECT r.status, COUNT(r) FROM Room r GROUP BY r.status")
})
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private RoomType type;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price_per_semester", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerSemester;

    @NotNull(message = "Room status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "floor", nullable = false)
    private int floor;

    @Column(name = "block", length = 10)
    private String block;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "has_wifi", nullable = false)
    private boolean hasWifi = true;

    @Column(name = "has_ac", nullable = false)
    private boolean hasAc = false;

    @Column(name = "has_attached_bathroom", nullable = false)
    private boolean hasAttachedBathroom = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceRequest> maintenanceRequests;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Room() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public BigDecimal getPricePerSemester() { return pricePerSemester; }
    public void setPricePerSemester(BigDecimal pricePerSemester) { this.pricePerSemester = pricePerSemester; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isHasWifi() { return hasWifi; }
    public void setHasWifi(boolean hasWifi) { this.hasWifi = hasWifi; }

    public boolean isHasAc() { return hasAc; }
    public void setHasAc(boolean hasAc) { this.hasAc = hasAc; }

    public boolean isHasAttachedBathroom() { return hasAttachedBathroom; }
    public void setHasAttachedBathroom(boolean hasAttachedBathroom) { this.hasAttachedBathroom = hasAttachedBathroom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<MaintenanceRequest> getMaintenanceRequests() { return maintenanceRequests; }
    public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) { this.maintenanceRequests = maintenanceRequests; }
}
