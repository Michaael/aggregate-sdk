package com.tibbo.aggregate.common.benchmark;

import com.tibbo.aggregate.common.util.Util;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Бенчмарк для измерения производительности преобразования типов.
 * 
 * Этот бенчмарк измеряет эффект от кэширования результатов парсинга строк
 * в Util.convertToNumber() и Util.convertToBoolean().
 * 
 * Запуск:
 * ./gradlew :aggregate-api:jmh --args="TypeConversionBenchmark -rf json -rff results.json"
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class TypeConversionBenchmark
{
  private String[] numberStrings;
  private String[] booleanStrings;

  @Setup
  public void setup()
  {
    // Подготовка тестовых строк для преобразования в числа
    numberStrings = new String[] {
      "0", "1", "10", "100", "1000", "12345", "999999",
      "0.0", "1.5", "3.14", "100.99", "999.999"
    };

    // Подготовка тестовых строк для преобразования в булевы значения
    booleanStrings = new String[] {
      "true", "false", "1", "0", "TRUE", "FALSE"
    };
  }

  /**
   * Бенчмарк для преобразования строк в числа.
   * Должен показать улучшение производительности благодаря кэшированию.
   */
  @Benchmark
  public Number convertStringToNumber()
  {
    Number total = 0;
    for (String str : numberStrings)
    {
      Number num = Util.convertToNumber(str, false, false);
      if (num != null)
      {
        total = total.doubleValue() + num.doubleValue();
      }
    }
    return total;
  }

  /**
   * Бенчмарк для преобразования строк в булевы значения.
   * Должен показать улучшение производительности благодаря кэшированию.
   */
  @Benchmark
  public Boolean convertStringToBoolean()
  {
    Boolean result = false;
    for (String str : booleanStrings)
    {
      Boolean bool = Util.convertToBoolean(str, false, false);
      if (bool != null)
      {
        result = result || bool;
      }
    }
    return result;
  }

  /**
   * Бенчмарк для преобразования уже числовых значений (без парсинга).
   */
  @Benchmark
  public Number convertNumberToNumber()
  {
    Number total = 0;
    for (int i = 0; i < 100; i++)
    {
      Number num = Util.convertToNumber(i, false, false);
      total = total.doubleValue() + num.doubleValue();
    }
    return total;
  }

  /**
   * Точка входа для запуска бенчмарка напрямую.
   */
  public static void main(String[] args) throws RunnerException
  {
    Options opt = new OptionsBuilder()
      .include(TypeConversionBenchmark.class.getSimpleName())
      .build();

    new Runner(opt).run();
  }
}

