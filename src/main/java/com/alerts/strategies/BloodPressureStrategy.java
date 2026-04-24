package com.alerts.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {
    private final BloodPressureAlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> systolicRecords = getRecordsByType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = getRecordsByType(records, "DiastolicPressure");

        for (PatientRecord record : systolicRecords) {
            double value = record.getMeasurementValue();

            if (value > 180 || value < 90) {
                alerts.add(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Critical systolic blood pressure",
                        record.getTimestamp()));
            }
        }

        for (PatientRecord record : diastolicRecords) {
            double value = record.getMeasurementValue();

            if (value > 120 || value < 60) {
                alerts.add(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Critical diastolic blood pressure",
                        record.getTimestamp()));
            }
        }

        checkTrend(patient, systolicRecords, "Systolic blood pressure trend", alerts);
        checkTrend(patient, diastolicRecords, "Diastolic blood pressure trend", alerts);

        return alerts;
    }

    /**
     * Checks whether three consecutive readings are consistently increasing or
     * decreasing by more than 10 units between each reading.
     *
     * Example increasing trend:
     * 100 -> 112 -> 125
     *
     * Example decreasing trend:
     * 125 -> 113 -> 100
     *
     * @param patient the patient being evaluated
     * @param records records of one measurement type, sorted by timestamp
     * @param condition alert condition text
     */
    private void checkTrend(Patient patient, List<PatientRecord> records, String condition, List<Alert> alerts) {
        for (int i = 0; i <= records.size() - 3; i++) {
            PatientRecord first = records.get(i);
            PatientRecord second = records.get(i + 1);
            PatientRecord third = records.get(i + 2);

            double change1 = second.getMeasurementValue() - first.getMeasurementValue();
            double change2 = third.getMeasurementValue() - second.getMeasurementValue();

            boolean increasing = change1 > 10 && change2 > 10;
            boolean decreasing = change1 < -10 && change2 < -10;

            if (increasing || decreasing) {
                alerts.add(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        condition,
                        third.getTimestamp()));
            }
        }
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
