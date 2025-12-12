package com.tibbo.aggregate.common.data;

import java.util.*;

import com.tibbo.aggregate.common.tests.*;

public class TestTimeZones extends CommonsTestCase
{
  public void testRawIds()
  {
    TimeZone tz = TimeZone.getTimeZone(TimeZones.getZoneDescSimple(3));
    
    assertEquals(3 * 60 * 60 * 1000, tz.getRawOffset());
    
    tz = TimeZone.getTimeZone(TimeZones.getZoneDescSimple(-12));
    
    assertEquals(-12 * 60 * 60 * 1000, tz.getRawOffset());
  }
}
