package com.alerts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.ECGStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.HypotensiveHypoxemiaStrategy;
import com.alerts.strategies.ManualAlertStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Coordinates alert strategies and stores alerts generated for evaluated
 * patients.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;
    private List<AlertStrategy> strategies;

    /**
     * Creates an alert generator backed by the given data storage.
     *
     * @param dataStorage the patient data repository used by this generator
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
        this.strategies = Arrays.asList(
                new BloodPressureStrategy(),
                new OxygenSaturationStrategy(),
                new HypotensiveHypoxemiaStrategy(),
                new ECGStrategy(),
                new HeartRateStrategy(),
                new ManualAlertStrategy());
    }

    /**
     * Evaluates all configured alert strategies for a patient.
     *
     * @param patient the patient whose records should be checked
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);

        for (AlertStrategy strategy : strategies) {
            alerts.addAll(strategy.checkAlert(patient, records));
        }
    }

    /**
     * Returns a copy of all alerts generated so far.
     *
     * @return generated alerts
     */
    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }
}
