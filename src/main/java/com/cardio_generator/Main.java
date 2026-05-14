package com.cardio_generator;

import java.io.IOException;
import java.util.Arrays;

import com.data_management.DataStorage;

/**
 * Application entry point that routes command-line execution to either the
 * simulator or the data storage demo.
 */
public class Main {
    /**
     * Runs DataStorage when the first argument is "DataStorage"; otherwise runs
     * HealthDataSimulator. The optional "HealthDataSimulator" argument is accepted
     * explicitly and removed before forwarding the remaining options.
     *
     * @param args command-line arguments selecting the component and options
     * @throws IOException if the selected component cannot initialize its output
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("DataStorage")) {
            DataStorage.main(Arrays.copyOfRange(args, 1, args.length));
        } else if (args.length > 0 && args[0].equalsIgnoreCase("HealthDataSimulator")) {
            HealthDataSimulator.main(Arrays.copyOfRange(args, 1, args.length));
        } else {
            HealthDataSimulator.main(args);
        }
    }
}
