package com.alerts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;


/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;


    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);

        checkBloodPressureAlerts(patient, records);
        checkSaturationAlerts(patient, records);
        checkHypotensiveHypoxemiaAlert(patient, records);
        checkEcgAlerts(patient, records);
        checkManualAlert(patient, records);
    }

    /**
     * Returns a copy of all alerts generated so far.
     * Returning a copy prevents external code from modifying the internal list.
     *
     * @return generated alerts
     */
    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        alerts.add(alert);
    }

    /**
     * Checks systolic and diastolic blood pressure records for critical values
     * and three-reading trends.
     *
     * Critical thresholds:
     * - systolic above 180 or below 90
     * - diastolic above 120 or below 60
     *
     * @param patient the patient being evaluated
     * @param records all records for the patient
     */
    private void checkBloodPressureAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = getRecordsByType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = getRecordsByType(records, "DiastolicPressure");

        for (PatientRecord record : systolicRecords) {
            double value = record.getMeasurementValue();

            if (value > 180 || value < 90) {
                triggerAlert(new BasicAlert(
                        String.valueOf(patient.getPatientId()),
                        "Critical systolic blood pressure",
                        record.getTimestamp()));
            }
        }

        for (PatientRecord record : diastolicRecords) {
            double value = record.getMeasurementValue();

            if (value > 120 || value < 60) {
                triggerAlert(new BasicAlert(
                        String.valueOf(patient.getPatientId()),
                        "Critical diastolic blood pressure",
                        record.getTimestamp()));
            }
        }

        checkTrend(patient, systolicRecords, "Systolic blood pressure trend");
        checkTrend(patient, diastolicRecords, "Diastolic blood pressure trend");
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
    private void checkTrend(Patient patient, List<PatientRecord> records, String condition) {

        for (int i = 0; i <= records.size() - 3; i++) {

            PatientRecord first = records.get(i);
            PatientRecord second = records.get(i + 1);
            PatientRecord third = records.get(i + 2);

            double change1 = second.getMeasurementValue() - first.getMeasurementValue();
            double change2 = third.getMeasurementValue() - second.getMeasurementValue();

            boolean increasing = change1 > 10 && change2 > 10;
            boolean decreasing = change1 < -10 && change2 < -10;

            if (increasing || decreasing) {
                triggerAlert(new BasicAlert(
                        String.valueOf(patient.getPatientId()),
                        condition,
                        third.getTimestamp()));
            }
        }
    }

    /**
     * Checks oxygen saturation records for low saturation and rapid drops.
     *
     * Low saturation means a value below 92%.
     * Rapid drop means a decrease of at least 5 percentage points within 10
     * minutes.
     *
     * @param patient the patient being evaluated
     * @param records all records for the patient
     */
    private void checkSaturationAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = getRecordsByType(records, "Saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                triggerAlert(new BasicAlert(
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
                    triggerAlert(new BasicAlert(
                            String.valueOf(patient.getPatientId()),
                            "Rapid oxygen saturation drop",
                            current.getTimestamp()));
                    break;
                }
            }
        }
    }
    
    /**
     * Checks the combined hypotensive hypoxemia condition.
     *
     * This alert is triggered when the latest systolic blood pressure is below 90
     * and the latest oxygen saturation is below 92.
     *
     * @param patient the patient being evaluated
     * @param records all records for the patient
     */
    private void checkHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> records) {
        PatientRecord latestSystolic = getLatestRecord(records, "SystolicPressure");
        PatientRecord latestSaturation = getLatestRecord(records, "Saturation");

        if (latestSystolic == null || latestSaturation == null) {
            return;
        }

        if (latestSystolic.getMeasurementValue() < 90 && latestSaturation.getMeasurementValue() < 92) {
            triggerAlert(new BasicAlert(
                    String.valueOf(patient.getPatientId()),
                    "Hypotensive hypoxemia",
                    Math.max(latestSystolic.getTimestamp(), latestSaturation.getTimestamp())));
        }
    }

    /**
     * Checks ECG data for abnormal peaks.
     *
     * Considering that a value is considered an abnormal peak when it is more than 0.5
     * above the average of the previous three ECG readings.
     *
     * @param patient the patient being evaluated
     * @param records all records for the patient
     */
    private void checkEcgAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = getRecordsByType(records, "ECG");

        for (int i = 3; i < ecgRecords.size(); i++) {
            double average = (
                    ecgRecords.get(i - 1).getMeasurementValue()
                    + ecgRecords.get(i - 2).getMeasurementValue()
                    + ecgRecords.get(i - 3).getMeasurementValue()) / 3.0;

            double currentValue = ecgRecords.get(i).getMeasurementValue();

            if (currentValue > average + 0.5) {
                triggerAlert(new BasicAlert(
                        String.valueOf(patient.getPatientId()),
                        "Abnormal ECG peak",
                        ecgRecords.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Checks records emitted by the simulator's Alert data generator.
     *
     * Assumption: FileDataReader converts "triggered" to 1.0 and "resolved" to
     * 0.0. Therefore, a latest Alert value of 1.0 or higher means the manual alert
     * is active.
     *
     * @param patient the patient being evaluated
     * @param records all records for the patient
     */
    private void checkManualAlert(Patient patient, List<PatientRecord> records) {
        PatientRecord latestAlert = getLatestRecord(records, "Alert");

        if (latestAlert == null) {
            return;
        }

        if (latestAlert.getMeasurementValue() >= 1.0) {
            triggerAlert(new BasicAlert(
                    String.valueOf(patient.getPatientId()),
                    "Manual alert triggered",
                    latestAlert.getTimestamp()));
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

    /**
     * Finds the newest record of a specific measurement type.
     *
     * @param records all records to search
     * @param recordType measurement type to find
     * @return latest matching record, or null if none exists
     */
    private PatientRecord getLatestRecord(List<PatientRecord> records, String recordType) {
        List<PatientRecord> matchingRecords = getRecordsByType(records, recordType);

        if (matchingRecords.isEmpty()) {
            return null;
        }

        return matchingRecords.get(matchingRecords.size() - 1);
    }
}
