package com.alerts;

public class HeartRateAlert extends BasicAlert {
    public HeartRateAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
