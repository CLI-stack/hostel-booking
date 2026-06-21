package com.hostel.service;

import com.hostel.dao.RegistrationPeriodDAO;
import com.hostel.entity.RegistrationPeriod;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class RegistrationPeriodService {

    @Inject
    private RegistrationPeriodDAO periodDAO;

    public RegistrationPeriod createPeriod(RegistrationPeriod period) {
        return periodDAO.save(period);
    }

    public RegistrationPeriod updatePeriod(RegistrationPeriod period) {
        periodDAO.findById(period.getId())
            .orElseThrow(() -> new IllegalArgumentException("Registration period not found"));
        return periodDAO.update(period);
    }

    public void deactivatePeriod(Long periodId) {
        RegistrationPeriod period = periodDAO.findById(periodId)
            .orElseThrow(() -> new IllegalArgumentException("Registration period not found"));
        period.setActive(false);
        periodDAO.update(period);
    }

    public Optional<RegistrationPeriod> getActivePeriod() {
        return periodDAO.findActivePeriod();
    }

    public List<RegistrationPeriod> getAllPeriods() {
        return periodDAO.findAll();
    }

    public boolean isRegistrationOpen() {
        return periodDAO.isRegistrationOpen();
    }
}
