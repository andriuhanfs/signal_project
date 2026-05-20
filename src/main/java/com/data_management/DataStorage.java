package com.data_management;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alerts.Alert;
import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static final DataStorage INSTANCE = new DataStorage();

    private final Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    /**
     * Returns the shared DataStorage instance.
     *
     * @return the singleton DataStorage instance
     */
    public static DataStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Constructs the singleton DataStorage instance.
     */
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public synchronized void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Removes all stored patient data.
     * This is useful for resetting the singleton between tests or simulations.
     */
    public synchronized void clear() {
        patientMap.clear();
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public synchronized List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public synchronized List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for loading simulator file output into storage and evaluating
     * stored patient records for alerts.
     * 
     * @param args command-line arguments; pass an output directory path, optionally
     *             prefixed with {@code file:}, or use {@code --input <directory>}
     * @throws IOException if the provided output directory cannot be read
     */
    public static void main(String[] args) throws IOException {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();

        if (args.length == 0) {
            printUsage();
            return;
        }

        Path outputDirectory = resolveOutputDirectory(args);
        FileDataReader reader = new FileDataReader(outputDirectory);
        reader.readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);

        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        List<Alert> alerts = alertGenerator.getAlerts();
        System.out.println("Loaded " + storage.getAllPatients().size() + " patient(s) from " + outputDirectory + ".");
        System.out.println("Generated " + alerts.size() + " alert(s).");

        for (Alert alert : alerts) {
            System.out.println("Alert for patient " + alert.getPatientId()
                    + ": " + alert.getCondition()
                    + " at " + alert.getTimestamp());
        }
    }

    private static Path resolveOutputDirectory(String[] args) throws IOException {
        String rawPath;

        if (args[0].equalsIgnoreCase("--input") || args[0].equalsIgnoreCase("--output")) {
            if (args.length < 2) {
                throw new IOException("Missing output directory after " + args[0]);
            }
            rawPath = args[1];
        } else {
            rawPath = args[0];
        }

        if (rawPath.startsWith("file:")) {
            rawPath = rawPath.substring("file:".length());
        }

        return Paths.get(rawPath);
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar target/cardio_generator-1.0-SNAPSHOT.jar DataStorage <output-directory>");
        System.out.println("Example: java -jar target/cardio_generator-1.0-SNAPSHOT.jar DataStorage file:./output");
    }
}
