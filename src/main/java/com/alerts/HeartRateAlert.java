package com.alerts;

/**
 * Alert type for abnormal heart-rate readings.
 */
public class HeartRateAlert extends BasicAlert {

    /**
     * Creates a heart-rate alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     */
    public HeartRateAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
