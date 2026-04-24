package com.alerts.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class ECGStrategy implements AlertStrategy {
    private final ECGAlertFactory factory = new ECGAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> ecgRecords = getRecordsByType(records, "ECG");

        for (int i = 3; i < ecgRecords.size(); i++) {
            double average = (
                    ecgRecords.get(i - 1).getMeasurementValue()
                    + ecgRecords.get(i - 2).getMeasurementValue()
                    + ecgRecords.get(i - 3).getMeasurementValue()) / 3.0;

            double currentValue = ecgRecords.get(i).getMeasurementValue();

            if (currentValue > average + 0.5) {
                alerts.add(factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Abnormal ECG peak",
                        ecgRecords.get(i).getTimestamp()));
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
