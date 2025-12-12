package com.tibbo.aggregate.common.util;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDateUtils
{
  
  @Test
  public void testGetStartOfHour()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), 0));
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getStartOfHour(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetEndOfHour()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), 59, 59))
        .plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getEndOfHour(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetStartOfDay()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getStartOfDay(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetEndOfDay()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))
        .plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getEndOfDay(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetStartOfWeek()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(0, 0));
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getStartOfWeek(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetEndOfWeek()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(DayOfWeek.SUNDAY), LocalTime.of(23, 59, 59))
        .plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getEndOfWeek(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetStartOfMonth()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.of(0, 0));
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getStartOfMonth(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetEndOfMonth()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.of(23, 59, 59))
        .plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getEndOfMonth(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetStartOfYear()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.of(0, 0));
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getStartOfYear(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
  
  @Test
  public void testGetEndOfYear()
  {
    LocalDateTime expected = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.of(23, 59, 59))
        .plus(999, ChronoField.MILLI_OF_DAY.getBaseUnit());
    LocalDateTime actual = LocalDateTime.ofInstant(DateUtils.getEndOfYear(new Date()).toInstant(), ZoneId.systemDefault());
    assertEquals(expected, actual);
  }
}
