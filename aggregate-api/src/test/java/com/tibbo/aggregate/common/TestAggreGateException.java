package com.tibbo.aggregate.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AggreGateException}.
 */
@DisplayName("AggreGateException Tests")
class TestAggreGateException {

    @Test
    @DisplayName("Test constructor with message only")
    void testConstructorWithMessage() {
        String message = "Test error message";
        AggreGateException exception = new AggreGateException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCode());
        assertNull(exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with message and details")
    void testConstructorWithMessageAndDetails() {
        String message = "Test error message";
        String details = "Detailed error information";
        AggreGateException exception = new AggreGateException(message, details);
        
        assertEquals(message, exception.getMessage());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with cause")
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        AggreGateException exception = new AggreGateException(cause);
        
        assertEquals(cause.getMessage(), exception.getMessage());
        assertSame(cause, exception.getCause());
        assertNull(exception.getCode());
        assertNull(exception.getDetails());
    }

    @Test
    @DisplayName("Test constructor with null cause message")
    void testConstructorWithNullCauseMessage() {
        Throwable cause = new RuntimeException();
        AggreGateException exception = new AggreGateException(cause);
        
        assertEquals(cause.toString(), exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with message and cause")
    void testConstructorWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        AggreGateException exception = new AggreGateException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
        assertNull(exception.getCode());
        assertNull(exception.getDetails());
    }

    @Test
    @DisplayName("Test constructor with null message and cause")
    void testConstructorWithNullMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        AggreGateException exception = new AggreGateException(null, cause);
        
        assertEquals(String.valueOf(cause), exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("Test constructor with message, cause and details")
    void testConstructorWithMessageCauseAndDetails() {
        String message = "Test error message";
        String details = "Detailed error information";
        Throwable cause = new RuntimeException("Root cause");
        AggreGateException exception = new AggreGateException(message, cause, details);
        
        assertEquals(message, exception.getMessage());
        assertEquals(details, exception.getDetails());
        assertSame(cause, exception.getCause());
        assertNull(exception.getCode());
    }

    @Test
    @DisplayName("Test setCode and getCode")
    void testSetCodeAndGetCode() {
        AggreGateException exception = new AggreGateException("Test");
        String code = "ERROR_CODE_001";
        
        exception.setCode(code);
        assertEquals(code, exception.getCode());
    }

    @Test
    @DisplayName("Test setCode with null")
    void testSetCodeWithNull() {
        AggreGateException exception = new AggreGateException("Test");
        exception.setCode("CODE");
        exception.setCode(null);
        
        assertNull(exception.getCode());
    }
}

