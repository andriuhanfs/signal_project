package com.alerts;

/**
 * Alert type for blood pressure anomalies.
 */
public class BloodPressureAlert extends BasicAlert {

    /**
     * Creates a blood pressure alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     */
    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
