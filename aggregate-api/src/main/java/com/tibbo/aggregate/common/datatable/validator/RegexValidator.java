package com.tibbo.aggregate.common.datatable.validator;

import java.text.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;

public class RegexValidator extends AbstractFieldValidator
{
  private static final String SEPARATOR = "^^";
  private static final String SEPARATOR_REGEX = "\\^\\^";
  
  /**
   * Кэш для скомпилированных регулярных выражений.
   * Переиспользование Pattern объектов снижает нагрузку на CPU и память.
   * Используется ConcurrentHashMap для потокобезопасности.
   */
  private static final ConcurrentHashMap<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>();
  
  /**
   * Максимальный размер кэша паттернов.
   * Ограничивает потребление памяти при большом количестве различных регулярных выражений.
   */
  private static final int MAX_PATTERN_CACHE_SIZE = 1000;
  
  private String regex;
  private String message;
  
  /**
   * Получить скомпилированный Pattern для регулярного выражения.
   * Использует кэш для переиспользования Pattern объектов.
   * 
   * @param regexString регулярное выражение
   * @return скомпилированный Pattern
   */
  private static Pattern getCompiledPattern(String regexString)
  {
    // Проверяем размер кэша и очищаем при необходимости
    if (patternCache.size() > MAX_PATTERN_CACHE_SIZE)
    {
      // Очищаем половину кэша (удаляем старые записи)
      // В реальности можно использовать LRU кэш, но для простоты используем очистку
      patternCache.clear();
    }
    
    return patternCache.computeIfAbsent(regexString, Pattern::compile);
  }
  
  public RegexValidator(String source)
  {
    String[] parts = source.split(SEPARATOR_REGEX);
    
    regex = parts[0];
    
    if (parts.length > 1)
    {
      message = parts[1];
    }
  }
  
  public RegexValidator(String regex, String message)
  {
    this.regex = regex;
    this.message = message;
  }
  
  public boolean shouldEncode()
  {
    return true;
  }
  
  public String encode()
  {
    return regex + (message != null ? SEPARATOR + message : "");
  }
  
  public Character getType()
  {
    return FieldFormat.VALIDATOR_REGEX;
  }
  
  public Object validate(Context context, ContextManager contextManager, CallerController caller, Object value) throws ValidationException
  {
    if (value == null)
    {
      return value;
    }
    
    try
    {
      // Оптимизация: используем кэшированный Pattern вместо String.matches()
      // String.matches() компилирует Pattern каждый раз, что неэффективно
      Pattern pattern = getCompiledPattern(regex);
      String valueString = value.toString();
      
      if (!pattern.matcher(valueString).matches())
      {
        throw new ValidationException(message != null ? message : MessageFormat.format(Cres.get().getString("dtValueDoesNotMatchPattern"), value, regex));
      }
    }
    catch (PatternSyntaxException ex)
    {
      throw new ValidationException(ex.getMessage(), ex);
    }
    
    return value;
  }
  
  /**
   * Очистить кэш паттернов.
   * Полезно для освобождения памяти или при изменении конфигурации.
   */
  public static void clearPatternCache()
  {
    patternCache.clear();
  }
  
  /**
   * Получить размер кэша паттернов.
   * 
   * @return количество кэшированных паттернов
   */
  public static int getPatternCacheSize()
  {
    return patternCache.size();
  }
  
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((regex == null) ? 0 : regex.hashCode());
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    RegexValidator other = (RegexValidator) obj;
    if (message == null)
    {
      if (other.message != null)
      {
        return false;
      }
    }
    else if (!message.equals(other.message))
    {
      return false;
    }
    if (regex == null)
    {
      if (other.regex != null)
      {
        return false;
      }
    }
    else if (!regex.equals(other.regex))
    {
      return false;
    }
    return true;
  }
}
