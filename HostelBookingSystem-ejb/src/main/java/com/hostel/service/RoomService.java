package com.hostel.service;

import com.hostel.dao.RoomDAO;
import com.hostel.entity.Room;
import com.hostel.entity.enums.RoomStatus;
import com.hostel.entity.enums.RoomType;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class RoomService {

    @Inject
    private RoomDAO roomDAO;

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomDAO.findAvailable();
    }

    public List<Room> getAvailableRoomsByType(RoomType type) {
        return roomDAO.findByType(type);
    }

    public Optional<Room> getRoomById(Long id) {
        return roomDAO.findById(id);
    }

    public Room createRoom(Room room) {
        if (roomDAO.findByRoomNumber(room.getRoomNumber()).isPresent()) {
            throw new IllegalArgumentException("Room number already exists: " + room.getRoomNumber());
        }
        room.setStatus(RoomStatus.AVAILABLE);
        return roomDAO.save(room);
    }

    public Room updateRoom(Room room) {
        roomDAO.findById(room.getId())
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return roomDAO.update(room);
    }

    public void updateRoomStatus(Long roomId, RoomStatus status) {
        Room room = roomDAO.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        roomDAO.update(room);
    }

    public long countAvailable() {
        return roomDAO.countByStatus(RoomStatus.AVAILABLE);
    }

    public long countOccupied() {
        return roomDAO.countByStatus(RoomStatus.OCCUPIED);
    }

    public long countMaintenance() {
        return roomDAO.countByStatus(RoomStatus.MAINTENANCE);
    }

    public long countTotal() {
        return roomDAO.count();
    }
}
