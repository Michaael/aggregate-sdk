package com.tibbo.aggregate.common.tests;

import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
}
