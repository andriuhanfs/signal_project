package com.cardio_generator.outputs;

/**
 * Defines the contract for output mechanisms used by the simulator.
 * Implementations decide how generated patient data is delivered, such as
 * printing it to the console, writing it to files, or streaming it over a network.
 */
public interface OutputStrategy {
    /**
     * Sends generated patient data through a specific output mechanism.
     *
     * @param patientId the identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated
     * @param label the type or category of the generated data
     * @param data the generated value to output
     */
    void output(int patientId, long timestamp, String label, String data);
}
