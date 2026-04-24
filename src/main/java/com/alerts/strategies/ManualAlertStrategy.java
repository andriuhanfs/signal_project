package com.alerts.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.BasicAlert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Alert strategy for manual alert records emitted by the simulator.
 */
public class ManualAlertStrategy implements AlertStrategy {

    /**
     * Checks the latest manual alert record and returns an alert when it is active.
     *
     * @param patient the patient being evaluated
     * @param records all records available for the patient
     * @return a manual alert when the latest alert record is triggered
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        PatientRecord latestAlert = getLatestRecord(records, "Alert");

        if (latestAlert != null && latestAlert.getMeasurementValue() >= 1.0) {
            alerts.add(new BasicAlert(
                    String.valueOf(patient.getPatientId()),
                    "Manual alert triggered",
                    latestAlert.getTimestamp()));
        }

        return alerts;
    }

    /**
     * Finds the newest record of a specific measurement type.
     *
     * @param records all records to search
     * @param recordType measurement type to find
     * @return latest matching record, or null if none exists
     */
    private PatientRecord getLatestRecord(List<PatientRecord> records, String recordType) {
        List<PatientRecord> matchingRecords = records.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());

        if (matchingRecords.isEmpty()) {
            return null;
        }

        return matchingRecords.get(matchingRecords.size() - 1);
    }
}
