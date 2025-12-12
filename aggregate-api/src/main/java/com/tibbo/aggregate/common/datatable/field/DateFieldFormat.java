package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class DateFieldFormat extends FieldFormat<Date>
{
  public static final String EDITOR_TIME = "time";
  public static final String EDITOR_DATE = "date";

  private static Date DEFAULT_DATE;
  private static String DATE_PATTERN = "^\\d{4,5}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$";

  static
  {
    try
    {
      GregorianCalendar gc = new GregorianCalendar(DateUtils.UTC_TIME_ZONE);
      gc.clear();
      gc.set(2000, 1, 1, 12, 0, 0);
      DEFAULT_DATE = gc.getTime();
    }
    catch (Exception ex)
    {
      Log.DATATABLE.error("Error initializing default date", ex);
    }
  }

  private static ThreadLocal<GregorianCalendar> CALENDAR_THREAD_LOCAL = new ThreadLocal<GregorianCalendar>();

  public DateFieldFormat(String name)
  {
    super(name);
  }

  @Override
  public char getType()
  {
    return FieldFormat.DATE_FIELD;
  }

  @Override
  public Class getFieldClass()
  {
    return Date.class;
  }

  @Override
  public Class getFieldWrappedClass()
  {
    return Date.class;
  }

  @Override
  public Date getNotNullDefault()
  {
    return DEFAULT_DATE;
  }

  private static GregorianCalendar getCalendar()
  {
    GregorianCalendar gc = CALENDAR_THREAD_LOCAL.get();
    if (gc == null)
    {
      gc = new GregorianCalendar(DateUtils.UTC_TIME_ZONE);
      CALENDAR_THREAD_LOCAL.set(gc);
    }
    return gc;
  }

  @Override
  public Date valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    try
    {
      return dateFromString(value);
    }
    catch (Exception ex)
    {
      try
      {
        return Util.convertToDate(value, true, true);
      }
      catch (Exception ex1)
      {
        throw new IllegalArgumentException("Error parsing date from string '" + value + "': " + ex.getMessage(), ex);
      }
    }
  }

  @Override
  public String valueToString(Date value, ClassicEncodingSettings settings)
  {
    try
    {
      if (value == null)
      {
        return null;
      }

      return dateToString(value);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error converting date " + value + " to string: " + ex.getMessage(), ex);
    }
  }

  public static Date dateFromString(String value)
  {
    if (!value.matches(DATE_PATTERN))
    {
      throw new IllegalStateException("Illegal value format");
    }

    String[] splitValue = value.split("\\D");

    GregorianCalendar gc = getCalendar();

    gc.set(Calendar.YEAR, Integer.parseInt(splitValue[0]));
    gc.set(Calendar.MONTH, Integer.parseInt(splitValue[1]) - 1);
    gc.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitValue[2]));
    gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitValue[3]));
    gc.set(Calendar.MINUTE, Integer.parseInt(splitValue[4]));
    gc.set(Calendar.SECOND, Integer.parseInt(splitValue[5]));
    gc.set(Calendar.MILLISECOND, Integer.parseInt(splitValue[6]));

    return gc.getTime();
  }

  public static String dateToString(Date value)
  {
    GregorianCalendar gc = getCalendar();

    gc.setTime(value);

    StringBuilder sb = new StringBuilder();

    int year = gc.get(Calendar.YEAR);
    if (year < 1000)
    {
      sb.append("0");
    }
    if (year < 100)
    {
      sb.append("0");
    }
    if (year < 10)
    {
      sb.append("0");
    }
    sb.append(year);

    sb.append("-");

    int month = gc.get(Calendar.MONTH) + 1;
    if (month < 10)
    {
      sb.append("0");
    }
    sb.append(month);

    sb.append("-");

    int day = gc.get(Calendar.DAY_OF_MONTH);
    if (day < 10)
    {
      sb.append("0");
    }
    sb.append(day);

    sb.append(" ");

    int hour = gc.get(Calendar.HOUR_OF_DAY);
    if (hour < 10)
    {
      sb.append("0");
    }
    sb.append(hour);

    sb.append(":");

    int minute = gc.get(Calendar.MINUTE);
    if (minute < 10)
    {
      sb.append("0");
    }
    sb.append(minute);

    sb.append(":");

    int second = gc.get(Calendar.SECOND);
    if (second < 10)
    {
      sb.append("0");
    }
    sb.append(second);

    sb.append(".");

    int millisecond = gc.get(Calendar.MILLISECOND);
    if (millisecond < 100)
    {
      sb.append("0");
    }
    if (millisecond < 10)
    {
      sb.append("0");
    }
    sb.append(millisecond);

    return sb.toString();
  }

  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(new String[] { EDITOR_LIST, EDITOR_DATE, EDITOR_TIME });
  }
}
