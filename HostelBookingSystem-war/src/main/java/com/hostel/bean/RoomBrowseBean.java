package com.hostel.bean;

import com.hostel.entity.Room;
import com.hostel.entity.enums.RoomType;
import com.hostel.service.RoomService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RoomBrowseBean implements Serializable {

    @Inject private RoomService roomService;

    private List<Room> rooms;
    private List<Room> filteredRooms;
    private String selectedType;
    private Room selectedRoom;

    @PostConstruct
    public void init() {
        rooms = roomService.getAvailableRooms();
    }

    public void filterByType() {
        if (selectedType == null || selectedType.isBlank()) {
            rooms = roomService.getAvailableRooms();
        } else {
            rooms = roomService.getAvailableRoomsByType(RoomType.valueOf(selectedType));
        }
    }

    public void selectRoom(Room room) {
        this.selectedRoom = room;
    }

    public RoomType[] getRoomTypes() {
        return RoomType.values();
    }

    public List<Room> getRooms() { return rooms; }
    public List<Room> getFilteredRooms() { return filteredRooms; }
    public void setFilteredRooms(List<Room> filteredRooms) { this.filteredRooms = filteredRooms; }
    public String getSelectedType() { return selectedType; }
    public void setSelectedType(String selectedType) { this.selectedType = selectedType; }
    public Room getSelectedRoom() { return selectedRoom; }
    public void setSelectedRoom(Room selectedRoom) { this.selectedRoom = selectedRoom; }
}
