package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Decorates an alert with repeat interval information.
 * This models alerts that should be repeated while the condition remains active.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private final long repeatIntervalMillis;

    /**
     * Creates a repeating-alert wrapper around an alert.
     *
     * @param alert the alert to repeat
     * @param repeatIntervalMillis repeat interval in milliseconds
     */
    public RepeatedAlertDecorator(Alert alert, long repeatIntervalMillis) {
        super(alert);
        this.repeatIntervalMillis = repeatIntervalMillis;
    }

    /**
     * Returns the original condition prefixed with repeat interval information.
     *
     * @return the decorated alert condition
     */
    @Override
    public String getCondition() {
        return "[REPEATED EVERY " + repeatIntervalMillis + "ms] " + alert.getCondition();
    }
}
