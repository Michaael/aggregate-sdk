package com.tibbo.aggregate.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AggreGateRuntimeException}.
 */
@DisplayName("AggreGateRuntimeException Tests")
class TestAggreGateRuntimeException {

    @Test
    @DisplayName("Test constructor with message only")
    void testConstructorWithMessage() {
        String message = "Test runtime error message";
        AggreGateRuntimeException exception = new AggreGateRuntimeException(message);
        
        assertEquals(message, exception.getMessage());
        assertSame(null, exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with message and cause")
    void testConstructorWithMessageAndCause() {
        String message = "Test runtime error message";
        Throwable cause = new RuntimeException("Root cause");
        AggreGateRuntimeException exception = new AggreGateRuntimeException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with cause only")
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        AggreGateRuntimeException exception = new AggreGateRuntimeException(cause);
        
        assertEquals(cause.getClass().getName() + ": " + cause.getMessage(), exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with null cause")
    void testConstructorWithNullCause() {
        AggreGateRuntimeException exception = new AggreGateRuntimeException((Throwable) null);
        
        assertEquals("null", exception.getMessage());
        assertSame(null, exception.getCause());
    }
}

