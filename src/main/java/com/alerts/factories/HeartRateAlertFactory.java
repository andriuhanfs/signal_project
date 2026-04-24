package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.HeartRateAlert;

/**
 * Factory for creating heart-rate alerts.
 */
public class HeartRateAlertFactory extends AlertFactory {

    /**
     * Creates a heart-rate alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     * @return a heart-rate alert
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new HeartRateAlert(patientId, condition, timestamp);
    }
}
