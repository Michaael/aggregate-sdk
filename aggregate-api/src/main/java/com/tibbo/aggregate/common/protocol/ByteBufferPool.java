package com.tibbo.aggregate.common.protocol;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Пул для переиспользования ByteBuffer объектов.
 * Снижает количество аллокаций и нагрузку на GC.
 * 
 * <p>Буферы возвращаются в пул после использования и могут быть
 * переиспользованы для следующих операций.</p>
 * 
 * <p>Пул ограничен по размеру, чтобы не накапливать слишком много
 * неиспользуемых буферов в памяти.</p>
 */
public class ByteBufferPool {
    
    /**
     * Максимальный размер пула для каждого размера буфера.
     */
    private static final int MAX_POOL_SIZE = 100;
    
    /**
     * Максимальный размер буфера, который будет храниться в пуле (64KB).
     * Буферы большего размера не кэшируются, так как они редко используются.
     */
    private static final int MAX_CACHED_SIZE = 64 * 1024;
    
    /**
     * Минимальный размер буфера для кэширования (256 bytes).
     * Меньшие буферы не кэшируются, так как их создание дешево.
     */
    private static final int MIN_CACHED_SIZE = 256;
    
    /**
     * Пул буферов, организованный по размерам.
     * Используется ConcurrentLinkedQueue для потокобезопасности.
     */
    private final Queue<ByteBuffer>[] pools;
    
    /**
     * Индекс для быстрого доступа к пулу нужного размера.
     */
    private static final int POOL_INDEX_SHIFT = 8; // 256 bytes шаг
    
    @SuppressWarnings("unchecked")
    public ByteBufferPool() {
        // Создаем пулы для размеров от MIN_CACHED_SIZE до MAX_CACHED_SIZE
        int poolCount = (MAX_CACHED_SIZE / (1 << POOL_INDEX_SHIFT)) + 1;
        pools = new Queue[poolCount];
        for (int i = 0; i < poolCount; i++) {
            pools[i] = new ConcurrentLinkedQueue<>();
        }
    }
    
    /**
     * Получить буфер из пула или создать новый, если пул пуст.
     * 
     * @param size требуемый размер буфера
     * @return ByteBuffer с требуемым размером
     */
    public ByteBuffer acquire(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive: " + size);
        }
        
        ProtocolMetrics.incrementBuffersAcquired();
        
        // Для маленьких или очень больших буферов не используем пул
        if (size < MIN_CACHED_SIZE || size > MAX_CACHED_SIZE) {
            ProtocolMetrics.incrementBuffersCreated();
            return ByteBuffer.allocate(size);
        }
        
        int poolIndex = getPoolIndex(size);
        Queue<ByteBuffer> pool = pools[poolIndex];
        
        ByteBuffer buffer = pool.poll();
        if (buffer == null) {
            // Создаем буфер с округлением до следующего размера
            int allocatedSize = roundUpToPoolSize(size);
            buffer = ByteBuffer.allocate(allocatedSize);
            ProtocolMetrics.incrementBuffersCreated();
        } else {
            // Проверяем, что буфер достаточно большой
            if (buffer.capacity() < size) {
                // Буфер слишком маленький, создаем новый
                buffer = ByteBuffer.allocate(roundUpToPoolSize(size));
                ProtocolMetrics.incrementBuffersCreated();
            } else {
                buffer.clear();
            }
        }
        
        return buffer;
    }
    
    /**
     * Вернуть буфер в пул для переиспользования.
     * 
     * @param buffer буфер для возврата
     */
    public void release(ByteBuffer buffer) {
        if (buffer == null) {
            return;
        }
        
        ProtocolMetrics.incrementBuffersReleased();
        
        int capacity = buffer.capacity();
        
        // Не кэшируем маленькие или очень большие буферы
        if (capacity < MIN_CACHED_SIZE || capacity > MAX_CACHED_SIZE) {
            return;
        }
        
        int poolIndex = getPoolIndex(capacity);
        Queue<ByteBuffer> pool = pools[poolIndex];
        
        // Ограничиваем размер пула
        if (pool.size() < MAX_POOL_SIZE) {
            buffer.clear();
            pool.offer(buffer);
        }
    }
    
    /**
     * Получить индекс пула для заданного размера.
     */
    private int getPoolIndex(int size) {
        return (size - 1) >> POOL_INDEX_SHIFT;
    }
    
    /**
     * Округлить размер до следующего размера пула.
     */
    private int roundUpToPoolSize(int size) {
        int poolSize = (1 << POOL_INDEX_SHIFT);
        return ((size + poolSize - 1) / poolSize) * poolSize;
    }
    
    /**
     * Очистить все буферы из пула.
     * Полезно для освобождения памяти при необходимости.
     */
    public void clear() {
        for (Queue<ByteBuffer> pool : pools) {
            pool.clear();
        }
    }
    
    /**
     * Получить статистику использования пула.
     * 
     * @return массив с количеством буферов в каждом пуле
     */
    public int[] getStatistics() {
        int[] stats = new int[pools.length];
        for (int i = 0; i < pools.length; i++) {
            stats[i] = pools[i].size();
        }
        return stats;
    }
}

