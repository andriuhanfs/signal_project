package com.alerts;

/**
 * Represents an alert generated for a patient monitoring condition.
 */
public interface Alert {

    /**
     * Returns the identifier of the patient associated with this alert.
     *
     * @return the patient identifier
     */
    String getPatientId();

    /**
     * Returns the condition that triggered this alert.
     *
     * @return the alert condition
     */
    String getCondition();

    /**
     * Returns the time at which this alert was generated.
     *
     * @return the alert timestamp
     */
    long getTimestamp();
}

