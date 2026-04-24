package com.alerts.decorators;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {
    public PriorityAlertDecorator(Alert alert) {
        super(alert);
    }

    @Override
    public String getCondition() {
        return "[HIGH PRIORITY] " + alert.getCondition();
    }
}
