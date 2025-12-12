package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.tests.*;

public class TestTimeUnitsManager extends CommonsTestCase
{
  public void testCreateTimeString()
  {
    TimeUnitsManager tum = new TimeUnitsManager(LongFieldFormat.encodePeriodEditorOptions(TimeHelper.SECOND, TimeHelper.HOUR));
    long period = 111 * TimeHelper.HOUR_IN_MS + 22 * TimeHelper.MINUTE_IN_MS + 33 * TimeHelper.SECOND_IN_MS + 444;
    String expected = "111 " + Cres.get().getString("tuHours") + " 22 " + Cres.get().getString("tuMinutes") + " 33 " + Cres.get().getString("tuSeconds");
    assertEquals(expected, tum.createTimeString(period));
    
    tum = new TimeUnitsManager(LongFieldFormat.encodePeriodEditorOptions(TimeHelper.MILLISECOND, TimeHelper.YEAR));
    period = 1 * TimeHelper.YEAR_IN_MS + 1 * TimeHelper.QUARTER_IN_MS + 1 * TimeHelper.MONTH_IN_MS;
    expected = "1 " + Cres.get().getString("tuYears") + " 4 " + Cres.get().getString("tuMonths") + " 1 " + Cres.get().getString("tuDays");
    assertEquals(expected, tum.createTimeString(period));
    
  }
}
