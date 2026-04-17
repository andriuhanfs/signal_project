package com.cardio_generator;

import java.io.IOException;
import java.util.Arrays;

import com.data_management.DataStorage;

public class Main {
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
