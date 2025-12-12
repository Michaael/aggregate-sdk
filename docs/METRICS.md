# Метрики протокола AggreGate

## Обзор

Класс `ProtocolMetrics` предоставляет инструменты для мониторинга производительности и использования ресурсов протокола AggreGate.

## Доступные метрики

### Основные метрики

- **Commands processed** - общее количество обработанных команд
- **Buffers acquired** - количество буферов, полученных из пула
- **Buffers released** - количество буферов, возвращенных в пул
- **Buffers created** - количество буферов, созданных заново (пул был пуст)
- **Streams acquired** - количество потоков, полученных из пула
- **JSON commands processed** - количество обработанных JSON команд

### Производные метрики

- **Buffer reuse ratio** - коэффициент переиспользования буферов (0.0 - 1.0)
- **Average command size** - средний размер команды в байтах
- **Max command size** - максимальный размер обработанной команды
- **JSON commands percentage** - процент JSON команд от общего количества
- **Oversized commands rejected** - количество отклоненных команд (превышающих лимит)

## Использование

### Получение метрик

```java
import com.tibbo.aggregate.common.protocol.ProtocolMetrics;

// Получить общую статистику
String summary = ProtocolMetrics.getMetricsSummary();
System.out.println(summary);

// Получить отдельные метрики
long commands = ProtocolMetrics.getCommandsProcessed();
double reuseRatio = ProtocolMetrics.getBufferReuseRatio();
double avgSize = ProtocolMetrics.getAverageCommandSize();
```

### Пример вывода

```
Protocol Metrics:
  Commands processed: 10000
  Buffers acquired: 8500
  Buffers released: 8200
  Buffers created: 300
  Buffer reuse ratio: 96.47%
  Streams acquired: 10000
  JSON commands: 500 (5.00%)
  Average command size: 2048.50 bytes
  Max command size: 1048576 bytes
  Oversized commands rejected: 0
```

## Интерпретация метрик

### Buffer reuse ratio

**Идеальное значение:** > 80%

Высокий коэффициент переиспользования означает, что пул буферов работает эффективно и большинство буферов переиспользуется, а не создается заново.

**Низкий коэффициент (< 50%)** может указывать на:
- Недостаточный размер пула
- Неправильное использование (буферы не возвращаются в пул)
- Слишком разнообразные размеры буферов

### Average command size

Помогает понять типичный размер команд и настроить размеры буферов соответственно.

**Рекомендации:**
- Если средний размер < 1KB, можно уменьшить начальный размер буфера
- Если средний размер > 10KB, можно увеличить начальный размер буфера

### JSON commands percentage

Показывает, какая доля команд использует JSON формат.

**Высокий процент (> 50%)** означает, что оптимизация JSON парсинга критична.

### Oversized commands rejected

Количество команд, превышающих максимальный размер (100MB).

**Ненулевое значение** может указывать на:
- Попытки атаки (DoS)
- Ошибки в клиентском коде
- Необходимость увеличения лимита (если это легитимные команды)

## Мониторинг в production

### Периодический сбор метрик

```java
// В методе мониторинга
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    String metrics = ProtocolMetrics.getMetricsSummary();
    logger.info("Protocol metrics:\n{}", metrics);
}, 0, 5, TimeUnit.MINUTES);
```

### Интеграция с JMX

```java
@ManagedResource
public class ProtocolMetricsMBean {
    
    @ManagedAttribute
    public long getCommandsProcessed() {
        return ProtocolMetrics.getCommandsProcessed();
    }
    
    @ManagedAttribute
    public double getBufferReuseRatio() {
        return ProtocolMetrics.getBufferReuseRatio();
    }
    
    // ... другие методы
}
```

### Алерты

Настройте алерты на следующие условия:

1. **Buffer reuse ratio < 50%** - пул работает неэффективно
2. **Oversized commands rejected > 0** - возможная атака или ошибка
3. **Max command size > 10MB** - очень большие команды могут вызывать проблемы
4. **Average command size резко увеличился** - возможна проблема с клиентом

## Сброс метрик

Метрики можно сбросить для периодического сброса статистики:

```java
// Сброс всех метрик
ProtocolMetrics.reset();
```

**Внимание:** Сброс метрик в production должен выполняться осторожно, так как это удалит всю накопленную статистику.

## Рекомендации

1. **Регулярный мониторинг** - собирайте метрики каждые 5-10 минут
2. **Анализ трендов** - отслеживайте изменения метрик во времени
3. **Корреляция с производительностью** - связывайте метрики с производительностью системы
4. **Настройка на основе метрик** - используйте метрики для оптимизации размеров пулов

---

*Документ обновлен: 2024-12-XX*  
*Версия: 1.3.0*

