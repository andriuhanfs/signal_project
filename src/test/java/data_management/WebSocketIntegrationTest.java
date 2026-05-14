package data_management;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.WebSocketDataReader;

class WebSocketIntegrationTest {

    @Test
    void testWebSocketReaderStoresStreamedDataAndSupportsAlertGeneration() throws Exception {
        TestWebSocketServer server = new TestWebSocketServer(availablePort());
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        WebSocketDataReader reader = null;

        try {
            server.start();
            assertTrue(server.awaitStarted(), "WebSocket test server did not start");

            reader = new WebSocketDataReader(URI.create("ws://localhost:" + server.getPort()), 2);
            reader.startReading(storage);
            assertTrue(server.awaitConnection(), "WebSocket reader did not connect");

            server.broadcast("Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 91%");

            assertTrue(waitForStoredRecord(storage), "Streamed patient record was not stored");

            Patient patient = storage.getAllPatients().get(0);
            AlertGenerator alertGenerator = new AlertGenerator(storage);
            alertGenerator.evaluateData(patient);

            List<Alert> alerts = alertGenerator.getAlerts();
            assertTrue(alerts.stream()
                    .anyMatch(alert -> alert.getCondition().equals("Low oxygen saturation")));
        } finally {
            if (reader != null) {
                reader.stopReading();
            }
            server.stop(1000);
        }
    }

    private int availablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private boolean waitForStoredRecord(DataStorage storage) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000L;

        while (System.currentTimeMillis() < deadline) {
            if (!storage.getRecords(1, 0L, Long.MAX_VALUE).isEmpty()) {
                return true;
            }
            Thread.sleep(25L);
        }

        return false;
    }

    private static class TestWebSocketServer extends WebSocketServer {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch connected = new CountDownLatch(1);

        TestWebSocketServer(int port) {
            super(new InetSocketAddress(port));
        }

        boolean awaitStarted() throws InterruptedException {
            return started.await(2, TimeUnit.SECONDS);
        }

        boolean awaitConnection() throws InterruptedException {
            return connected.await(2, TimeUnit.SECONDS);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            connected.countDown();
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
        }

        @Override
        public void onStart() {
            started.countDown();
        }
    }
}
