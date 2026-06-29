package com.hostel.dao;

import com.hostel.entity.MaintenanceRequest;
import com.hostel.entity.enums.MaintenanceStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MaintenanceDAO extends AbstractDAO<MaintenanceRequest, Long> {

    public MaintenanceDAO() {
        super(MaintenanceRequest.class);
    }

    public List<MaintenanceRequest> findAll() {
        return em.createNamedQuery("MaintenanceRequest.findAll", MaintenanceRequest.class).getResultList();
    }

    public List<MaintenanceRequest> findByStatus(MaintenanceStatus status) {
        TypedQuery<MaintenanceRequest> q = em.createNamedQuery("MaintenanceRequest.findByStatus", MaintenanceRequest.class);
        q.setParameter("status", status);
        return q.getResultList();
    }

    public List<MaintenanceRequest> findByRoom(Long roomId) {
        TypedQuery<MaintenanceRequest> q = em.createNamedQuery("MaintenanceRequest.findByRoom", MaintenanceRequest.class);
        q.setParameter("roomId", roomId);
        return q.getResultList();
    }

    public long countByStatus(MaintenanceStatus status) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.status = :status", Long.class);
        q.setParameter("status", status);
        return q.getSingleResult();
    }
}
