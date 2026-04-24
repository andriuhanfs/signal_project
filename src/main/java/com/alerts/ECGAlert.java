package com.alerts;

/**
 * Alert type for ECG anomalies.
 */
public class ECGAlert extends BasicAlert {

    /**
     * Creates an ECG alert.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     */
    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
