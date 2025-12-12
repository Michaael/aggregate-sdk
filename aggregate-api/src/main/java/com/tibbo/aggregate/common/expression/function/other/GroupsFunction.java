package com.tibbo.aggregate.common.expression.function.other;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class GroupsFunction extends AbstractFunction
{
  /**
   * Кэш для скомпилированных регулярных выражений.
   * Переиспользование Pattern объектов снижает нагрузку на CPU и память.
   */
  private static final ConcurrentHashMap<String, Pattern> patternCache = new ConcurrentHashMap<String, Pattern>();
  
  /**
   * Максимальный размер кэша паттернов.
   */
  private static final int MAX_PATTERN_CACHE_SIZE = 1000;
  
  public GroupsFunction()
  {
    super("groups", Function.GROUP_STRING_PROCESSING, "String source, String regex", "Object", Cres.get().getString("fDescGroups"));
  }
  
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
      patternCache.clear();
    }
    
    return patternCache.computeIfAbsent(regexString, Pattern::compile);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    String source = parameters[0].toString();
    String regex = parameters[1].toString();
    
    // Оптимизация: используем кэшированный Pattern вместо компиляции каждый раз
    Pattern pattern = getCompiledPattern(regex);
    Matcher matcher = pattern.matcher(source);
    
    // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
    List<Object> result = new ArrayList<Object>();
    
    while (matcher.find())
    {
      for (int i = 0; i < matcher.groupCount(); i++)
        result.add(matcher.group(i + 1));
    }
    
    return result.size() == 1 ? result.get(0) : DataTableUtils.wrapToTable(result);
  }
  
  /**
   * Очистить кэш паттернов.
   */
  public static void clearPatternCache()
  {
    patternCache.clear();
  }
}
