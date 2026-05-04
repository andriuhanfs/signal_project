package data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.WebSocketDataReader;

class WebSocketDataReaderTest {

    @Test
    void testStringConstructorStoresServerUri() {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080");

        assertEquals(URI.create("ws://localhost:8080"), reader.getServerUri());
    }

    @Test
    void testUriConstructorStoresServerUri() {
        URI uri = URI.create("ws://localhost:8081");

        WebSocketDataReader reader = new WebSocketDataReader(uri);

        assertEquals(uri, reader.getServerUri());
    }

    @Test
    void testStopReadingBeforeStartDoesNothing() throws IOException {
        WebSocketDataReader reader = new WebSocketDataReader("ws://localhost:8080");

        reader.stopReading();
    }

    @Test
    void testReadDataFailsClearlyWhenServerUnavailable() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:1"), 1);
        DataStorage storage = new DataStorage();

        assertThrows(IOException.class, () -> reader.readData(storage));
    }
}
