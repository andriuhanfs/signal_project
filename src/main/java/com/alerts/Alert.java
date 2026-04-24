package com.alerts;

// Represents an alert
public interface Alert {
    String getPatientId();
    String getCondition();
    long getTimestamp();
}


