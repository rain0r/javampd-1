package org.bff.javampd.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorEventTest {
    @Test
    void getMessage() {
        String message = "message";
        ErrorEvent errorEvent = new ErrorEvent(this, message);

        assertEquals(errorEvent.getMessage(), message);
    }

    @Test
    void getSource() {
        Object source = new Object();
        ErrorEvent errorEvent = new ErrorEvent(source);

        assertEquals(errorEvent.getSource(), source);
    }

    @Test
    void getSourceAndMessage() {
        Object source = new Object();
        String message = "message";
        ErrorEvent errorEvent = new ErrorEvent(source, message);

        assertEquals(errorEvent.getSource(), source);
        assertEquals(errorEvent.getMessage(), message);
    }
}
