package com.alerts.decorators;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator {
    private final long repeatIntervalMillis;

    public RepeatedAlertDecorator(Alert alert, long repeatIntervalMillis) {
        super(alert);
        this.repeatIntervalMillis = repeatIntervalMillis;
    }

    @Override
    public String getCondition() {
        return "[REPEATED EVERY " + repeatIntervalMillis + "ms] " + alert.getCondition();
    }
}
