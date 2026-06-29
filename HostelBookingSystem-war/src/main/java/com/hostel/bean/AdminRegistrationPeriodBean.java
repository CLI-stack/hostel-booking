package com.hostel.bean;

import com.hostel.entity.RegistrationPeriod;
import com.hostel.service.RegistrationPeriodService;
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
public class AdminRegistrationPeriodBean implements Serializable {

    @Inject private RegistrationPeriodService periodService;

    private List<RegistrationPeriod> periods;
    private RegistrationPeriod newPeriod = new RegistrationPeriod();
    private RegistrationPeriod selectedPeriod;
    private boolean registrationOpen;

    @PostConstruct
    public void init() {
        periods = periodService.getAllPeriods();
        registrationOpen = periodService.isRegistrationOpen();
    }

    public void createPeriod() {
        try {
            periodService.createPeriod(newPeriod);
            newPeriod = new RegistrationPeriod();
            periods = periodService.getAllPeriods();
            registrationOpen = periodService.isRegistrationOpen();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Period Created", "Registration period created."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void updatePeriod() {
        try {
            periodService.updatePeriod(selectedPeriod);
            periods = periodService.getAllPeriods();
            registrationOpen = periodService.isRegistrationOpen();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated", "Period updated."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void deactivatePeriod(Long id) {
        try {
            periodService.deactivatePeriod(id);
            periods = periodService.getAllPeriods();
            registrationOpen = periodService.isRegistrationOpen();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Deactivated", "Registration period closed."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public List<RegistrationPeriod> getPeriods() { return periods; }
    public RegistrationPeriod getNewPeriod() { return newPeriod; }
    public void setNewPeriod(RegistrationPeriod p) { this.newPeriod = p; }
    public RegistrationPeriod getSelectedPeriod() { return selectedPeriod; }
    public void setSelectedPeriod(RegistrationPeriod p) { this.selectedPeriod = p; }
    public boolean isRegistrationOpen() { return registrationOpen; }
}
