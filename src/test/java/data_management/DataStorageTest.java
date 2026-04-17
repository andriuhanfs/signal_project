package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Tests the storage and retrieval behavior of the DataStorage repository.
 */
class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);

        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
        assertEquals(200.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testGetRecordsFiltersByTimeRange() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "ECG", 1000L);
        storage.addPatientData(1, 200.0, "ECG", 2000L);
        storage.addPatientData(1, 300.0, "ECG", 3000L);

        List<PatientRecord> records = storage.getRecords(1, 1500L, 2500L);

        assertEquals(1, records.size());
        assertEquals(200.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsReturnsEmptyListForUnknownPatient() {
        DataStorage storage = new DataStorage();

        List<PatientRecord> records = storage.getRecords(99, 1000L, 2000L);

        assertTrue(records.isEmpty());
    }

    @Test
    void testGetRecordsIncludesStartAndEndTimes() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "Saturation", 1000L);
        storage.addPatientData(1, 200.0, "Saturation", 2000L);

        List<PatientRecord> records = storage.getRecords(1, 1000L, 2000L);

        assertEquals(2, records.size());
    }

    @Test
    void testRecordsAreSeparatedByPatient() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "ECG", 1000L);
        storage.addPatientData(2, 200.0, "ECG", 1000L);

        List<PatientRecord> patientOneRecords = storage.getRecords(1, 0L, 2000L);
        List<PatientRecord> patientTwoRecords = storage.getRecords(2, 0L, 2000L);

        assertEquals(1, patientOneRecords.size());
        assertEquals(1, patientTwoRecords.size());
        assertEquals(100.0, patientOneRecords.get(0).getMeasurementValue());
        assertEquals(200.0, patientTwoRecords.get(0).getMeasurementValue());
    }

    @Test
    void testGetAllPatientsReturnsStoredPatients() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "ECG", 1000L);
        storage.addPatientData(2, 200.0, "Saturation", 2000L);

        List<Patient> patients = storage.getAllPatients();

        assertEquals(2, patients.size());
        assertTrue(patients.stream().anyMatch(patient -> patient.getPatientId() == 1));
        assertTrue(patients.stream().anyMatch(patient -> patient.getPatientId() == 2));
    }

    @Test
    void testGetAllPatientsReturnsCopy() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "ECG", 1000L);

        List<Patient> patients = storage.getAllPatients();
        patients.clear();

        assertEquals(1, storage.getAllPatients().size());
    }
}
