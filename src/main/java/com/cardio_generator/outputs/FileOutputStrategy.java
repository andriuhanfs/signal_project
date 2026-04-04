package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// Renamed class to UpperCamelCase 
public class FileOutputStrategy implements OutputStrategy {

    // Renamed variable to lowerCamelCase 
    private String baseDirectory;

    // Renamed variable to lowerCamelCase and made it privte because it is internal state of this calss.
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    // Renamed the constructor to UpperCamelCase to match the class name
    public FileOutputStrategy(String baseDirectory) {

        // Renamed to lowerCamelCase
        this.baseDirectory = baseDirectory;
    }

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