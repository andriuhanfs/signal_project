package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Decorates an alert with a high-priority condition tag.
 * This is used for alerts that need urgent attention without changing the
 * underlying alert object.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    /**
     * Creates a high-priority wrapper around an alert.
     *
     * @param alert the alert to mark as high priority
     */
    public PriorityAlertDecorator(Alert alert) {
        super(alert);
    }

    /**
     * Returns the original condition prefixed with a high-priority tag.
     *
     * @return the decorated alert condition
     */
    @Override
    public String getCondition() {
        return "[HIGH PRIORITY] " + alert.getCondition();
    }
}
