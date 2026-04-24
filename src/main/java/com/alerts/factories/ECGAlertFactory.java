package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.ECGAlert;

public class ECGAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condiString, long timestamp) {
        return new ECGAlert(patientId, condiString, timestamp);
    }
}
