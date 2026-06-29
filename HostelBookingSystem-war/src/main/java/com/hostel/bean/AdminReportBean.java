package com.hostel.bean;

import com.hostel.service.ReportService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Named
@ViewScoped
public class AdminReportBean implements Serializable {

    @Inject private ReportService reportService;

    private Map<String, Long> bookingStatusSummary;
    private Map<String, Long> roomStatusSummary;
    private BigDecimal totalRevenue;
    private long totalRooms;
    private long totalBookings;
    private long pendingBookings;
    private double occupancyRate;

    @PostConstruct
    public void init() {
        bookingStatusSummary = reportService.getBookingStatusSummary();
        roomStatusSummary    = reportService.getRoomStatusSummary();
        totalRevenue         = reportService.getTotalRevenue();
        totalRooms           = reportService.getTotalRooms();
        totalBookings        = reportService.getTotalBookings();
        pendingBookings      = reportService.getPendingBookings();
        occupancyRate        = reportService.getOccupancyRate();
    }

    public Map<String, Long> getBookingStatusSummary() { return bookingStatusSummary; }
    public Map<String, Long> getRoomStatusSummary() { return roomStatusSummary; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public long getTotalRooms() { return totalRooms; }
    public long getTotalBookings() { return totalBookings; }
    public long getPendingBookings() { return pendingBookings; }
    public double getOccupancyRate() { return occupancyRate; }
}
