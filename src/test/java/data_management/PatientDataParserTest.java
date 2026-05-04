package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.data_management.PatientDataParser;
import com.data_management.PatientRecord;

class PatientDataParserTest {

    @Test
    void testParsesReadableFormat() throws IOException {
        PatientRecord record = PatientDataParser.parse(
                "Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%");

        assertEquals(1, record.getPatientId());
        assertEquals(1714376789050L, record.getTimestamp());
        assertEquals("Saturation", record.getRecordType());
        assertEquals(95.0, record.getMeasurementValue());
    }

    @Test
    void testParsesCsvFormat() throws IOException {
        PatientRecord record = PatientDataParser.parse("2,1714376789051,ECG,0.42");

        assertEquals(2, record.getPatientId());
        assertEquals(1714376789051L, record.getTimestamp());
        assertEquals("ECG", record.getRecordType());
        assertEquals(0.42, record.getMeasurementValue());
    }

    @Test
    void testParsesTriggeredAlertValue() throws IOException {
        PatientRecord record = PatientDataParser.parse("1,1714376789050,Alert,triggered");

        assertEquals(PatientDataParser.ALERT_TRIGGERED_VALUE, record.getMeasurementValue());
    }

    @Test
    void testParsesResolvedAlertValue() throws IOException {
        PatientRecord record = PatientDataParser.parse("1,1714376789050,Alert,resolved");

        assertEquals(PatientDataParser.ALERT_RESOLVED_VALUE, record.getMeasurementValue());
    }

    @Test
    void testRejectsInvalidFormat() {
        assertThrows(IOException.class, () -> PatientDataParser.parse("Patient 1, Saturation, 95%"));
    }

    @Test
    void testRejectsInvalidNumericValue() {
        assertThrows(IOException.class, () -> PatientDataParser.parse("1,1714376789050,Saturation,not-a-number"));
    }

    @Test
    void testRejectsEmptyMessage() {
        assertThrows(IOException.class, () -> PatientDataParser.parse("   "));
    }
}
