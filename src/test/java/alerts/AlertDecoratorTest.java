package alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BasicAlert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;

class AlertDecoratorTest {

    @Test
    void testPriorityAlertDecoratorPreservesAlertDataAndAddsPriorityTag() {
        Alert alert = new PriorityAlertDecorator(new BasicAlert("1", "High heart rate", 1000L));

        assertEquals("1", alert.getPatientId());
        assertEquals("[HIGH PRIORITY] High heart rate", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void testRepeatedAlertDecoratorPreservesAlertDataAndAddsRepeatTag() {
        Alert alert = new RepeatedAlertDecorator(new BasicAlert("2", "Low heart rate", 2000L), 60000L);

        assertEquals("2", alert.getPatientId());
        assertEquals("[REPEATED EVERY 60000ms] Low heart rate", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
    }
}
