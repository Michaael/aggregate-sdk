package com.tibbo.aggregate.common.util;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.tibbo.aggregate.common.*;

public class TimeHelper
{
  public static final long SECOND_IN_MS = 1000;
  public static final long MINUTE_IN_MS = SECOND_IN_MS * 60;
  public static final long HOUR_IN_MS = MINUTE_IN_MS * 60;
  public static final long DAY_IN_MS = HOUR_IN_MS * 24;
  public static final long WEEK_IN_MS = DAY_IN_MS * 7;
  public static final long MONTH_IN_MS = DAY_IN_MS * 30;
  public static final long QUARTER_IN_MS = DAY_IN_MS * 91;
  public static final long YEAR_IN_MS = DAY_IN_MS * 365;
  
  public static final long MINUTE_IN_SECONDS = 60;
  public static final long HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;
  public static final long DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
  public static final long WEEK_IN_SECONDS = DAY_IN_SECONDS * 7;
  public static final long MONTH_IN_SECONDS = DAY_IN_SECONDS * 30;
  public static final long QUARTER_IN_SECONDS = DAY_IN_SECONDS * 91;
  public static final long YEAR_IN_SECONDS = DAY_IN_SECONDS * 365;
  
  public static final int MILLISECOND = 0;
  public static final int SECOND = 1;
  public static final int MINUTE = 2;
  public static final int HOUR = 3;
  public static final int DAY = 4;
  public static final int WEEK = 5;
  public static final int MONTH = 6;
  public static final int QUARTER = 7;
  public static final int YEAR = 8;
  
  public static final String NAME_MILLISECOND = "millisecond";
  public static final String NAME_MS = "ms";
  public static final String NAME_SECOND = "second";
  public static final String NAME_SEC = "sec";
  public static final String NAME_S = "s";
  public static final String NAME_MINUTE = "minute";
  public static final String NAME_MIN = "min";
  public static final String NAME_M = "m";
  public static final String NAME_HOUR = "hour";
  public static final String NAME_HR = "hr";
  public static final String NAME_H = "h";
  public static final String NAME_DAY = "day";
  public static final String NAME_D = "d";
  public static final String NAME_WEEK = "week";
  public static final String NAME_W = "w";
  public static final String NAME_MONTH = "month";
  public static final String NAME_YEAR = "year";
  public static final String NAME_Y = "y";
  
  public static final TimeUnit MILLISECOND_UNIT = new TimeUnit(MILLISECOND, 1l,
      Cres.get().getString("tuMilliseconds"), Calendar.MILLISECOND, false, ChronoUnit.MILLIS);
  public static final TimeUnit SECOND_UNIT = new TimeUnit(SECOND, SECOND_IN_MS,
      Cres.get().getString("tuSeconds"), Calendar.SECOND, false, ChronoUnit.SECONDS);
  public static final TimeUnit MINUTE_UNIT = new TimeUnit(MINUTE, MINUTE_IN_MS,
      Cres.get().getString("tuMinutes"), Calendar.MINUTE, false, ChronoUnit.MINUTES);
  public static final TimeUnit HOUR_UNIT = new TimeUnit(HOUR, HOUR_IN_MS,
      Cres.get().getString("tuHours"), Calendar.HOUR_OF_DAY, false, ChronoUnit.HOURS);
  public static final TimeUnit DAY_UNIT = new TimeUnit(DAY, DAY_IN_MS,
      Cres.get().getString("tuDays"), Calendar.DAY_OF_MONTH, false, ChronoUnit.DAYS);
  public static final TimeUnit WEEK_UNIT = new TimeUnit(WEEK, WEEK_IN_MS,
      Cres.get().getString("tuWeeks"), Calendar.WEEK_OF_YEAR, true, ChronoUnit.WEEKS);
  public static final TimeUnit MONTH_UNIT = new TimeUnit(MONTH, MONTH_IN_MS,
      Cres.get().getString("tuMonths"), Calendar.MONTH, false, ChronoUnit.MONTHS);
  public static final TimeUnit QUARTER_UNIT = new TimeUnit(QUARTER, QUARTER_IN_MS,
      Cres.get().getString("tuQuarters"), null, true);
  public static final TimeUnit YEAR_UNIT = new TimeUnit(YEAR, YEAR_IN_MS,
      Cres.get().getString("tuYears"), Calendar.YEAR, false, ChronoUnit.YEARS);
  
  private static final Map<Object, String> SELECTION_VALUES = new LinkedHashMap();
  private static final List<TimeUnit> UNITS;
  private static List<TimeUnit> REVERSED_UNITS;
  private static Map<String, TimeUnit> NAMED_UNITS = new HashMap();
  
  static
  {
    SELECTION_VALUES.put(MILLISECOND, Cres.get().getString("tuMillisecond"));
    SELECTION_VALUES.put(SECOND, Cres.get().getString("tuSecond"));
    SELECTION_VALUES.put(MINUTE, Cres.get().getString("tuMinute"));
    SELECTION_VALUES.put(HOUR, Cres.get().getString("tuHour"));
    SELECTION_VALUES.put(DAY, Cres.get().getString("tuDay"));
    SELECTION_VALUES.put(WEEK, Cres.get().getString("tuWeek"));
    SELECTION_VALUES.put(MONTH, Cres.get().getString("tuMonth"));
    SELECTION_VALUES.put(QUARTER, Cres.get().getString("tuQuarter"));
    SELECTION_VALUES.put(YEAR, Cres.get().getString("tuYear"));
    
    // Оптимизация: используем ArrayList вместо LinkedList
    // Предварительно резервируем размер для 9 единиц времени
    UNITS = new ArrayList<TimeUnit>(9);
    UNITS.add(MILLISECOND_UNIT);
    UNITS.add(SECOND_UNIT);
    UNITS.add(MINUTE_UNIT);
    UNITS.add(HOUR_UNIT);
    UNITS.add(DAY_UNIT);
    UNITS.add(WEEK_UNIT);
    UNITS.add(MONTH_UNIT);
    UNITS.add(QUARTER_UNIT);
    UNITS.add(YEAR_UNIT);
    
    // Оптимизация: используем ArrayList вместо LinkedList
    REVERSED_UNITS = new ArrayList<TimeUnit>(TimeHelper.getUnits());
    Collections.reverse(REVERSED_UNITS);
    
    NAMED_UNITS.put(NAME_MILLISECOND, MILLISECOND_UNIT);
    NAMED_UNITS.put(NAME_MS, MILLISECOND_UNIT);
    NAMED_UNITS.put(NAME_SECOND, SECOND_UNIT);
    NAMED_UNITS.put(NAME_SEC, SECOND_UNIT);
    NAMED_UNITS.put(NAME_S, SECOND_UNIT);
    NAMED_UNITS.put(NAME_MINUTE, MINUTE_UNIT);
    NAMED_UNITS.put(NAME_MIN, MINUTE_UNIT);
    NAMED_UNITS.put(NAME_M, MINUTE_UNIT);
    NAMED_UNITS.put(NAME_HOUR, HOUR_UNIT);
    NAMED_UNITS.put(NAME_HR, HOUR_UNIT);
    NAMED_UNITS.put(NAME_H, HOUR_UNIT);
    NAMED_UNITS.put(NAME_DAY, DAY_UNIT);
    NAMED_UNITS.put(NAME_D, DAY_UNIT);
    NAMED_UNITS.put(NAME_WEEK, WEEK_UNIT);
    NAMED_UNITS.put(NAME_W, WEEK_UNIT);
    NAMED_UNITS.put(NAME_MONTH, MONTH_UNIT);
    NAMED_UNITS.put(NAME_YEAR, YEAR_UNIT);
    NAMED_UNITS.put(NAME_Y, YEAR_UNIT);
  }
  
  public static Map<Object, String> getSelectionValues()
  {
    return SELECTION_VALUES;
  }
  
  public static List<TimeUnit> getUnits()
  {
    return UNITS;
  }
  
  public static List<TimeUnit> getReversedUnits()
  {
    return REVERSED_UNITS;
  }
  
  public static String getUnitDescription(int unit)
  {
    return SELECTION_VALUES.get(unit);
  }
  
  public static String getUnitDescriptionPlural(int unit)
  {
    return getTimeUnit(unit).getDescription();
  }
  
  public static TimeUnit getTimeUnit(int unit)
  {
    for (TimeUnit tu : UNITS)
    {
      if (tu.getUnit() == unit)
      {
        return tu;
      }
    }
    
    throw new IllegalStateException("Unknown time unit: " + unit);
  }
  
  public static TimeUnit getTimeUnit(String name)
  {
    TimeUnit unit = NAMED_UNITS.get(name);
    
    if (unit != null)
    {
      return unit;
    }
    
    Number num = Util.convertToNumber(name, true, false);
    
    return getTimeUnit(num.intValue());
  }
  
  public static long convertToMillis(long period, int unit)
  {
    switch (unit)
    {
      case MILLISECOND:
        return period;
        
      case SECOND:
        return period * SECOND_IN_MS;
        
      case MINUTE:
        return period * MINUTE_IN_MS;
        
      case HOUR:
        return period * HOUR_IN_MS;
        
      case DAY:
        return period * DAY_IN_MS;
        
      case WEEK:
        return period * WEEK_IN_MS;
        
      case MONTH:
        return period * MONTH_IN_MS;
        
      case QUARTER:
        return period * QUARTER_IN_MS;
        
      case YEAR:
        return period * YEAR_IN_MS;
        
      default:
        throw new IllegalStateException("Unknown time unit: " + unit);
    }
  }
  
  public static String getForSeconds(Long milliseconds)
  {
    return getForSeconds(milliseconds, Cres.get());
  }
  
  public static String getForSeconds(Long milliseconds, ResourceBundle bundle)
  {
    String Seconds = Cres.get().getString("forSeconds");
    String Second = Cres.get().getString("forSecond");
    String SecondsTwo = Cres.get().getString("forSecondsOne");
    String SecondsOne = Cres.get().getString("forSecondsTwo");
    if (milliseconds == null)
    {
      return "0" + bundle.getString("forSeconds");
    }
    String forSeconds;
    long seconds = milliseconds / TimeHelper.SECOND_IN_MS;
    if (seconds == 1)
    {
      forSeconds = bundle.getString("forSecond");
    }
    else if (seconds % 10 == 1 && seconds % 100 != 11)
    {
      forSeconds = bundle.getString("forSecondsOne");
    }
    else if ((seconds % 10 == 2 && seconds % 100 != 12) || (seconds % 10 == 3 && seconds % 100 != 13) || (seconds % 10 == 4 && seconds % 100 != 14))
    {
      forSeconds = bundle.getString("forSecondsTwo");
    }
    else
    {
      forSeconds = bundle.getString("forSeconds");
    }
    return MessageFormat.format("{0} {1}", seconds, forSeconds);
  }
}
