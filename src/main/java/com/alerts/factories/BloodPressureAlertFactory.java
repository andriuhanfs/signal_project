package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;

/**
 * Factory for creating blood pressure alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {

    /**
     * Creates a blood pressure alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     * @return a blood pressure alert
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}
