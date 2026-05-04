package com.cardio_generator.outputs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class WebSocketOutputStrategyTest {

    @Test
    void testFormatMessageUsesReadablePatientDataFormat() {
        String message = WebSocketOutputStrategy.formatMessage(1, 1714376789050L, "Saturation", "95%");

        assertEquals(
                "Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 95%",
                message);
    }
}
