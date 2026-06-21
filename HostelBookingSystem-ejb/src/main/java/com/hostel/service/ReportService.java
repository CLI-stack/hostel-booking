package com.hostel.service;

import com.hostel.dao.BookingDAO;
import com.hostel.dao.PaymentDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.entity.enums.BookingStatus;
import com.hostel.entity.enums.RoomStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class ReportService {

    @Inject private BookingDAO bookingDAO;
    @Inject private RoomDAO roomDAO;
    @Inject private PaymentDAO paymentDAO;

    public Map<String, Long> getBookingStatusSummary() {
        Map<String, Long> summary = new HashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            summary.put(status.name(), bookingDAO.countByStatus(status));
        }
        return summary;
    }

    public Map<String, Long> getRoomStatusSummary() {
        Map<String, Long> summary = new HashMap<>();
        for (RoomStatus status : RoomStatus.values()) {
            summary.put(status.name(), roomDAO.countByStatus(status));
        }
        return summary;
    }

    public BigDecimal getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }

    public long getTotalRooms() {
        return roomDAO.count();
    }

    public long getTotalBookings() {
        return bookingDAO.count();
    }

    public long getPendingBookings() {
        return bookingDAO.countByStatus(BookingStatus.PENDING);
    }

    public double getOccupancyRate() {
        long total = roomDAO.count();
        if (total == 0) return 0.0;
        long occupied = roomDAO.countByStatus(RoomStatus.OCCUPIED);
        return (double) occupied / total * 100;
    }
}
