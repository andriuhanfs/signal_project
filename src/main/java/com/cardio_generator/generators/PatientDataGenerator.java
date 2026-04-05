package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the contract for classes that generate simulated data for a patient.
 * Implementations produce a specific type of health-related output and send it
 * through the configured output strategy.
 */
public interface PatientDataGenerator {
    /**
     * Generates simulated data for the given patient and forwards the result
     * to the provided output strategy.
     *
     * @param patientId the identifier of the patient for whom data is generated
     * @param outputStrategy the output strategy that receives the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
