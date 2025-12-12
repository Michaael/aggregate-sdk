package com.tibbo.aggregate.common.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Метрики использования протокола AggreGate.
 * Отслеживает статистику использования пулов и производительность.
 * 
 * <p>Метрики собираются автоматически и могут быть использованы
 * для мониторинга и оптимизации производительности.</p>
 */
public class ProtocolMetrics {
    
    /**
     * Количество команд, обработанных через пул буферов.
     */
    private static final AtomicLong commandsProcessed = new AtomicLong(0);
    
    /**
     * Количество буферов, полученных из пула.
     */
    private static final AtomicLong buffersAcquired = new AtomicLong(0);
    
    /**
     * Количество буферов, возвращенных в пул.
     */
    private static final AtomicLong buffersReleased = new AtomicLong(0);
    
    /**
     * Количество буферов, созданных заново (пул был пуст).
     */
    private static final AtomicLong buffersCreated = new AtomicLong(0);
    
    /**
     * Количество потоков, полученных из пула.
     */
    private static final AtomicLong streamsAcquired = new AtomicLong(0);
    
    /**
     * Количество команд, обработанных с использованием JSON.
     */
    private static final AtomicLong jsonCommandsProcessed = new AtomicLong(0);
    
    /**
     * Общий размер обработанных команд (в байтах).
     */
    private static final AtomicLong totalCommandSize = new AtomicLong(0);
    
    /**
     * Максимальный размер обработанной команды (в байтах).
     */
    private static final AtomicLong maxCommandSize = new AtomicLong(0);
    
    /**
     * Количество команд, превысивших максимальный размер.
     */
    private static final AtomicLong oversizedCommandsRejected = new AtomicLong(0);
    
    /**
     * Увеличить счетчик обработанных команд.
     */
    public static void incrementCommandsProcessed()
    {
        commandsProcessed.incrementAndGet();
    }
    
    /**
     * Увеличить счетчик полученных буферов.
     */
    public static void incrementBuffersAcquired()
    {
        buffersAcquired.incrementAndGet();
    }
    
    /**
     * Увеличить счетчик возвращенных буферов.
     */
    public static void incrementBuffersReleased()
    {
        buffersReleased.incrementAndGet();
    }
    
    /**
     * Увеличить счетчик созданных буферов.
     */
    public static void incrementBuffersCreated()
    {
        buffersCreated.incrementAndGet();
    }
    
    /**
     * Увеличить счетчик полученных потоков.
     */
    public static void incrementStreamsAcquired()
    {
        streamsAcquired.incrementAndGet();
    }
    
    /**
     * Увеличить счетчик обработанных JSON команд.
     */
    public static void incrementJsonCommandsProcessed()
    {
        jsonCommandsProcessed.incrementAndGet();
    }
    
    /**
     * Добавить размер команды к общей статистике.
     * 
     * @param size размер команды в байтах
     */
    public static void addCommandSize(long size)
    {
        totalCommandSize.addAndGet(size);
        // Обновляем максимальный размер
        long currentMax = maxCommandSize.get();
        while (size > currentMax && !maxCommandSize.compareAndSet(currentMax, size))
        {
            currentMax = maxCommandSize.get();
        }
    }
    
    /**
     * Увеличить счетчик отклоненных команд (превышающих максимальный размер).
     */
    public static void incrementOversizedCommandsRejected()
    {
        oversizedCommandsRejected.incrementAndGet();
    }
    
    /**
     * Получить количество обработанных команд.
     * 
     * @return количество команд
     */
    public static long getCommandsProcessed()
    {
        return commandsProcessed.get();
    }
    
    /**
     * Получить количество полученных буферов.
     * 
     * @return количество буферов
     */
    public static long getBuffersAcquired()
    {
        return buffersAcquired.get();
    }
    
    /**
     * Получить количество возвращенных буферов.
     * 
     * @return количество буферов
     */
    public static long getBuffersReleased()
    {
        return buffersReleased.get();
    }
    
    /**
     * Получить количество созданных буферов.
     * 
     * @return количество буферов
     */
    public static long getBuffersCreated()
    {
        return buffersCreated.get();
    }
    
    /**
     * Получить количество полученных потоков.
     * 
     * @return количество потоков
     */
    public static long getStreamsAcquired()
    {
        return streamsAcquired.get();
    }
    
    /**
     * Получить количество обработанных JSON команд.
     * 
     * @return количество команд
     */
    public static long getJsonCommandsProcessed()
    {
        return jsonCommandsProcessed.get();
    }
    
    /**
     * Получить средний размер команды.
     * 
     * @return средний размер в байтах, или 0 если команды не обрабатывались
     */
    public static double getAverageCommandSize()
    {
        long processed = commandsProcessed.get();
        if (processed == 0)
        {
            return 0.0;
        }
        return (double) totalCommandSize.get() / processed;
    }
    
    /**
     * Получить максимальный размер обработанной команды.
     * 
     * @return максимальный размер в байтах
     */
    public static long getMaxCommandSize()
    {
        return maxCommandSize.get();
    }
    
    /**
     * Получить количество отклоненных команд.
     * 
     * @return количество команд
     */
    public static long getOversizedCommandsRejected()
    {
        return oversizedCommandsRejected.get();
    }
    
    /**
     * Получить коэффициент переиспользования буферов.
     * 
     * @return коэффициент от 0.0 до 1.0, где 1.0 означает 100% переиспользование
     */
    public static double getBufferReuseRatio()
    {
        long acquired = buffersAcquired.get();
        if (acquired == 0)
        {
            return 0.0;
        }
        long created = buffersCreated.get();
        return 1.0 - ((double) created / acquired);
    }
    
    /**
     * Получить процент JSON команд от общего количества.
     * 
     * @return процент от 0.0 до 100.0
     */
    public static double getJsonCommandsPercentage()
    {
        long processed = commandsProcessed.get();
        if (processed == 0)
        {
            return 0.0;
        }
        return ((double) jsonCommandsProcessed.get() / processed) * 100.0;
    }
    
    /**
     * Сбросить все метрики.
     * Полезно для тестирования или периодического сброса статистики.
     */
    public static void reset()
    {
        commandsProcessed.set(0);
        buffersAcquired.set(0);
        buffersReleased.set(0);
        buffersCreated.set(0);
        streamsAcquired.set(0);
        jsonCommandsProcessed.set(0);
        totalCommandSize.set(0);
        maxCommandSize.set(0);
        oversizedCommandsRejected.set(0);
    }
    
    /**
     * Получить строковое представление всех метрик.
     * 
     * @return строка с метриками
     */
    public static String getMetricsSummary()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Protocol Metrics:\n");
        sb.append("  Commands processed: ").append(getCommandsProcessed()).append("\n");
        sb.append("  Buffers acquired: ").append(getBuffersAcquired()).append("\n");
        sb.append("  Buffers released: ").append(getBuffersReleased()).append("\n");
        sb.append("  Buffers created: ").append(getBuffersCreated()).append("\n");
        sb.append("  Buffer reuse ratio: ").append(String.format("%.2f%%", getBufferReuseRatio() * 100)).append("\n");
        sb.append("  Streams acquired: ").append(getStreamsAcquired()).append("\n");
        sb.append("  JSON commands: ").append(getJsonCommandsProcessed());
        sb.append(" (").append(String.format("%.2f%%", getJsonCommandsPercentage())).append(")\n");
        sb.append("  Average command size: ").append(String.format("%.2f", getAverageCommandSize())).append(" bytes\n");
        sb.append("  Max command size: ").append(getMaxCommandSize()).append(" bytes\n");
        sb.append("  Oversized commands rejected: ").append(getOversizedCommandsRejected()).append("\n");
        return sb.toString();
    }
}

