package com.data_management;

import java.io.IOException;
import java.net.URI;

/**
 * DataReader implementation that connects to a WebSocket server and streams
 * patient data into DataStorage.
 */
public class WebSocketDataReader implements DataReader {
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 5;

    private final URI serverUri;
    private final int connectTimeoutSeconds;

    private WebSocketClient client;

    /**
     * Creates a WebSocket data reader from a URL string.
     *
     * @param serverUrl WebSocket server URL, for example ws://localhost:8080
     */
    public WebSocketDataReader(String serverUrl) {
        this(URI.create(serverUrl));
    }

    /**
     * Creates a WebSocket data reader from a URI.
     *
     * @param serverUri WebSocket server URI, for example ws://localhost:8080
     */
    public WebSocketDataReader(URI serverUri) {
        this(serverUri, DEFAULT_CONNECT_TIMEOUT_SECONDS);
    }

    /**
     * Creates a WebSocket data reader with a custom connection timeout.
     *
     * @param serverUri WebSocket server URI
     * @param connectTimeoutSeconds timeout for opening the connection
     */
    public WebSocketDataReader(URI serverUri, int connectTimeoutSeconds) {
        this.serverUri = serverUri;
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    /**
     * Starts the WebSocket stream.
     * This keeps DataReader compatibility by delegating to startReading.
     *
     * @param dataStorage the storage where streamed data will be stored
     * @throws IOException if the WebSocket connection cannot be opened
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        startReading(dataStorage);
    }

    /**
     * Creates and connects the WebSocket client.
     *
     * @param dataStorage the storage where streamed data will be stored
     * @throws IOException if the connection cannot be opened in time
     */
    @Override
    public void startReading(DataStorage dataStorage) throws IOException {
        client = new WebSocketClient(serverUri, dataStorage);

        try {
            boolean connected = client.connectBlocking(connectTimeoutSeconds, java.util.concurrent.TimeUnit.SECONDS);

            if (!connected) {
                throw new IOException("Timed out connecting to WebSocket server: " + serverUri);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while connecting to WebSocket server: " + serverUri, exception);
        }
    }

    /**
     * Closes the active WebSocket client connection.
     *
     * @throws IOException if the close operation is interrupted
     */
    @Override
    public void stopReading() throws IOException {
        if (client == null) {
            return;
        }

        try {
            client.closeBlocking();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while closing WebSocket connection: " + serverUri, exception);
        }
    }

    /**
     * Returns the active WebSocket client.
     * This is mainly useful for diagnostics and tests.
     *
     * @return current WebSocket client, or null if not started
     */
    public WebSocketClient getClient() {
        return client;
    }

    /**
     * Returns the WebSocket server URI.
     *
     * @return WebSocket server URI
     */
    public URI getServerUri() {
        return serverUri;
    }
}
