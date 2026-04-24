package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.strategies.HeartRateStrategy;
import com.data_management.Patient;

class HeartRateStrategyTest {

    @Test
    void testHighHeartRateCreatesPriorityAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(101.0, "HeartRate", 1000L);

        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient, patient.getRecords(0L, 2000L));

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0) instanceof PriorityAlertDecorator);
        assertEquals("[HIGH PRIORITY] High heart rate", alerts.get(0).getCondition());
    }

    @Test
    void testLowHeartRateCreatesRepeatedAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(59.0, "HeartRate", 1000L);

        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient, patient.getRecords(0L, 2000L));

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0) instanceof RepeatedAlertDecorator);
        assertEquals("[REPEATED EVERY 60000ms] Low heart rate", alerts.get(0).getCondition());
    }

    @Test
    void testNormalHeartRateDoesNotCreateAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(80.0, "HeartRate", 1000L);

        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient, patient.getRecords(0L, 2000L));

        assertTrue(alerts.isEmpty());
    }
}
