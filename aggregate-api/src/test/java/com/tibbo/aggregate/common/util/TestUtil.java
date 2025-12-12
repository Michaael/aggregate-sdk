package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Util}.
 */
@DisplayName("Util Tests")
class TestUtil {

    @Test
    @DisplayName("Test equals with both null")
    void testEqualsBothNull() {
        assertTrue(Util.equals(null, null));
    }

    @Test
    @DisplayName("Test equals with first null")
    void testEqualsFirstNull() {
        assertFalse(Util.equals(null, "test"));
    }

    @Test
    @DisplayName("Test equals with second null")
    void testEqualsSecondNull() {
        assertFalse(Util.equals("test", null));
    }

    @Test
    @DisplayName("Test equals with equal objects")
    void testEqualsEqualObjects() {
        assertTrue(Util.equals("test", "test"));
        assertTrue(Util.equals(123, 123));
        assertTrue(Util.equals(true, true));
    }

    @Test
    @DisplayName("Test equals with different objects")
    void testEqualsDifferentObjects() {
        assertFalse(Util.equals("test", "other"));
        assertFalse(Util.equals(123, 456));
        assertFalse(Util.equals(true, false));
    }

    @Test
    @DisplayName("Test getRootCause with single exception")
    void testGetRootCauseSingleException() {
        Exception ex = new Exception("test");
        Throwable rootCause = Util.getRootCause(ex);
        assertEquals(ex, rootCause);
    }

    @Test
    @DisplayName("Test getRootCause with nested exceptions")
    void testGetRootCauseNestedExceptions() {
        Exception root = new Exception("root");
        Exception middle = new Exception("middle", root);
        Exception top = new Exception("top", middle);
        
        Throwable rootCause = Util.getRootCause(top);
        assertEquals(root, rootCause);
    }

    @Test
    @DisplayName("Test getRootCause with null")
    void testGetRootCauseWithNull() {
        // getRootCause doesn't handle null, will throw NPE
        assertThrows(NullPointerException.class, () -> {
            Util.getRootCause(null);
        });
    }

    @Test
    @DisplayName("Test readStream with valid input")
    void testReadStreamWithValidInput() throws IOException {
        String content = "Hello, World!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        
        byte[] result = Util.readStream(inputStream);
        
        assertNotNull(result);
        assertEquals(content.length(), result.length);
        assertEquals(content, new String(result));
    }

    @Test
    @DisplayName("Test readStream with empty input")
    void testReadStreamWithEmptyInput() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        byte[] result = Util.readStream(inputStream);
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("Test convertToString with null and allowNull=true")
    void testConvertToStringNullAllowNull() {
        String result = Util.convertToString(null, false, true);
        assertNull(result);
    }

    @Test
    @DisplayName("Test convertToString with null and allowNull=false")
    void testConvertToStringNullNotAllowNull() {
        String result = Util.convertToString(null, false, false);
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Test convertToString with string")
    void testConvertToStringWithString() {
        String result = Util.convertToString("test", false, false);
        assertEquals("test", result);
    }

    @Test
    @DisplayName("Test convertToString with number")
    void testConvertToStringWithNumber() {
        String result = Util.convertToString(123, false, false);
        assertEquals("123", result);
    }

    @Test
    @DisplayName("Test convertToNumber with integer")
    void testConvertToNumberWithInteger() {
        Number result = Util.convertToNumber(123, false, false);
        assertNotNull(result);
        assertEquals(123, result.intValue());
    }

    @Test
    @DisplayName("Test convertToNumber with double")
    void testConvertToNumberWithDouble() {
        Number result = Util.convertToNumber(123.45, false, false);
        assertNotNull(result);
        assertEquals(123.45, result.doubleValue(), 0.001);
    }

    @Test
    @DisplayName("Test convertToNumber with string number")
    void testConvertToNumberWithStringNumber() {
        Number result = Util.convertToNumber("123", false, false);
        assertNotNull(result);
        assertEquals(123, result.intValue());
    }

    @Test
    @DisplayName("Test convertToNumber with null and allowNull=true")
    void testConvertToNumberNullAllowNull() {
        Number result = Util.convertToNumber(null, false, true);
        assertNull(result);
    }

    @Test
    @DisplayName("Test convertToNumber with null and allowNull=false")
    void testConvertToNumberNullNotAllowNull() {
        Number result = Util.convertToNumber(null, false, false);
        assertNotNull(result);
        assertEquals(0, result.intValue());
    }

    @Test
    @DisplayName("Test convertToNumber with boolean true")
    void testConvertToNumberWithBooleanTrue() {
        Number result = Util.convertToNumber(true, false, false);
        assertEquals(1, result.intValue());
    }

    @Test
    @DisplayName("Test convertToNumber with boolean false")
    void testConvertToNumberWithBooleanFalse() {
        Number result = Util.convertToNumber(false, false, false);
        assertEquals(0, result.intValue());
    }

    @Test
    @DisplayName("Test convertToDate with Date object")
    void testConvertToDateWithDate() {
        Date date = new Date(1234567890L);
        Date result = Util.convertToDate(date, false, false);
        assertEquals(date, result);
    }

    @Test
    @DisplayName("Test convertToDate with number")
    void testConvertToDateWithNumber() {
        Date result = Util.convertToDate(1234567890L, false, false);
        assertNotNull(result);
        assertEquals(1234567890L, result.getTime());
    }

    @Test
    @DisplayName("Test convertToDate with null and allowNull=true")
    void testConvertToDateNullAllowNull() {
        Date result = Util.convertToDate(null, false, true);
        assertNull(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with boolean true")
    void testConvertToBooleanWithBooleanTrue() {
        Boolean result = Util.convertToBoolean(true, false, false);
        assertTrue(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with boolean false")
    void testConvertToBooleanWithBooleanFalse() {
        Boolean result = Util.convertToBoolean(false, false, false);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with string 'true'")
    void testConvertToBooleanWithStringTrue() {
        Boolean result = Util.convertToBoolean("true", false, false);
        assertTrue(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with string 'false'")
    void testConvertToBooleanWithStringFalse() {
        Boolean result = Util.convertToBoolean("false", false, false);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with string '1'")
    void testConvertToBooleanWithStringOne() {
        Boolean result = Util.convertToBoolean("1", false, false);
        assertTrue(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with string '0'")
    void testConvertToBooleanWithStringZero() {
        Boolean result = Util.convertToBoolean("0", false, false);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with number 1")
    void testConvertToBooleanWithNumberOne() {
        Boolean result = Util.convertToBoolean(1, false, false);
        assertTrue(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with number 0")
    void testConvertToBooleanWithNumberZero() {
        Boolean result = Util.convertToBoolean(0, false, false);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test convertToBoolean with null and allowNull=true")
    void testConvertToBooleanNullAllowNull() {
        Boolean result = Util.convertToBoolean(null, false, true);
        assertNull(result);
    }

    @Test
    @DisplayName("Test isFloatingPoint with Float")
    void testIsFloatingPointWithFloat() {
        assertTrue(Util.isFloatingPoint(1.0f));
    }

    @Test
    @DisplayName("Test isFloatingPoint with Double")
    void testIsFloatingPointWithDouble() {
        assertTrue(Util.isFloatingPoint(1.0));
    }

    @Test
    @DisplayName("Test isFloatingPoint with Integer")
    void testIsFloatingPointWithInteger() {
        assertFalse(Util.isFloatingPoint(1));
    }

    @Test
    @DisplayName("Test isFloatingPoint with Long")
    void testIsFloatingPointWithLong() {
        assertFalse(Util.isFloatingPoint(1L));
    }

    @Test
    @DisplayName("Test getObjectDescription with null")
    void testGetObjectDescriptionWithNull() {
        String result = Util.getObjectDescription(null);
        assertEquals("null", result);
    }

    @Test
    @DisplayName("Test getObjectDescription with object")
    void testGetObjectDescriptionWithObject() {
        String result = Util.getObjectDescription("test");
        assertNotNull(result);
        assertTrue(result.contains("test"));
        assertTrue(result.contains("String"));
    }

    @Test
    @DisplayName("Test parseVersion with valid version string")
    void testParseVersionWithValidString() {
        int result = Util.parseVersion("1.23.45");
        // major * 10000 + minor * 100 + build
        // 1 * 10000 + 23 * 100 + 45 = 10000 + 2300 + 45 = 12345
        assertEquals(12345, result);
    }

    @Test
    @DisplayName("Test nameToDescription with camelCase")
    void testNameToDescriptionWithCamelCase() {
        String result = Util.nameToDescription("helloWorld");
        assertNotNull(result);
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("World"));
    }

    @Test
    @DisplayName("Test nameToDescription with underscores")
    void testNameToDescriptionWithUnderscores() {
        String result = Util.nameToDescription("hello_world");
        assertNotNull(result);
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("World"));
    }

    @Test
    @DisplayName("Test descriptionToName with valid description")
    void testDescriptionToName() {
        String result = Util.descriptionToName("Hello World");
        assertNotNull(result);
        // Should convert spaces to underscores and keep valid chars
        assertTrue(result.contains("_") || result.equals("HelloWorld"));
    }

    @Test
    @DisplayName("Test sortByValue with map")
    void testSortByValue() {
        Map<String, Integer> map = new HashMap<>();
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);
        
        Map<String, Integer> sorted = Util.sortByValue(map);
        
        assertNotNull(sorted);
        assertEquals(3, sorted.size());
        
        // Check order (should be sorted by value)
        // Since it's a LinkedHashMap, we can check insertion order
        Integer[] values = sorted.values().toArray(new Integer[0]);
        assertTrue(values[0] <= values[1]);
        assertTrue(values[1] <= values[2]);
    }
}

