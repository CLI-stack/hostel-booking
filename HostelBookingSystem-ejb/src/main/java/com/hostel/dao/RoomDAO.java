package com.hostel.dao;

import com.hostel.entity.Room;
import com.hostel.entity.enums.RoomStatus;
import com.hostel.entity.enums.RoomType;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RoomDAO extends AbstractDAO<Room, Long> {

    public RoomDAO() {
        super(Room.class);
    }

    public List<Room> findAll() {
        return em.createNamedQuery("Room.findAll", Room.class).getResultList();
    }

    public List<Room> findAvailable() {
        return em.createNamedQuery("Room.findAvailable", Room.class).getResultList();
    }

    public List<Room> findByType(RoomType type) {
        TypedQuery<Room> q = em.createNamedQuery("Room.findByType", Room.class);
        q.setParameter("type", type);
        return q.getResultList();
    }

    public Optional<Room> findByRoomNumber(String roomNumber) {
        try {
            TypedQuery<Room> q = em.createNamedQuery("Room.findByRoomNumber", Room.class);
            q.setParameter("roomNumber", roomNumber);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Room> findByStatusAndType(RoomStatus status, RoomType type) {
        TypedQuery<Room> q = em.createQuery(
            "SELECT r FROM Room r WHERE r.status = :status AND r.type = :type ORDER BY r.roomNumber",
            Room.class);
        q.setParameter("status", status);
        q.setParameter("type", type);
        return q.getResultList();
    }

    public long countByStatus(RoomStatus status) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(r) FROM Room r WHERE r.status = :status", Long.class);
        q.setParameter("status", status);
        return q.getSingleResult();
    }
}
