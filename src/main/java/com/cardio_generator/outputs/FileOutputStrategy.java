package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes generated patient data to text files grouped by data label.
 * This output strategy creates the base output directory if needed and
 * appends each generated record to the file associated with its label.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Renamed variable to lowerCamelCase 
    private String baseDirectory;

    // Renamed variable to lowerCamelCase and made it privte because it is internal state of this calss.
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a file-based output strategy that stores generated data in the
     * specified base directory.
     *
     * @param baseDirectory the directory in which output files should be created
     */
    public FileOutputStrategy(String baseDirectory) {

        // Renamed to lowerCamelCase
        this.baseDirectory = baseDirectory;
    }

    /**
     * Appends a generated patient record to the file corresponding to the
     * given data label.
     *
     * @param patientId the identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated
     * @param label the type or category of the generated data, also used as the file name prefix
     * @param data the generated value to write to the file
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Rename to lowerCamelCase to match the corrected variables
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}