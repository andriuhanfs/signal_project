package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Tests patient-level record storage and inclusive time-range filtering.
 */
class PatientTest {

    @Test
    void testGetRecordsReturnsRecordsWithinInclusiveTimeRange() {
        Patient patient = new Patient(1);

        patient.addRecord(100.0, "ECG", 1000L);
        patient.addRecord(200.0, "ECG", 2000L);
        patient.addRecord(300.0, "ECG", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 2000L);

        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals(200.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testGetRecordsReturnsEmptyListWhenNoRecordsMatch() {
        Patient patient = new Patient(1);

        patient.addRecord(100.0, "ECG", 1000L);

        List<PatientRecord> records = patient.getRecords(2000L, 3000L);

        assertTrue(records.isEmpty());
    }

    @Test
    void testGetPatientIdReturnsCorrectId() {
        Patient patient = new Patient(42);

        assertEquals(42, patient.getPatientId());
    }
}
