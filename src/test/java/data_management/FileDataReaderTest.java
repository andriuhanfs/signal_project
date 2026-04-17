package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

class FileDataReaderTest {
    @TempDir
    Path tempDirectory;

    @Test
    void testReadDataParsesSimulatorOutputFiles() throws IOException {

        writeDataFile("Saturation.txt",
                "Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%",
                "Patient ID: 2, Timestamp: 1714376789051, Label: Saturation, Data: 91%");

        writeDataFile("ECG.txt",
                "Patient ID: 1, Timestamp: 1714376789052, Label: ECG, Data: 0.42");

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDirectory);

        reader.readData(storage);

        List<PatientRecord> patientOneRecords = storage.getRecords(1, 0L, Long.MAX_VALUE);
        List<PatientRecord> patientTwoRecords = storage.getRecords(2, 0L, Long.MAX_VALUE);

        assertEquals(2, patientOneRecords.size());
        assertEquals(95.0, getMeasurementValue(patientOneRecords, "Saturation"));
        assertEquals(0.42, getMeasurementValue(patientOneRecords, "ECG"));
        assertEquals(1, patientTwoRecords.size());
        assertEquals(91.0, patientTwoRecords.get(0).getMeasurementValue());
    }

    @Test
    void testReadDataConvertsAlertTextValues() throws IOException {

        writeDataFile("Alert.txt",
                "Patient ID: 1, Timestamp: 1714376789050, Label: Alert, Data: triggered",
                "Patient ID: 1, Timestamp: 1714376789051, Label: Alert, Data: resolved");

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDirectory);

        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);

        assertEquals(2, records.size());
        assertEquals(FileDataReader.ALERT_TRIGGERED_VALUE, records.get(0).getMeasurementValue());
        assertEquals(FileDataReader.ALERT_RESOLVED_VALUE, records.get(1).getMeasurementValue());
    }

    @Test
    void testReadDataRejectsInvalidLineFormat() throws IOException {

        writeDataFile("Saturation.txt", "Patient 1, Saturation, 95%");

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDirectory);

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testReadDataRejectsNonNumericValues() throws IOException {

        writeDataFile("Saturation.txt",
                "Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: not-a-number");

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDirectory);

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testReadDataRejectsNonDirectoryPath() throws IOException {
        
        Path filePath = tempDirectory.resolve("Saturation.txt");
        Files.write(filePath, List.of("Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%"));

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(filePath);

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    private void writeDataFile(String fileName, String... lines) throws IOException {
        Files.write(tempDirectory.resolve(fileName), List.of(lines));
    }

    private double getMeasurementValue(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> record.getRecordType().equals(recordType))
                .findFirst()
                .orElseThrow()
                .getMeasurementValue();
    }
}
