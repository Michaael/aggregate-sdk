package com.tibbo.aggregate.common.util;

import static com.tibbo.aggregate.common.datatable.DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;

public class DateUtils
{
  public final static String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";
  public final static String DEFAULT_TIME_PATTERN = "HH:mm:ss";
  
  public final static String DATATABLE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
  
  public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
  public static final HashMap<Object, String> DATE_TIME_FORMATS = new LinkedHashMap<Object, String>();
  static
  {
    DATE_TIME_FORMATS.put(null, Cres.get().getString("notSelected"));
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)).toPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)).toPattern(), null);
    
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.UK)).toLocalizedPattern(), null);
    DATE_TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK)).toLocalizedPattern(), null);
  }
  
  public static final HashMap<Object, String> TIME_FORMATS = new LinkedHashMap<Object, String>();
  static
  {
    TIME_FORMATS.put(null, Cres.get().getString("notSelected"));
    TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.FULL)).toPattern(), null);
    TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.LONG)).toPattern(), null);
    TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM)).toPattern(), null);
    TIME_FORMATS.put(((SimpleDateFormat) SimpleDateFormat.getTimeInstance(DateFormat.SHORT)).toPattern(), null);
  }
  
  private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>()
  {
    {
      put("^\\d{8}$", "yyyyMMdd");
      put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
      put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
      put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
      put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
      put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
      put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
      put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
      put("^\\d{12}$", "yyyyMMddHHmm");
      put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
      put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
      put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
      put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
      put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
      put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
      put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
      put("^\\d{14}$", "yyyyMMddHHmmss");
      put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
      put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
      put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yyyy HH:mm:ss");
      put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
      put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
      put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
      put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
      put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
      put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}$", "yyyy-MM-dd HH:mm:ss.SSS");
      put("^\\d{4}-\\d{1,2}-\\d{1,2}-\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}$", "yyyy-MM-dd-HH:mm:ss.SSS");
      put("^(\\d\\d:\\d\\d:\\d\\d)", "hh:mm:ss");
    }
  };
  
  public static SimpleDateFormat createDateFormatter()
  {
    SimpleDateFormat sdf = new SimpleDateFormat(DATATABLE_DATE_PATTERN, Locale.ENGLISH);
    sdf.setTimeZone(UTC_TIME_ZONE);
    return sdf;
  }
  
  public static String getDateTimePattern(String datePattern, String timePattern)
  {
    return (datePattern != null ? datePattern : DEFAULT_DATE_PATTERN) + " " + (timePattern != null ? timePattern : DEFAULT_TIME_PATTERN);
  }
  
  public static Date getStartOfHour(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
  
  public static Date getEndOfHour(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }
  
  public static Date getStartOfDay(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
  
  public static Date getEndOfDay(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }
  
  public static Date getStartOfWeek(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static Date getEndOfWeek(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

    if (calendar.getTime().before(date))
    {
      calendar.add(Calendar.DAY_OF_MONTH, 7);
    }

    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);

    return calendar.getTime();
  }
  
  public static Date getStartOfMonth(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
  
  public static Date getEndOfMonth(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }
  
  public static Date getStartOfYear(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }
  
  public static Date getEndOfYear(Date date)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.YEAR, 1);
    calendar.set(Calendar.DAY_OF_YEAR, 1);
    calendar.add(Calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }
  
  public static Map<Object, String> dateTimeFormats()
  {
    return DATE_TIME_FORMATS;
  }
  
  public static Map<Object, String> timeFormats()
  {
    return TIME_FORMATS;
  }
  
  public static Date parseSmart(String dateString) throws ParseException
  {
    String dateFormat = determineDateFormat(dateString);
    if (dateFormat == null)
    {
      throw new ParseException(Cres.get().getString("utUnknownDateFormat") + dateString, 0);
    }
    return parse(dateString, dateFormat);
  }
  
  private static Date parse(String dateString, String dateFormat) throws ParseException
  {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
    return simpleDateFormat.parse(dateString);
  }
  
  public static String determineDateFormat(String dateString)
  {
    for (String regexp : DATE_FORMAT_REGEXPS.keySet())
    {
      if (dateString.toLowerCase().matches(regexp))
      {
        return DATE_FORMAT_REGEXPS.get(regexp);
      }
    }
    return null; // Unknown format.
  }

  public static SimpleDateFormat getPattern(DataRecord field)
  {
    String editor = field.getString(FIELD_FIELDS_FORMAT_EDITOR);
    String pattern;
    if (DateFieldFormat.EDITOR_DATE.equals(editor))
    {
      pattern = DateUtils.DEFAULT_DATE_PATTERN;
    }
    else if (DateFieldFormat.EDITOR_TIME.equals(editor))
    {
      pattern = DateUtils.DEFAULT_TIME_PATTERN;
    }
    else
    {
      pattern = DateUtils.getDateTimePattern(DateUtils.DEFAULT_DATE_PATTERN, DateUtils.DEFAULT_TIME_PATTERN);
    }
    return new SimpleDateFormat(pattern);
  }
}
