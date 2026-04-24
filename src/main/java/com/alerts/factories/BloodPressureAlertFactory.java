package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;

public class BloodPressureAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condiString, long timestamp) {
        return new BloodPressureAlert(patientId, condiString, timestamp);
    }
}
