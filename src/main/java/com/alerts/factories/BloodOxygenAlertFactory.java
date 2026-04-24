package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condiString, long timestamp) {
        return new BloodOxygenAlert(patientId, condiString, timestamp);
    }
}
