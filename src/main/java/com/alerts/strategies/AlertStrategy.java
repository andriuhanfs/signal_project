package com.alerts.strategies;

import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Strategy interface for evaluating patient records and creating alerts.
 */
public interface AlertStrategy {

    /**
     * Checks patient records against a monitoring rule.
     *
     * @param patient the patient being evaluated
     * @param records all records available for the patient
     * @return alerts produced by the strategy
     */
    List<Alert> checkAlert(Patient patient, List<PatientRecord> records);
}
