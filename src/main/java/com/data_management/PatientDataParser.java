package com.data_management;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses patient data messages emitted by the signal generator.
 * Supports both the readable output format and the compact WebSocket CSV format.
 */
public class PatientDataParser {

    public static final double ALERT_TRIGGERED_VALUE = 1.0;
    public static final double ALERT_RESOLVED_VALUE = 0.0;

    private static final Pattern READABLE_LINE_PATTERN = Pattern.compile(
            "^Patient ID:\\s*(\\d+),\\s*Timestamp:\\s*(\\d+),\\s*Label:\\s*([^,]+),\\s*Data:\\s*(.+)$");

    private PatientDataParser() {
    }

    /**
     * Parses a patient data message into a PatientRecord.
     *
     * Supported formats:
     * Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%
     * 1,1714376789050,Saturation,95%
     *
     * @param message raw message from file or WebSocket stream
     * @return parsed patient record
     * @throws IOException if the message format or data value is invalid
     */
    public static PatientRecord parse(String message) throws IOException {
        if (message == null || message.trim().isEmpty()) {
            throw new IOException("Patient data message is empty");
        }

        String trimmedMessage = message.trim();

        if (trimmedMessage.startsWith("Patient ID:")) {
            return parseReadableFormat(trimmedMessage);
        }

        return parseCsvFormat(trimmedMessage);
    }

    private static PatientRecord parseReadableFormat(String message) throws IOException {
        Matcher matcher = READABLE_LINE_PATTERN.matcher(message);

        if (!matcher.matches()) {
            throw new IOException("Invalid patient data message format: " + message);
        }

        int patientId = parsePatientId(matcher.group(1), message);
        long timestamp = parseTimestamp(matcher.group(2), message);
        String recordType = matcher.group(3).trim();
        double measurementValue = parseMeasurementValue(matcher.group(4).trim(), message);

        return new PatientRecord(patientId, measurementValue, recordType, timestamp);
    }

    private static PatientRecord parseCsvFormat(String message) throws IOException {
        String[] parts = message.split(",", 4);

        if (parts.length != 4) {
            throw new IOException("Invalid patient data CSV message format: " + message);
        }

        int patientId = parsePatientId(parts[0].trim(), message);
        long timestamp = parseTimestamp(parts[1].trim(), message);
        String recordType = parts[2].trim();
        double measurementValue = parseMeasurementValue(parts[3].trim(), message);

        return new PatientRecord(patientId, measurementValue, recordType, timestamp);
    }

    private static int parsePatientId(String rawPatientId, String message) throws IOException {
        try {
            return Integer.parseInt(rawPatientId);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid patient ID in message: " + message, exception);
        }
    }

    private static long parseTimestamp(String rawTimestamp, String message) throws IOException {
        try {
            return Long.parseLong(rawTimestamp);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid timestamp in message: " + message, exception);
        }
    }

    private static double parseMeasurementValue(String rawValue, String message) throws IOException {
        String normalizedValue = rawValue.trim();

        if (normalizedValue.endsWith("%")) {
            normalizedValue = normalizedValue.substring(0, normalizedValue.length() - 1).trim();
        }

        if (normalizedValue.equalsIgnoreCase("triggered")) {
            return ALERT_TRIGGERED_VALUE;
        }

        if (normalizedValue.equalsIgnoreCase("resolved") || normalizedValue.equalsIgnoreCase("untriggered")) {
            return ALERT_RESOLVED_VALUE;
        }

        try {
            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid measurement value in message: " + message, exception);
        }
    }
}
