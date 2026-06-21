package com.hostel.dao;

import com.hostel.entity.RegistrationPeriod;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Stateless
public class RegistrationPeriodDAO extends AbstractDAO<RegistrationPeriod, Long> {

    public RegistrationPeriodDAO() {
        super(RegistrationPeriod.class);
    }

    public Optional<RegistrationPeriod> findActivePeriod() {
        try {
            TypedQuery<RegistrationPeriod> q = em.createNamedQuery(
                "RegistrationPeriod.findActive", RegistrationPeriod.class);
            q.setParameter("today", LocalDate.now());
            q.setMaxResults(1);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<RegistrationPeriod> findAll() {
        return em.createNamedQuery("RegistrationPeriod.findAll", RegistrationPeriod.class).getResultList();
    }

    public boolean isRegistrationOpen() {
        return findActivePeriod().isPresent();
    }
}
