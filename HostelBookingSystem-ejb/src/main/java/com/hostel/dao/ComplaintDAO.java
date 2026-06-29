package com.hostel.dao;

import com.hostel.entity.Complaint;
import com.hostel.entity.enums.ComplaintStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ComplaintDAO extends AbstractDAO<Complaint, Long> {

    public ComplaintDAO() {
        super(Complaint.class);
    }

    public List<Complaint> findByStudent(Long studentId) {
        TypedQuery<Complaint> q = em.createNamedQuery("Complaint.findByStudent", Complaint.class);
        q.setParameter("studentId", studentId);
        return q.getResultList();
    }

    public List<Complaint> findByStatus(ComplaintStatus status) {
        TypedQuery<Complaint> q = em.createNamedQuery("Complaint.findByStatus", Complaint.class);
        q.setParameter("status", status);
        return q.getResultList();
    }

    public List<Complaint> findAll() {
        return em.createNamedQuery("Complaint.findAll", Complaint.class).getResultList();
    }

    public long countByStatus(ComplaintStatus status) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(c) FROM Complaint c WHERE c.status = :status", Long.class);
        q.setParameter("status", status);
        return q.getSingleResult();
    }
}
