package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients.
 * This generator tracks whether an alert is currently active for each patient
 * and randomly triggers or resolves alerts over time.
 */
public class AlertGenerator implements PatientDataGenerator {

    // Changed to private because this helper field is internal to the class
    private static final Random randomGenerator = new Random(); 
    // Renamed to lowerCamelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates an alert generator and initializes the alert state storage for
     * all patients.
     *
     * @param patientCount the total number of patients for which alert states are tracked
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates one alert update for the specified patient and sends it
     * through the provided output strategy.
     *
     * @param patientId the identifier of the patient for whom alert data is generated
     * @param outputStrategy the output strategy that receives the generated alert event
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Renamed the variabke to lowerCamelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
