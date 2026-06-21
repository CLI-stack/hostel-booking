package com.hostel.bean;

import com.hostel.entity.Room;
import com.hostel.entity.enums.RoomStatus;
import com.hostel.entity.enums.RoomType;
import com.hostel.service.RoomService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AdminRoomBean implements Serializable {

    @Inject private RoomService roomService;

    private List<Room> rooms;
    private Room newRoom = new Room();
    private Room selectedRoom;

    @PostConstruct
    public void init() {
        rooms = roomService.getAllRooms();
    }

    public void createRoom() {
        try {
            roomService.createRoom(newRoom);
            newRoom = new Room();
            rooms = roomService.getAllRooms();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Room Created", "New room added successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void updateRoom() {
        try {
            roomService.updateRoom(selectedRoom);
            rooms = roomService.getAllRooms();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Room Updated", "Room saved successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void selectRoom(Room room) {
        this.selectedRoom = room;
    }

    public RoomType[] getRoomTypes() { return RoomType.values(); }
    public RoomStatus[] getRoomStatuses() { return RoomStatus.values(); }

    public List<Room> getRooms() { return rooms; }
    public Room getNewRoom() { return newRoom; }
    public void setNewRoom(Room newRoom) { this.newRoom = newRoom; }
    public Room getSelectedRoom() { return selectedRoom; }
    public void setSelectedRoom(Room selectedRoom) { this.selectedRoom = selectedRoom; }
    public long getTotalRooms() { return roomService.countTotal(); }
    public long getAvailableRooms() { return roomService.countAvailable(); }
    public long getOccupiedRooms() { return roomService.countOccupied(); }
    public long getMaintenanceRooms() { return roomService.countMaintenance(); }
}
