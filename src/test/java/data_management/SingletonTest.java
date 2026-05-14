package data_management;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

class SingletonTest {

    @Test
    void testDataStorageGetInstanceReturnsSameObject() {
        assertSame(DataStorage.getInstance(), DataStorage.getInstance());
    }

    @Test
    void testDataStorageConstructorIsPrivate() throws NoSuchMethodException {
        assertTrue(Modifier.isPrivate(DataStorage.class.getDeclaredConstructor().getModifiers()));
    }

    @Test
    void testDataStorageClearRemovesStoredPatients() {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        storage.addPatientData(1, 80.0, "HeartRate", 1000L);

        storage.clear();

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testHealthDataSimulatorGetInstanceReturnsSameObject() {
        assertSame(HealthDataSimulator.getInstance(), HealthDataSimulator.getInstance());
    }

    @Test
    void testHealthDataSimulatorConstructorIsPrivate() throws NoSuchMethodException {
        assertTrue(Modifier.isPrivate(HealthDataSimulator.class.getDeclaredConstructor().getModifiers()));
    }
}
