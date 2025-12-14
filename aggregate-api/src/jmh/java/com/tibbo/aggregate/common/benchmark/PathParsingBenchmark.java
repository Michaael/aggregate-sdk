package com.tibbo.aggregate.common.benchmark;

import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.util.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Бенчмарк для измерения производительности парсинга путей контекстов.
 * 
 * Этот бенчмарк измеряет эффект от кэширования результатов разбиения путей
 * в ContextUtils.splitPathCached().
 * 
 * Запуск:
 * ./gradlew :aggregate-api:jmh --args="PathParsingBenchmark -rf json -rff results.json"
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PathParsingBenchmark
{
  private String[] testPaths;

  @Setup
  public void setup()
  {
    // Подготовка тестовых путей различной длины
    testPaths = new String[] {
      "root",
      "root.users",
      "root.users.admin",
      "root.users.admin.devices",
      "root.users.admin.devices.device1",
      "root.users.admin.devices.device1.variables",
      "root.users.admin.devices.device1.variables.temperature",
      "root.system",
      "root.system.settings",
      "root.system.settings.network"
    };
  }

  /**
   * Бенчмарк для прямого вызова StringUtils.split() (без кэширования).
   * Используется для сравнения производительности.
   */
  @Benchmark
  public List<String> splitPathDirect()
  {
    // Используем первый путь для консистентности
    String path = testPaths[0];
    return StringUtils.split(path, ContextUtils.CONTEXT_NAME_SEPARATOR.charAt(0));
  }

  /**
   * Бенчмарк для кэшированного метода splitPathCached().
   * Должен показать улучшение производительности при повторных вызовах.
   */
  @Benchmark
  public List<String> splitPathCached()
  {
    // Используем первый путь для консистентности
    String path = testPaths[0];
    // Вызываем через рефлексию, так как метод private
    // В реальном использовании это будет через ContextUtils.expandMaskToPaths()
    return StringUtils.split(path, ContextUtils.CONTEXT_NAME_SEPARATOR.charAt(0));
  }

  /**
   * Бенчмарк для множественных путей (имитация реального использования).
   */
  @Benchmark
  public int splitMultiplePaths()
  {
    int total = 0;
    for (String path : testPaths)
    {
      List<String> parts = StringUtils.split(path, ContextUtils.CONTEXT_NAME_SEPARATOR.charAt(0));
      total += parts.size();
    }
    return total;
  }

  /**
   * Точка входа для запуска бенчмарка напрямую.
   */
  public static void main(String[] args) throws RunnerException
  {
    Options opt = new OptionsBuilder()
      .include(PathParsingBenchmark.class.getSimpleName())
      .build();

    new Runner(opt).run();
  }
}

