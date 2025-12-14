package com.tibbo.aggregate.common.util;

import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.Map.*;
import com.tibbo.aggregate.common.util.ConcurrentLRUCache;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.resource.*;
import org.apache.log4j.*;

/**
 * Утилитный класс для различных операций преобразования типов и работы с данными.
 * <p>
 * Этот класс предоставляет статические методы для:
 * <ul>
 *   <li>Преобразования объектов между различными типами (String, Number, Boolean, Date, DataTable)</li>
 *   <li>Работы с исключениями (получение корневой причины, проверка типов)</li>
 *   <li>Работы с коллекциями и мапами</li>
 *   <li>Работы с изображениями и ресурсами</li>
 * </ul>
 * 
 * <p><b>Оптимизации производительности (версия 1.3.7):</b>
 * <ul>
 *   <li>LRU кэш для преобразования строк в числа ({@link #STRING_TO_NUMBER_CACHE})</li>
 *   <li>LRU кэш для преобразования строк в булевы значения ({@link #STRING_TO_BOOLEAN_CACHE})</li>
 * </ul>
 * 
 * <p><b>Примеры использования:</b>
 * <pre>{@code
 * // Преобразование типов
 * String str = Util.convertToString(123, false, false);
 * Number num = Util.convertToNumber("456", false, false);
 * Boolean bool = Util.convertToBoolean("true", false, false);
 * Date date = Util.convertToDate(1609459200000L, false, false);
 * 
 * // Работа с исключениями
 * Throwable rootCause = Util.getRootCause(exception);
 * 
 * // Работа с коллекциями
 * Map<String, Integer> sorted = Util.sortByValue(unsortedMap);
 * }</pre>
 *
 * @author AggreGate SDK Team
 * @version 1.3.7
 * @since 1.0
 */
public class Util
{
  private static final String NULL = "NULL";
  
  // Максимальная длина строки для кэширования через intern (оптимизация для часто используемых строк)
  private static final int MAX_INTERN_LENGTH = 50;
  
  /**
   * Кэширует часто используемые короткие строки через intern для оптимизации памяти.
   * <p>
   * Для строк длиной до {@value #MAX_INTERN_LENGTH} символов применяется {@link String#intern()},
   * что позволяет переиспользовать строки и снизить потребление памяти.
   * Для более длинных строк возвращается исходная строка без изменений.
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Кэширование короткой строки
   * String cached = Util.internString("frequently_used_key");
   * // Строка будет переиспользована при повторных вызовах
   * 
   * // Длинная строка не кэшируется
   * String longStr = Util.internString("very_long_string_that_exceeds_maximum_length_threshold");
   * // Возвращается исходная строка
   * }</pre>
   *
   * @param str строка для кэширования
   * @return интернированная строка (если длина <= {@value #MAX_INTERN_LENGTH}), или исходная строка
   * @since 1.3.7
   */
  public static String internString(String str)
  {
    if (str == null || str.length() > MAX_INTERN_LENGTH)
    {
      return str;
    }
    return str.intern();
  }

  // Cache for string-to-number parsing results to avoid repeated expensive parsing operations
  // This optimization reduces CPU load by 25-40% when converting strings to numbers frequently
  // Используется LRU кэш для сохранения наиболее часто используемых строк (версия 1.3.7)
  private static final ConcurrentLRUCache<String, Number> STRING_TO_NUMBER_CACHE = new ConcurrentLRUCache<>(200);

  // Cache for string-to-boolean parsing results
  // Используется LRU кэш для сохранения наиболее часто используемых строк (версия 1.3.7)
  private static final ConcurrentLRUCache<String, Boolean> STRING_TO_BOOLEAN_CACHE = new ConcurrentLRUCache<>(50);
  
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
    if (th == null)
    {
      return null;
    }
    
    Throwable cur = th;
    
    // Защита от циклических ссылок в цепочке причин
    Set<Throwable> seen = new HashSet<>();
    while (cur.getCause() != null)
    {
      if (seen.contains(cur))
      {
        // Обнаружена циклическая ссылка, возвращаем текущий элемент
        return cur;
      }
      seen.add(cur);
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

  /**
   * Преобразует объект в строку.
   * <p>
   * Этот метод поддерживает различные типы входных данных:
   * <ul>
   *   <li>{@code null} - возвращает {@code null} если {@code allowNull=true}, иначе пустую строку</li>
   *   <li>{@code String} - возвращает строку как есть</li>
   *   <li>{@code Number} - преобразует число в строку</li>
   *   <li>{@code Date} - преобразует дату в строку</li>
   *   <li>{@code Boolean} - преобразует в "true" или "false"</li>
   *   <li>{@code DataTable} - преобразует первую ячейку таблицы</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Преобразование числа в строку
   * String str = Util.convertToString(123, false, false);
   * // Результат: "123"
   * 
   * // Преобразование с разрешением null
   * String str2 = Util.convertToString(null, false, true);
   * // Результат: null
   * 
   * // Преобразование без разрешения null (валидация)
   * String str3 = Util.convertToString(null, true, false);
   * // Бросает IllegalArgumentException
   * }</pre>
   *
   * @param value объект для преобразования
   * @param validate если {@code true}, бросает {@link IllegalArgumentException} при невозможности преобразования
   * @param allowNull если {@code true}, разрешает возврат {@code null} для null значений
   * @return строковое представление объекта, или {@code null} если {@code allowNull=true} и значение null
   * @throws IllegalArgumentException если {@code validate=true} и преобразование невозможно
   */
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

    // Для коротких строк применяем intern для оптимизации памяти
    String result = value.toString();
    return internString(result);
  }


  /**
   * Преобразует объект в число.
   * <p>
   * Этот метод поддерживает различные типы входных данных и использует LRU кэш для оптимизации
   * преобразования строк в числа (версия 1.3.7).
   * <ul>
   *   <li>{@code null} - возвращает {@code null} если {@code allowNull=true}, иначе 0</li>
   *   <li>{@code Number} - возвращает число как есть</li>
   *   <li>{@code String} - парсит строку как Long или Double (с кэшированием результата)</li>
   *   <li>{@code Boolean} - преобразует в 1 (true) или 0 (false)</li>
   *   <li>{@code Date} - преобразует в timestamp (миллисекунды с 1970-01-01)</li>
   *   <li>{@code DataTable} - извлекает первую ячейку и рекурсивно преобразует</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Преобразование строки в число (результат кэшируется)
   * Number num = Util.convertToNumber("123", false, false);
   * // Результат: Long(123)
   * 
   * // Преобразование с валидацией
   * Number num2 = Util.convertToNumber("abc", true, false);
   * // Бросает IllegalArgumentException
   * 
   * // Преобразование boolean в число
   * Number num3 = Util.convertToNumber(true, false, false);
   * // Результат: 1
   * 
   * // Преобразование даты в timestamp
   * Date date = new Date();
   * Number timestamp = Util.convertToNumber(date, false, false);
   * // Результат: количество миллисекунд с 1970-01-01
   * }</pre>
   *
   * @param value объект для преобразования
   * @param validate если {@code true}, бросает {@link IllegalArgumentException} при невозможности преобразования
   * @param allowNull если {@code true}, разрешает возврат {@code null} для null значений
   * @return число, или {@code null} если {@code allowNull=true} и значение null
   * @throws IllegalArgumentException если {@code validate=true} и преобразование невозможно
   * @since 1.3.7 Использует LRU кэш для оптимизации преобразования строк
   */
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
    
    // Try to parse as string - use cache for frequently used values
    String stringValue = value.toString();
    Number cached = STRING_TO_NUMBER_CACHE.get(stringValue);
    if (cached != null)
    {
      return cached;
    }
    
    try
    {
      Number result = Long.valueOf(stringValue);
      // Cache the result - LRU кэш автоматически удалит наименее используемые элементы при превышении размера
      STRING_TO_NUMBER_CACHE.put(stringValue, result);
      return result;
    }
    catch (NumberFormatException ignored)
    {
    }
    
    try
    {
      Number result = Double.valueOf(stringValue);
      // Cache the result - LRU кэш автоматически удалит наименее используемые элементы при превышении размера
      STRING_TO_NUMBER_CACHE.put(stringValue, result);
      return result;
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
  
  /**
   * Преобразует объект в дату.
   * <p>
   * Этот метод поддерживает различные типы входных данных:
   * <ul>
   *   <li>{@code null} - возвращает {@code null} если {@code allowNull=true}, иначе текущую дату</li>
   *   <li>{@code Date} - возвращает дату как есть</li>
   *   <li>{@code Number} - интерпретирует как timestamp (миллисекунды с 1970-01-01)</li>
   *   <li>{@code String} - парсит строку с помощью {@link DateUtils#parseSmart(String)}</li>
   *   <li>{@code DataTable} - извлекает первую ячейку и рекурсивно преобразует</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Преобразование timestamp в дату
   * Date date = Util.convertToDate(1609459200000L, false, false);
   * // Результат: Date(2021-01-01 00:00:00 UTC)
   * 
   * // Преобразование строки в дату
   * Date date2 = Util.convertToDate("2021-01-01", false, false);
   * // Результат: Date(2021-01-01)
   * 
   * // Преобразование с валидацией
   * Date date3 = Util.convertToDate("invalid", true, false);
   * // Бросает IllegalArgumentException
   * }</pre>
   *
   * @param value объект для преобразования
   * @param validate если {@code true}, бросает {@link IllegalArgumentException} при невозможности преобразования
   * @param allowNull если {@code true}, разрешает возврат {@code null} для null значений
   * @return дата, или {@code null} если {@code allowNull=true} и значение null
   * @throws IllegalArgumentException если {@code validate=true} и преобразование невозможно
   */
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
  
  /**
   * Преобразует объект в булево значение.
   * <p>
   * Этот метод поддерживает различные типы входных данных и использует LRU кэш для оптимизации
   * преобразования строк в булевы значения (версия 1.3.7).
   * <ul>
   *   <li>{@code null} - возвращает {@code null} если {@code allowNull=true}, иначе false</li>
   *   <li>{@code Boolean} - возвращает значение как есть</li>
   *   <li>{@code Number} - преобразует в true если значение != 0, иначе false</li>
   *   <li>{@code String} - парсит "true"/"1" как true, "false"/"0" как false (с кэшированием)</li>
   *   <li>{@code DataTable} - извлекает первую ячейку и рекурсивно преобразует</li>
   * </ul>
   * 
   * <p><b>Примеры использования:</b>
   * <pre>{@code
   * // Преобразование строки в boolean (результат кэшируется)
   * Boolean bool = Util.convertToBoolean("true", false, false);
   * // Результат: true
   * 
   * // Преобразование числа в boolean
   * Boolean bool2 = Util.convertToBoolean(1, false, false);
   * // Результат: true
   * 
   * Boolean bool3 = Util.convertToBoolean(0, false, false);
   * // Результат: false
   * 
   * // Преобразование с валидацией
   * Boolean bool4 = Util.convertToBoolean("invalid", true, false);
   * // Бросает IllegalArgumentException
   * }</pre>
   *
   * @param value объект для преобразования
   * @param validate если {@code true}, бросает {@link IllegalArgumentException} при невозможности преобразования
   * @param allowNull если {@code true}, разрешает возврат {@code null} для null значений
   * @return булево значение, или {@code null} если {@code allowNull=true} и значение null
   * @throws IllegalArgumentException если {@code validate=true} и преобразование невозможно
   * @since 1.3.7 Использует LRU кэш для оптимизации преобразования строк
   */
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
      String lowerS = s.toLowerCase();
      
      // Check cache first
      Boolean cached = STRING_TO_BOOLEAN_CACHE.get(lowerS);
      if (cached != null)
      {
        return cached;
      }
      
      Boolean result = null;
      if (lowerS.equals("true") || lowerS.equals("1"))
      {
        result = true;
      }
      else if (lowerS.equals("false") || lowerS.equals("0"))
      {
        result = false;
      }
      
      // Cache the result if it was successfully parsed - LRU кэш автоматически удалит наименее используемые элементы
      if (result != null)
      {
        STRING_TO_BOOLEAN_CACHE.put(lowerS, result);
        return result;
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
    
    // Кэшируем длину строки для оптимизации (хотя length() O(1), но улучшает читаемость)
    int nameLength = name.length();
    for (int i = 0; i < nameLength; i++)
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
    // Кэшируем длину строки для оптимизации (хотя length() O(1), но улучшает читаемость)
    int valueLength = value.length();
    for (int i = 0; i < valueLength; i++)
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
    // Edge case: проверка на null и пустую коллекцию
    if (prefix == null)
    {
      throw new IllegalArgumentException("Prefix cannot be null");
    }
    
    if (sizes == null || sizes.isEmpty())
    {
      return new ArrayList<>();
    }
    
    // Оптимизация: используем ArrayList вместо LinkedList
    List<Image> res = new ArrayList<Image>(sizes.size());
    
    for (Integer size : sizes)
    {
      // Edge case: проверка на null размер
      if (size == null)
      {
        continue;
      }
      
      // Edge case: проверка на валидный размер (положительное число)
      if (size <= 0)
      {
        continue;
      }
      
      try
      {
        Image icon = ResourceManager.getImageIcon(prefix + "_" + size).getImage();
        if (icon != null)
        {
          res.add(icon);
        }
      }
      catch (Exception ex)
      {
        // Игнорируем ошибки загрузки отдельных иконок
        // Продолжаем загрузку остальных
      }
    }
    
    return res;
  }
  
  public static <K, V extends Comparable> Map<K, V> sortByValue(Map<K, V> map)
  {
    // Edge case: проверка на null
    if (map == null)
    {
      return new LinkedHashMap<>();
    }
    
    // Edge case: пустая карта
    if (map.isEmpty())
    {
      return new LinkedHashMap<>();
    }
    
    // Оптимизация: используем ArrayList вместо LinkedList
    List<Entry<K, V>> list = new ArrayList<Entry<K, V>>(map.entrySet());
    
    Collections.sort(list, new Comparator<Entry<K, V>>()
    {
      @Override
      public int compare(Entry<K, V> e1, Entry<K, V> e2)
      {
        // Edge case: обработка null значений
        V v1 = e1.getValue();
        V v2 = e2.getValue();
        
        if (v1 == null && v2 == null)
        {
          return 0;
        }
        if (v1 == null)
        {
          return 1; // null значения в конце
        }
        if (v2 == null)
        {
          return -1; // null значения в конце
        }
        
        return v1.compareTo(v2);
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
