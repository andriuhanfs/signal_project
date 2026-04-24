package com.alerts.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Alert strategy for oxygen saturation readings.
 * It checks for critically low saturation and rapid saturation drops.
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private final BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

    /**
     * Checks oxygen saturation records and returns generated alerts.
     *
     * @param patient the patient being evaluated
     * @param records all records available for the patient
     * @return oxygen saturation alerts triggered by low values or rapid drops
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> saturationRecords = getRecordsByType(records, "Saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                alerts.add(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Low oxygen saturation",
                        record.getTimestamp()));
            }
        }

        for (int i = 1; i < saturationRecords.size(); i++) {
            PatientRecord current = saturationRecords.get(i);

            for (int j = i - 1; j >= 0; j--) {
                PatientRecord previous = saturationRecords.get(j);
                long timeDifference = current.getTimestamp() - previous.getTimestamp();

                if (timeDifference > 10 * 60 * 1000L) {
                    break;
                }

                double drop = previous.getMeasurementValue() - current.getMeasurementValue();

                if (drop >= 5) {
                    alerts.add(factory.createAlert(
                            String.valueOf(patient.getPatientId()),
                            "Rapid oxygen saturation drop",
                            current.getTimestamp()));
                    break;
                }
            }
        }

        return alerts;
    }

    /**
     * Filters records by measurement type and sorts them by timestamp.
     *
     * @param records all records to filter
     * @param recordType measurement type, such as "ECG" or "Saturation"
     * @return matching records sorted by timestamp
     */
    private List<PatientRecord> getRecordsByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> record.getRecordType().equalsIgnoreCase(recordType))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }
}
