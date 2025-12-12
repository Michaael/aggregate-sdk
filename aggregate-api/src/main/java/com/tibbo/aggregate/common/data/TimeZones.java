package com.tibbo.aggregate.common.data;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;

public class TimeZones
{
  
  public final static String DEFAULT_TIME_ZONE_ID = "GMT";
  
  private static final Map<String, String> ZONES = new LinkedHashMap<String, String>();
  private static final Map<Object, String> SELECTION_VALUES = new LinkedHashMap<Object, String>();
  
  static
  {
    String[] zoneIds = TimeZone.getAvailableIDs();
    
    for (int i = 0; i < zoneIds.length; i++)
    {
      ZONES.put(zoneIds[i], getZoneDesc(zoneIds[i]));
      SELECTION_VALUES.put(zoneIds[i], getZoneDesc(zoneIds[i]));
    }
    
    for (int i = -12; i <= 14; i++)
    {
      final String desc = getZoneDescSimple(i);
      ZONES.put(desc, desc);
      SELECTION_VALUES.put(desc, desc);
    }
  }
  
  public static Map<String, String> getTimeZones()
  {
    return ZONES;
  }
  
  private static String getZoneDesc(String zoneId)
  {
    TimeZone tz = TimeZone.getTimeZone(zoneId);
    
    int rawOffset = tz.getRawOffset();
    int hour = rawOffset / (60 * 60 * 1000);
    int min = Math.abs(rawOffset / (60 * 1000)) % 60;
    
    boolean hasDST = tz.useDaylightTime();
    
    DecimalFormat form = new DecimalFormat("00");
    
    return "GMT" + ((hour >= 0) ? "+" : "-") + form.format(Math.abs(hour)) + ":" + form.format(min) + ", " + zoneId + (hasDST ? ", with DST" : "");
  }
  
  static String getZoneDescSimple(int offset)
  {
    DecimalFormat form = new DecimalFormat("00");
    
    return "GMT" + ((offset >= 0) ? "+" : "-") + form.format(Math.abs(offset)) + ":" + form.format(0);
  }
  
  public static Map<Object, String> getSelectionValues(boolean allowNotSelected)
  {
    Map<Object, String> sv = new LinkedHashMap();
    if (allowNotSelected)
    {
      sv.put(null, Cres.get().getString("notSelected"));
    }
    sv.putAll(SELECTION_VALUES);
    return sv;
  }
  
  public static final String getDefaultTimezoneId()
  {
    return Arrays.asList(TimeZone.getAvailableIDs()).contains(TimeZone.getDefault().getID()) ? TimeZone.getDefault().getID() : DEFAULT_TIME_ZONE_ID;
  }
}
