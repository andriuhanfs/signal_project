package com.alerts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.ECGStrategy;
import com.alerts.strategies.HypotensiveHypoxemiaStrategy;
import com.alerts.strategies.ManualAlertStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class AlertGenerator {
    private DataStorage dataStorage;
    private List<Alert> alerts;
    private List<AlertStrategy> strategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alerts = new ArrayList<>();
        this.strategies = Arrays.asList(
                new BloodPressureStrategy(),
                new OxygenSaturationStrategy(),
                new HypotensiveHypoxemiaStrategy(),
                new ECGStrategy(),
                new ManualAlertStrategy());
    }

    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);

        for (AlertStrategy strategy : strategies) {
            alerts.addAll(strategy.checkAlert(patient, records));
        }
    }

    public List<Alert> getAlerts() {
        return new ArrayList<>(alerts);
    }
}
