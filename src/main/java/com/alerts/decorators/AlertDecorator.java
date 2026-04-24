package com.alerts.decorators;

import com.alerts.Alert;

public abstract class AlertDecorator implements Alert {
    protected final Alert alert;

    protected AlertDecorator(Alert alert) {
        this.alert = alert;
    }

    @Override
    public String getPatientId() {
        return alert.getPatientId();
    }

    @Override
    public long getTimestamp() {
        return alert.getTimestamp();
    }
}
