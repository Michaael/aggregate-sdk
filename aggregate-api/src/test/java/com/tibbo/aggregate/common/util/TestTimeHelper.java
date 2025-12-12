package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TimeHelper}.
 */
@DisplayName("TimeHelper Tests")
class TestTimeHelper {

    @Test
    @DisplayName("Test time constants values")
    void testTimeConstants() {
        assertEquals(1000L, TimeHelper.SECOND_IN_MS);
        assertEquals(60000L, TimeHelper.MINUTE_IN_MS);
        assertEquals(3600000L, TimeHelper.HOUR_IN_MS);
        assertEquals(86400000L, TimeHelper.DAY_IN_MS);
        assertEquals(604800000L, TimeHelper.WEEK_IN_MS);
        assertEquals(2592000000L, TimeHelper.MONTH_IN_MS);
        assertEquals(7862400000L, TimeHelper.QUARTER_IN_MS);
        assertEquals(31536000000L, TimeHelper.YEAR_IN_MS);
        
        assertEquals(60L, TimeHelper.MINUTE_IN_SECONDS);
        assertEquals(3600L, TimeHelper.HOUR_IN_SECONDS);
        assertEquals(86400L, TimeHelper.DAY_IN_SECONDS);
        assertEquals(604800L, TimeHelper.WEEK_IN_SECONDS);
        assertEquals(2592000L, TimeHelper.MONTH_IN_SECONDS);
        assertEquals(7862400L, TimeHelper.QUARTER_IN_SECONDS);
        assertEquals(31536000L, TimeHelper.YEAR_IN_SECONDS);
    }

    @Test
    @DisplayName("Test unit constants")
    void testUnitConstants() {
        assertEquals(0, TimeHelper.MILLISECOND);
        assertEquals(1, TimeHelper.SECOND);
        assertEquals(2, TimeHelper.MINUTE);
        assertEquals(3, TimeHelper.HOUR);
        assertEquals(4, TimeHelper.DAY);
        assertEquals(5, TimeHelper.WEEK);
        assertEquals(6, TimeHelper.MONTH);
        assertEquals(7, TimeHelper.QUARTER);
        assertEquals(8, TimeHelper.YEAR);
    }

    @Test
    @DisplayName("Test getSelectionValues returns non-null map")
    void testGetSelectionValues() {
        Map<Object, String> values = TimeHelper.getSelectionValues();
        assertNotNull(values);
        assertTrue(values.size() > 0);
    }

    @Test
    @DisplayName("Test getUnits returns all time units")
    void testGetUnits() {
        List<TimeUnit> units = TimeHelper.getUnits();
        assertNotNull(units);
        assertEquals(9, units.size()); // MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, YEAR
    }

    @Test
    @DisplayName("Test getReversedUnits returns reversed list")
    void testGetReversedUnits() {
        List<TimeUnit> units = TimeHelper.getUnits();
        List<TimeUnit> reversedUnits = TimeHelper.getReversedUnits();
        
        assertNotNull(reversedUnits);
        assertEquals(units.size(), reversedUnits.size());
        
        // Check that the order is reversed
        for (int i = 0; i < units.size(); i++) {
            assertEquals(units.get(i), reversedUnits.get(units.size() - 1 - i));
        }
    }

    @Test
    @DisplayName("Test getUnitDescription for all units")
    void testGetUnitDescription() {
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.MILLISECOND));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.SECOND));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.MINUTE));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.HOUR));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.DAY));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.WEEK));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.MONTH));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.QUARTER));
        assertNotNull(TimeHelper.getUnitDescription(TimeHelper.YEAR));
    }

    @Test
    @DisplayName("Test getUnitDescriptionPlural for all units")
    void testGetUnitDescriptionPlural() {
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.MILLISECOND));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.SECOND));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.MINUTE));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.HOUR));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.DAY));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.WEEK));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.MONTH));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.QUARTER));
        assertNotNull(TimeHelper.getUnitDescriptionPlural(TimeHelper.YEAR));
    }

    @Test
    @DisplayName("Test getTimeUnit with valid unit IDs")
    void testGetTimeUnitWithValidIds() {
        TimeUnit unit = TimeHelper.getTimeUnit(TimeHelper.MILLISECOND);
        assertNotNull(unit);
        assertEquals(TimeHelper.MILLISECOND, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.SECOND);
        assertNotNull(unit);
        assertEquals(TimeHelper.SECOND, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.MINUTE);
        assertNotNull(unit);
        assertEquals(TimeHelper.MINUTE, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.HOUR);
        assertNotNull(unit);
        assertEquals(TimeHelper.HOUR, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.DAY);
        assertNotNull(unit);
        assertEquals(TimeHelper.DAY, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.WEEK);
        assertNotNull(unit);
        assertEquals(TimeHelper.WEEK, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.MONTH);
        assertNotNull(unit);
        assertEquals(TimeHelper.MONTH, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.QUARTER);
        assertNotNull(unit);
        assertEquals(TimeHelper.QUARTER, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.YEAR);
        assertNotNull(unit);
        assertEquals(TimeHelper.YEAR, unit.getUnit());
    }

    @Test
    @DisplayName("Test getTimeUnit with invalid unit ID throws exception")
    void testGetTimeUnitWithInvalidId() {
        assertThrows(IllegalStateException.class, () -> {
            TimeHelper.getTimeUnit(999);
        });
    }

    @Test
    @DisplayName("Test getTimeUnit with string names")
    void testGetTimeUnitWithStringNames() {
        // Test various name formats
        TimeUnit unit = TimeHelper.getTimeUnit(TimeHelper.NAME_SECOND);
        assertNotNull(unit);
        assertEquals(TimeHelper.SECOND, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_SEC);
        assertNotNull(unit);
        assertEquals(TimeHelper.SECOND, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_S);
        assertNotNull(unit);
        assertEquals(TimeHelper.SECOND, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_MINUTE);
        assertNotNull(unit);
        assertEquals(TimeHelper.MINUTE, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_MIN);
        assertNotNull(unit);
        assertEquals(TimeHelper.MINUTE, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_M);
        assertNotNull(unit);
        assertEquals(TimeHelper.MINUTE, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_HOUR);
        assertNotNull(unit);
        assertEquals(TimeHelper.HOUR, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_HR);
        assertNotNull(unit);
        assertEquals(TimeHelper.HOUR, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_H);
        assertNotNull(unit);
        assertEquals(TimeHelper.HOUR, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_DAY);
        assertNotNull(unit);
        assertEquals(TimeHelper.DAY, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_D);
        assertNotNull(unit);
        assertEquals(TimeHelper.DAY, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_WEEK);
        assertNotNull(unit);
        assertEquals(TimeHelper.WEEK, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_W);
        assertNotNull(unit);
        assertEquals(TimeHelper.WEEK, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_YEAR);
        assertNotNull(unit);
        assertEquals(TimeHelper.YEAR, unit.getUnit());
        
        unit = TimeHelper.getTimeUnit(TimeHelper.NAME_Y);
        assertNotNull(unit);
        assertEquals(TimeHelper.YEAR, unit.getUnit());
    }

    @Test
    @DisplayName("Test convertToMillis for all units")
    void testConvertToMillis() {
        long period = 5;
        
        // Test MILLISECOND
        assertEquals(5L, TimeHelper.convertToMillis(period, TimeHelper.MILLISECOND));
        
        // Test SECOND
        assertEquals(5000L, TimeHelper.convertToMillis(period, TimeHelper.SECOND));
        
        // Test MINUTE
        assertEquals(300000L, TimeHelper.convertToMillis(period, TimeHelper.MINUTE));
        
        // Test HOUR
        assertEquals(18000000L, TimeHelper.convertToMillis(period, TimeHelper.HOUR));
        
        // Test DAY
        assertEquals(432000000L, TimeHelper.convertToMillis(period, TimeHelper.DAY));
        
        // Test WEEK
        assertEquals(3024000000L, TimeHelper.convertToMillis(period, TimeHelper.WEEK));
        
        // Test MONTH
        assertEquals(12960000000L, TimeHelper.convertToMillis(period, TimeHelper.MONTH));
        
        // Test QUARTER
        assertEquals(39312000000L, TimeHelper.convertToMillis(period, TimeHelper.QUARTER));
        
        // Test YEAR
        assertEquals(157680000000L, TimeHelper.convertToMillis(period, TimeHelper.YEAR));
    }

    @Test
    @DisplayName("Test convertToMillis with zero period")
    void testConvertToMillisWithZero() {
        assertEquals(0L, TimeHelper.convertToMillis(0, TimeHelper.SECOND));
        assertEquals(0L, TimeHelper.convertToMillis(0, TimeHelper.MINUTE));
        assertEquals(0L, TimeHelper.convertToMillis(0, TimeHelper.HOUR));
    }

    @Test
    @DisplayName("Test convertToMillis with invalid unit throws exception")
    void testConvertToMillisWithInvalidUnit() {
        assertThrows(IllegalStateException.class, () -> {
            TimeHelper.convertToMillis(5, 999);
        });
    }

    @Test
    @DisplayName("Test getForSeconds with null")
    void testGetForSecondsWithNull() {
        String result = TimeHelper.getForSeconds(null);
        assertNotNull(result);
        assertTrue(result.contains("0"));
    }

    @Test
    @DisplayName("Test getForSeconds with valid milliseconds")
    void testGetForSecondsWithValidMilliseconds() {
        // Test 1 second
        String result = TimeHelper.getForSeconds(1000L);
        assertNotNull(result);
        assertTrue(result.contains("1"));
        
        // Test 5 seconds
        result = TimeHelper.getForSeconds(5000L);
        assertNotNull(result);
        assertTrue(result.contains("5"));
        
        // Test 60 seconds (1 minute)
        result = TimeHelper.getForSeconds(60000L);
        assertNotNull(result);
        assertTrue(result.contains("60"));
    }

    @Test
    @DisplayName("Test getForSeconds with zero")
    void testGetForSecondsWithZero() {
        String result = TimeHelper.getForSeconds(0L);
        assertNotNull(result);
        assertTrue(result.contains("0"));
    }
}

