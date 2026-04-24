package com.alerts;

/**
 * Default immutable implementation of the Alert interface.
 */
public class BasicAlert implements Alert {

    private final String patientId;
    private final String condition;
    private final long timestamp;

    /**
     * Creates an alert for a patient condition at a specific time.
     *
     * @param patientId the patient identifier
     * @param condition the alert condition
     * @param timestamp the time the alert was generated
     */
    public BasicAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    } 

    /**
     * Returns the identifier of the patient associated with this alert.
     *
     * @return the patient identifier
     */
    @Override
    public String getPatientId() {
        return patientId;
    }

    /**
     * Returns the condition that triggered this alert.
     *
     * @return the alert condition
     */
    @Override
    public String getCondition() {
        return condition;
    }

    /**
     * Returns the time at which this alert was generated.
     *
     * @return the alert timestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
