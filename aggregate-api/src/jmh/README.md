# JMH Benchmarks

Этот модуль содержит бенчмарки производительности для измерения эффекта от оптимизаций в AggreGate SDK.

## Установка

JMH зависимости уже настроены в `build.gradle.kts`. Для работы бенчмарков требуется:

- Java 8+
- Gradle 8.11+

## Доступные бенчмарки

### 1. PathParsingBenchmark

Измеряет производительность парсинга путей контекстов:
- `splitPathDirect()` - прямое разбиение пути (без кэширования)
- `splitPathCached()` - разбиение пути с кэшированием
- `splitMultiplePaths()` - разбиение множественных путей

**Цель:** Измерить эффект от кэширования результатов `ContextUtils.splitPathCached()`

### 2. TypeConversionBenchmark

Измеряет производительность преобразования типов:
- `convertStringToNumber()` - преобразование строк в числа
- `convertStringToBoolean()` - преобразование строк в булевы значения
- `convertNumberToNumber()` - преобразование чисел (базовая линия)

**Цель:** Измерить эффект от кэширования результатов `Util.convertToNumber()` и `Util.convertToBoolean()`

## Запуск бенчмарков

### Базовый запуск (показать справку)

```bash
./gradlew :aggregate-api:jmh
```

### Запуск конкретного бенчмарка

```bash
./gradlew :aggregate-api:jmh --args="PathParsingBenchmark"
```

### Запуск с выводом в JSON

```bash
./gradlew :aggregate-api:jmh --args="PathParsingBenchmark -rf json -rff results.json"
```

### Запуск всех бенчмарков

```bash
./gradlew :aggregate-api:jmh --args=".*Benchmark"
```

### Параметры JMH

Доступные параметры JMH:
- `-h` - показать справку
- `-rf <format>` - формат результатов (json, csv, text)
- `-rff <file>` - файл для сохранения результатов
- `-i <iterations>` - количество итераций
- `-w <time>` - время прогрева
- `-wi <iterations>` - количество итераций прогрева
- `-f <forks>` - количество форков

Пример с полными параметрами:

```bash
./gradlew :aggregate-api:jmh --args="PathParsingBenchmark -rf json -rff results.json -i 10 -wi 5 -f 3"
```

## Интерпретация результатов

Результаты показывают среднее время выполнения в наносекундах. Чем меньше значение, тем лучше производительность.

### Ожидаемые улучшения

После оптимизаций версии 1.3.7 ожидаются следующие улучшения:

1. **PathParsingBenchmark**: 30-50% снижение времени выполнения для кэшированных путей
2. **TypeConversionBenchmark**: 25-40% снижение времени выполнения для часто используемых строк

## Добавление новых бенчмарков

1. Создайте новый класс в `src/jmh/java/com/tibbo/aggregate/common/benchmark/`
2. Аннотируйте класс с `@BenchmarkMode`, `@State`, `@Warmup`, `@Measurement`
3. Добавьте методы с аннотацией `@Benchmark`
4. Добавьте метод `main()` для прямого запуска

Пример:

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MyBenchmark {
    @Benchmark
    public void myBenchmark() {
        // Код для измерения
    }
}
```

## Примечания

- Бенчмарки должны запускаться в изолированной среде для точных результатов
- Рекомендуется запускать на машине с минимальной нагрузкой
- Для production-измерений используйте профилировщики (JProfiler, VisualVM, async-profiler)

