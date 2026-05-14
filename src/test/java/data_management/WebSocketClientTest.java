package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClient;

class WebSocketClientTest {

    @Test
    void testOnMessageStoresValidReadableMessage() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"), storage);

        client.onMessage("Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%");

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);

        assertEquals(1, records.size());
        assertEquals("Saturation", records.get(0).getRecordType());
        assertEquals(95.0, records.get(0).getMeasurementValue());
        assertEquals(0, client.getMalformedMessageCount());
    }

    @Test
    void testOnMessageStoresValidCsvMessage() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"), storage);

        client.onMessage("2,1714376789051,ECG,0.42");

        List<PatientRecord> records = storage.getRecords(2, 0L, Long.MAX_VALUE);

        assertEquals(1, records.size());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(0.42, records.get(0).getMeasurementValue());
    }

    @Test
    void testMalformedMessageIsCountedAndDoesNotThrow() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"), storage);

        client.onMessage("bad-message");

        assertEquals(1, client.getMalformedMessageCount());
        assertNotNull(client.getLastError());
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testOnOpenAndOnCloseRecordConnectionState() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"), storage);

        client.onOpen(null);
        assertTrue(client.isConnectedToServer());

        client.onClose(1000, "normal close", true);

        assertFalse(client.isConnectedToServer());
        assertEquals(1000, client.getCloseCode());
        assertEquals("normal close", client.getCloseReason());
        assertTrue(client.isClosedRemotely());
    }

    @Test
    void testOnErrorStoresLastError() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketClient client = new WebSocketClient(URI.create("ws://localhost:8080"), storage);
        RuntimeException exception = new RuntimeException("network error");

        client.onError(exception);

        assertEquals(exception, client.getLastError());
    }
}
