package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

/**
 * Factory for creating blood oxygen alerts.
 */
public class BloodOxygenAlertFactory extends AlertFactory {

    /**
     * Creates a blood oxygen alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     * @return a blood oxygen alert
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}
