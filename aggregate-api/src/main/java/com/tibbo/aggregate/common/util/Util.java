package com.tibbo.aggregate.common.util;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.Map.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.resource.*;
import org.apache.log4j.*;

public class Util
{
  private static final String NULL = "NULL";
  
  public static boolean equals(Object o1, Object o2)
  {
    if (o1 == null)
    {
      return o2 == null;
    }
    else
    {
      return o1.equals(o2);
    }
  }
  
  public static Throwable getCause(Throwable th, Class<InterruptedException> throwableClass)
  {
    Throwable cause = th;
    
    do
    {
      if (cause != null && throwableClass.isAssignableFrom(cause.getClass()))
      {
        return cause;
      }
      
      cause = cause.getCause();
    }
    while (cause != null);
    
    return null;
  }
  
  public static Throwable getRootCause(Throwable th)
  {
    Throwable cur = th;
    
    while (cur.getCause() != null)
    {
      cur = cur.getCause();
    }
    
    return cur;
  }
  
  public static byte[] readStream(InputStream is) throws IOException
  {
    byte[] buf = new byte[is.available()];
    ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
    
    int numRead;
    while ((numRead = is.read(buf)) > 0)
    {
      os.write(buf, 0, numRead);
    }
    
    return os.toByteArray();
  }

  public static String convertToString(Object value, boolean validate, boolean allowNull)
  {
    if (value == null)
    {
      if (allowNull)
      {
        return null;
      }
      if (validate)
      {
        throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToString") + getObjectDescription(value));
      }
      return new String();
    }

    return value.toString();
  }


  public static Number convertToNumber(Object value, boolean validate, boolean allowNull)
  {
    if (value == null)
    {
      if (allowNull)
      {
        return null;
      }
      if (validate)
      {
        throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToNumber") + getObjectDescription(value));
      }
      return 0;
    }
    
    if (value instanceof DataTable)
    {
      DataTable table = (DataTable) value;
      
      if (table.getRecordCount() == 0 || table.getFieldCount() == 0)
      {
        if (validate)
        {
          throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToNumber") + table);
        }
        return 0;
      }
      
      return convertToNumber(table.get(), validate, allowNull);
    }
    
    if (value instanceof ExtendedNumber)
    {
      Number number = ((ExtendedNumber) value).getNumber();
      return convertToNumber(number, validate, allowNull);
    }
    
    if (value instanceof Number)
    {
      return (Number) value;
    }
    
    if (value instanceof Date)
    {
      return ((Date) value).getTime();
    }
    
    if (value instanceof Boolean)
    {
      return (Boolean) value ? 1 : 0;
    }
    
    try
    {
      return Long.valueOf(value.toString());
    }
    catch (NumberFormatException ignored)
    {
    }
    
    try
    {
      return Double.valueOf(value.toString());
    }
    catch (NumberFormatException ignored)
    {
    }
    
    Boolean aBoolean = convertToBoolean(value, false, true);
    if (aBoolean != null)
    {
      return aBoolean ? 1 : 0;
    }
    
    if (NULL.equals(value.toString().toUpperCase()))
    {
      return allowNull ? null : 0;
    }
    
    if (validate)
    {
      throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToNumber") + getObjectDescription(value));
    }
    else
    {
      return allowNull ? null : 0;
    }
  }
  
  public static Date convertToDate(Object value, boolean validate, boolean allowNull)
  {
    if (value == null)
    {
      if (allowNull)
      {
        return null;
      }
      if (validate)
      {
        throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToDate") + getObjectDescription(value));
      }
      return new Date();
    }
    
    if (value instanceof DataTable)
    {
      DataTable table = (DataTable) value;
      
      if (table.getRecordCount() == 0 || table.getFieldCount() == 0)
      {
        if (validate)
        {
          throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToDate") + table);
        }
        return new Date();
      }
      
      return convertToDate(table.get(), validate, allowNull);
    }
    
    if (value instanceof Number)
    {
      return new Date(((Number) value).longValue());
    }
    
    if (value instanceof Date)
    {
      return (Date) value;
    }
    
    try
    {
      return DateUtils.parseSmart(value.toString());
    }
    catch (ParseException ex)
    {
      if (validate)
      {
        throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToDate") + getObjectDescription(value));
      }
      else
      {
        return allowNull ? null : new Date();
      }
    }
  }
  
  public static Boolean convertToBoolean(Object value, boolean validate, boolean allowNull)
  {
    if (value == null)
    {
      if (allowNull)
      {
        return null;
      }
      if (validate)
      {
        throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToBoolean") + getObjectDescription(value));
      }
      return false;
    }
    
    if (value instanceof DataTable)
    {
      DataTable table = (DataTable) value;
      
      if (table.getRecordCount() == 0 || table.getFieldCount() == 0)
      {
        if (validate)
        {
          throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToBoolean") + table);
        }
        return false;
      }
      
      return convertToBoolean(table.get(), validate, allowNull);
    }
    
    if (value instanceof Boolean)
    {
      return (Boolean) value;
    }
    
    if (value instanceof Number)
    {
      return ((Number) value).longValue() != 0;
    }
    
    if (value instanceof String)
    {
      String s = (String) value;
      if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"))
      {
        return true;
      }
      if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("0"))
      {
        return false;
      }
    }
    
    if (validate)
    {
      throw new IllegalArgumentException(Cres.get().getString("utCannotConvertToBoolean") + getObjectDescription(value));
    }
    else
    {
      return allowNull ? null : false;
    }
  }
  
  public static boolean isFloatingPoint(Number n)
  {
    return n instanceof Float || n instanceof Double;
  }
  
  public static String getObjectDescription(Object o)
  {
    if (o == null)
    {
      return "null";
    }
    
    return o.toString() + " (" + o.getClass().getName() + ")";
  }
  
  public static Class getListElementType(Type listType)
  {
    if (listType instanceof ParameterizedType)
    {
      ParameterizedType pt = (ParameterizedType) listType;
      Type t = pt.getActualTypeArguments()[0];
      if (t != null && t instanceof Class)
      {
        return (Class) t;
      }
    }
    
    return null;
  }
  
  public static Class getMapKeyType(Type mapType)
  {
    if (mapType instanceof ParameterizedType)
    {
      ParameterizedType pt = (ParameterizedType) mapType;
      Type t = pt.getActualTypeArguments()[0];
      if (t != null && t instanceof Class)
      {
        return (Class) t;
      }
    }
    
    return null;
  }
  
  public static int parseVersion(String version)
  {
    int major = Integer.parseInt(version.substring(0, 1));
    int minor = Integer.parseInt(version.substring(2, 4));
    int build = Integer.parseInt(version.substring(5, 7));
    
    return major * 10000 + minor * 100 + build;
  }
  
  public static String nameToDescription(String name)
  {
    StringBuilder sb = new StringBuilder();
    
    boolean prevWasUpper = false;
    boolean nextToUpper = false;
    
    for (int i = 0; i < name.length(); i++)
    {
      Character c = name.charAt(i);
      
      if (Character.isUpperCase(c))
      {
        if (!prevWasUpper && i != 0)
        {
          sb.append(" ");
        }
        prevWasUpper = true;
      }
      else
      {
        prevWasUpper = false;
      }
      
      if (i == 0 || nextToUpper)
      {
        c = Character.toUpperCase(c);
        nextToUpper = false;
      }
      
      if (c == '_')
      {
        sb.append(" ");
        nextToUpper = true;
      }
      else
      {
        sb.append(c);
      }
    }
    
    return sb.toString();
  }
  
  public static String descriptionToName(String value)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);
      if (ContextUtils.isValidContextNameChar(c))
      {
        sb.append(c);
      }
      else
      {
        sb.append('_');
      }
    }
    return sb.toString();
  }
  
  public static String getTrayIconId(String prefix)
  {
    List<Integer> sizes = Arrays.asList(16, 24, 32, 48, 64, 128);
    
    if (SystemTray.isSupported())
    {
      Integer width = SystemTray.getSystemTray().getTrayIconSize().width;
      if (sizes.contains(width))
      {
        return prefix + "_" + width;
      }
    }
    
    return prefix + "_" + 16;
  }
  
  public static List<Image> getIconImages(String prefix, List<Integer> sizes)
  {
    // Оптимизация: используем ArrayList вместо LinkedList
    List<Image> res = new ArrayList<Image>();
    
    for (Integer size : sizes)
    {
      res.add(ResourceManager.getImageIcon(prefix + "_" + size).getImage());
    }
    
    return res;
  }
  
  public static <K, V extends Comparable> Map<K, V> sortByValue(Map<K, V> map)
  {
    // Оптимизация: используем ArrayList вместо LinkedList
    List<Entry<K, V>> list = new ArrayList<Entry<K, V>>(map.entrySet());
    
    Collections.sort(list, new Comparator<Entry<K, V>>()
    {
      @Override
      public int compare(Entry<K, V> e1, Entry<K, V> e2)
      {
        return e1.getValue().compareTo(e2.getValue());
      }
    });
    
    Map<K, V> result = new LinkedHashMap<>();
    
    for (Entry<K, V> entry : list)
    {
      result.put(entry.getKey(), entry.getValue());
    }
    
    return result;
  }
  
  public static void logWithSourceCodeLine(Logger logger, Level level, Object o)
  {
    if (o == null || StringUtils.isEmpty(o.toString()))
    {
      return;
    }
    
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    
    int sourceLoggerStackPosition = 2;
    String sourceLoggerStack = "";
    if (stackTrace.length > sourceLoggerStackPosition + 1)
    {
      sourceLoggerStack = stackTrace[sourceLoggerStackPosition].toString();
    }
    
    logger.log(level, o.toString() + ": " + sourceLoggerStack);
  }
  
  public static String getChosenValueRepresentation(Map<Object, String> sectionValues, Object selectedValue)
  {
    String ov = sectionValues.get(selectedValue);
    return ov != null ? ov : (selectedValue != null ? selectedValue.toString() : Cres.get().getString("notSelected"));
  }
}
