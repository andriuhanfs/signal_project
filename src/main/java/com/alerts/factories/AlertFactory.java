package com.alerts.factories;

import com.alerts.Alert;

/**
 * Base factory for creating alert objects without coupling callers to concrete
 * alert classes.
 */
public abstract class AlertFactory {

    /**
     * Creates an alert for the provided patient condition and timestamp.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     * @return a concrete alert instance
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp); 
}
