package com.tibbo.aggregate.common.tests;

import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Базовый класс для тестов, предоставляющий общие методы инициализации и статические методы assert.
 * <p>
 * Этот класс предоставляет:
 * <ul>
 *   <li>Методы setUp() и tearDown() для инициализации тестового окружения</li>
 *   <li>Статические методы assert* для удобства использования в тестах</li>
 *   <li>Утилитные методы для работы с тестовыми данными</li>
 * </ul>
 * 
 * <p><b>Использование:</b>
 * <pre>{@code
 * public class MyTest extends CommonsTestCase {
 *     public void testSomething() {
 *         assertTrue(condition);
 *         assertEquals(expected, actual);
 *     }
 * }
 * }</pre>
 *
 * @since 1.3.7
 */
public class CommonsTestCase
{
  private CommonsFixture commonsFixture = new CommonsFixture();
  
  @BeforeEach
  protected void setUp() throws Exception
  {
    commonsFixture.setUp();
  }
  
  @AfterEach
  protected void tearDown() throws Exception
  {
    commonsFixture.tearDown();
    commonsFixture = null;
  }
  
  public CommonsFixture getCommonsFixture()
  {
    return commonsFixture;
  }

  public static Calendar getCalendar() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }
  
  // Статические методы assert для удобства использования в тестах
  // Делегируют вызовы в org.junit.jupiter.api.Assertions
  
  protected static void assertTrue(boolean condition) {
    org.junit.jupiter.api.Assertions.assertTrue(condition);
  }
  
  protected static void assertTrue(boolean condition, String message) {
    org.junit.jupiter.api.Assertions.assertTrue(condition, message);
  }
  
  protected static void assertFalse(boolean condition) {
    org.junit.jupiter.api.Assertions.assertFalse(condition);
  }
  
  protected static void assertFalse(boolean condition, String message) {
    org.junit.jupiter.api.Assertions.assertFalse(condition, message);
  }
  
  protected static void assertEquals(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }
  
  protected static void assertEquals(Object expected, Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }
  
  protected static void assertEquals(int expected, int actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }
  
  protected static void assertEquals(int expected, int actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }
  
  protected static void assertEquals(long expected, long actual) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
  }
  
  protected static void assertEquals(long expected, long actual, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
  }
  
  protected static void assertEquals(double expected, double actual, double delta) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, delta);
  }
  
  protected static void assertEquals(double expected, double actual, double delta, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, delta, message);
  }
  
  protected static void assertEquals(float expected, float actual, float delta) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, delta);
  }
  
  protected static void assertEquals(float expected, float actual, float delta, String message) {
    org.junit.jupiter.api.Assertions.assertEquals(expected, actual, delta, message);
  }
  
  protected static void assertNotNull(Object actual) {
    org.junit.jupiter.api.Assertions.assertNotNull(actual);
  }
  
  protected static void assertNotNull(Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotNull(actual, message);
  }
  
  protected static void assertNull(Object actual) {
    org.junit.jupiter.api.Assertions.assertNull(actual);
  }
  
  protected static void assertNull(Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertNull(actual, message);
  }
  
  protected static void assertSame(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertSame(expected, actual);
  }
  
  protected static void assertSame(Object expected, Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertSame(expected, actual, message);
  }
  
  protected static void assertNotSame(Object expected, Object actual) {
    org.junit.jupiter.api.Assertions.assertNotSame(expected, actual);
  }
  
  protected static void assertNotSame(Object expected, Object actual, String message) {
    org.junit.jupiter.api.Assertions.assertNotSame(expected, actual, message);
  }
  
  protected static void fail() {
    org.junit.jupiter.api.Assertions.fail();
  }
  
  protected static void fail(String message) {
    org.junit.jupiter.api.Assertions.fail(message);
  }
}
