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
 * Alert strategy for the combined hypotensive hypoxemia condition.
 * It checks whether the latest systolic pressure and oxygen saturation readings
 * are both below their critical thresholds.
 */
public class HypotensiveHypoxemiaStrategy implements AlertStrategy {

    /**
     * Checks for combined low systolic pressure and low oxygen saturation.
     *
     * @param patient the patient being evaluated
     * @param records all records available for the patient
     * @return an alert when hypotensive hypoxemia is detected
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();

        PatientRecord latestSystolic = getLatestRecord(records, "SystolicPressure");
        PatientRecord latestSaturation = getLatestRecord(records, "Saturation");

        if (latestSystolic == null || latestSaturation == null) {
            return alerts;
        }

        if (latestSystolic.getMeasurementValue() < 90 && latestSaturation.getMeasurementValue() < 92) {
            alerts.add(new BasicAlert(
                    String.valueOf(patient.getPatientId()),
                    "Hypotensive hypoxemia",
                    Math.max(latestSystolic.getTimestamp(), latestSaturation.getTimestamp())));
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
