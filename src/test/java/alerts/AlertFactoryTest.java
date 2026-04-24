package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;
import com.alerts.HeartRateAlert;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.alerts.factories.HeartRateAlertFactory;

class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactoryCreatesBloodPressureAlert() {
        Alert alert = new BloodPressureAlertFactory().createAlert("1", "Critical systolic blood pressure", 1000L);

        assertTrue(alert instanceof BloodPressureAlert);
        assertAlertFields(alert, "1", "Critical systolic blood pressure", 1000L);
    }

    @Test
    void testBloodOxygenAlertFactoryCreatesBloodOxygenAlert() {
        Alert alert = new BloodOxygenAlertFactory().createAlert("2", "Low oxygen saturation", 2000L);

        assertTrue(alert instanceof BloodOxygenAlert);
        assertAlertFields(alert, "2", "Low oxygen saturation", 2000L);
    }

    @Test
    void testEcgAlertFactoryCreatesEcgAlert() {
        Alert alert = new ECGAlertFactory().createAlert("3", "Abnormal ECG peak", 3000L);

        assertTrue(alert instanceof ECGAlert);
        assertAlertFields(alert, "3", "Abnormal ECG peak", 3000L);
    }

    @Test
    void testHeartRateAlertFactoryCreatesHeartRateAlert() {
        Alert alert = new HeartRateAlertFactory().createAlert("4", "High heart rate", 4000L);

        assertTrue(alert instanceof HeartRateAlert);
        assertAlertFields(alert, "4", "High heart rate", 4000L);
    }

    private void assertAlertFields(Alert alert, String patientId, String condition, long timestamp) {
        assertEquals(patientId, alert.getPatientId());
        assertEquals(condition, alert.getCondition());
        assertEquals(timestamp, alert.getTimestamp());
    }
}
