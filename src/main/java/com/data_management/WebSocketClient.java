package com.data_management;

import java.io.IOException;
import java.net.URI;

import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocket client that receives real-time patient data messages and stores
 * them in DataStorage.
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    private final DataStorage dataStorage;

    private int malformedMessageCount;
    private Exception lastError;
    private boolean connectedToServer;
    private int closeCode;
    private String closeReason;
    private boolean closedRemotely;

    /**
     * Creates a client for the given WebSocket server URI.
     *
     * @param serverUri WebSocket server URI, for example ws://localhost:8080
     * @param dataStorage storage where parsed patient records will be saved
     */
    public WebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
        this.malformedMessageCount = 0;
        this.connectedToServer = false;
        this.closeCode = -1;
        this.closeReason = "";
        this.closedRemotely = false;
    }

    /**
     * Records that the WebSocket connection has opened.
     *
     * @param handshake server handshake details
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        connectedToServer = true;
    }

    /**
     * Parses an incoming patient data message and stores it.
     * Malformed messages are counted and saved as the latest error instead of
     * being thrown from the callback.
     *
     * @param message raw WebSocket message
     */
    @Override
    public void onMessage(String message) {
        try {
            PatientRecord record = PatientDataParser.parse(message);
            dataStorage.addPatientData(
                    record.getPatientId(),
                    record.getMeasurementValue(),
                    record.getRecordType(),
                    record.getTimestamp());
        } catch (IOException exception) {
            malformedMessageCount++;
            lastError = exception;
        }
    }

    /**
     * Records close information for diagnostics and tests.
     *
     * @param code close status code
     * @param reason close reason
     * @param remote true if the server closed the connection
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        connectedToServer = false;
        closeCode = code;
        closeReason = reason;
        closedRemotely = remote;
    }

    /**
     * Records the latest WebSocket error.
     *
     * @param exception error reported by the WebSocket client
     */
    @Override
    public void onError(Exception exception) {
        lastError = exception;
    }

    /**
     * Returns how many malformed messages were received.
     *
     * @return malformed message count
     */
    public int getMalformedMessageCount() {
        return malformedMessageCount;
    }

    /**
     * Returns the latest parsing or connection error.
     *
     * @return latest error, or null if no error has occurred
     */
    public Exception getLastError() {
        return lastError;
    }

    /**
     * Returns whether the client currently considers itself connected.
     *
     * @return true if connected
     */
    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    /**
     * Returns the last WebSocket close code.
     *
     * @return close code, or -1 if not closed yet
     */
    public int getCloseCode() {
        return closeCode;
    }

    /**
     * Returns the last WebSocket close reason.
     *
     * @return close reason
     */
    public String getCloseReason() {
        return closeReason;
    }

    /**
     * Returns whether the last close was initiated remotely.
     *
     * @return true if the server closed the connection
     */
    public boolean isClosedRemotely() {
        return closedRemotely;
    }
}
