package com.alerts;

/**
 * Alert type for blood oxygen saturation anomalies.
 */
public class BloodOxygenAlert extends BasicAlert {

    /**
     * Creates a blood oxygen alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     */
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
