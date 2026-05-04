package com.data_management;

import java.io.IOException;


/**
 * Defines a reader that can load patient data into DataStorage.
 * Implementations may read from a one-time source, such as files, or from a
 * continuous source, such as a WebSocket stream.
 */
public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Starts reading data from a continuous source.
     * Batch readers use the default behavior, which simply calls readData.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if the reader cannot start
     */
    default void startReading(DataStorage dataStorage) throws IOException {
        readData(dataStorage);
    }

    /**
     * Stops reading data from a continuous source.
     * Batch readers do not need cleanup, so the default implementation does nothing.
     *
     * @throws IOException if the reader cannot stop cleanly
     */
    default void stopReading() throws IOException {
        // No cleanup needed by default.
    }
}
