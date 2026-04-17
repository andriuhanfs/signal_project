package alerts;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

/**
 * Tests alert generation rules for blood pressure, oxygen saturation, ECG, and manual alerts.
 */
class AlertGeneratorTest {

    @ParameterizedTest
    @CsvSource({
            "SystolicPressure, 181, Critical systolic blood pressure",
            "SystolicPressure, 89, Critical systolic blood pressure",
            "DiastolicPressure, 121, Critical diastolic blood pressure",
            "DiastolicPressure, 59, Critical diastolic blood pressure"
    })
    void testCriticalBloodPressureAlerts(String label, double value, String expectedCondition) {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, value, label, 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), expectedCondition);
    }

    @Test
    void testNoCriticalBloodPressureAlertAtBoundaryValues() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 180.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 120.0, "DiastolicPressure", 3000L);
        storage.addPatientData(1, 60.0, "DiastolicPressure", 4000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(generator.getAlerts().isEmpty());
    }

    @Test
    void testIncreasingBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 112.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 125.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Systolic blood pressure trend");
    }

    @Test
    void testDecreasingBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        storage.addPatientData(1, 113.0, "DiastolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "DiastolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Diastolic blood pressure trend");
    }

    @Test
    void testLowSaturationAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 91.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Low oxygen saturation");
    }

    @Test
    void testNoLowSaturationAlertAtBoundaryValue() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 92.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(generator.getAlerts().isEmpty());
    }

    @Test
    void testRapidSaturationDropAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 1000L + 5 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Rapid oxygen saturation drop");
    }

    @Test
    void testNoRapidSaturationDropOutsideTenMinutes() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 1000L + 11 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(containsAlert(generator.getAlerts(), "Rapid oxygen saturation drop"));
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 89.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 2000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Hypotensive hypoxemia");
    }

    @Test
    void testAbnormalEcgPeakAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 0.1, "ECG", 1000L);
        storage.addPatientData(1, 0.1, "ECG", 2000L);
        storage.addPatientData(1, 0.1, "ECG", 3000L);
        storage.addPatientData(1, 1.0, "ECG", 4000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Abnormal ECG peak");
    }

    @Test
    void testManualAlertTriggered() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 1.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertContainsAlert(generator.getAlerts(), "Manual alert triggered");
    }

    @Test
    void testManualAlertResolvedDoesNotTriggerAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 0.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(generator.getAlerts().isEmpty());
    }

    @Test
    void testEvaluateDataWithEmptyPatientRecordsDoesNotCreateAlerts() {
        DataStorage storage = new DataStorage();
        Patient patient = new Patient(1);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(patient);

        assertTrue(generator.getAlerts().isEmpty());
    }

    private void assertContainsAlert(List<Alert> alerts, String condition) {
        assertTrue(containsAlert(alerts, condition), "Expected alert condition: " + condition);
    }

    private boolean containsAlert(List<Alert> alerts, String condition) {
        return alerts.stream()
                .anyMatch(alert -> alert.getCondition().equals(condition));
    }
}
