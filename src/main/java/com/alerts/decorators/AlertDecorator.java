package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Base decorator for alerts.
 * It delegates common alert data to the wrapped alert while allowing subclasses
 * to extend specific behavior such as the displayed condition.
 */
public abstract class AlertDecorator implements Alert {
    protected final Alert alert;

    /**
     * Creates a decorator around an existing alert.
     *
     * @param alert the alert being extended
     */
    protected AlertDecorator(Alert alert) {
        this.alert = alert;
    }

    /**
     * Returns the patient identifier from the wrapped alert.
     *
     * @return the patient identifier
     */
    @Override
    public String getPatientId() {
        return alert.getPatientId();
    }

    /**
     * Returns the timestamp from the wrapped alert.
     *
     * @return the alert timestamp
     */
    @Override
    public long getTimestamp() {
        return alert.getTimestamp();
    }
}
