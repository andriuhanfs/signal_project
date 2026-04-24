package com.alerts.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.factories.HeartRateAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private static final long REPEAT_INTERVAL_MILLIS = 60000L;

    private final HeartRateAlertFactory factory = new HeartRateAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> heartRateRecords = getRecordsByType(records, "HeartRate");

        for (PatientRecord record : heartRateRecords) {
            double value = record.getMeasurementValue();

            if (value < 60) {
                Alert alert = factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Low heart rate",
                        record.getTimestamp());
                alerts.add(new RepeatedAlertDecorator(alert, REPEAT_INTERVAL_MILLIS));
            } else if (value > 100) {
                Alert alert = factory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "High heart rate",
                        record.getTimestamp());
                alerts.add(new PriorityAlertDecorator(alert));
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
