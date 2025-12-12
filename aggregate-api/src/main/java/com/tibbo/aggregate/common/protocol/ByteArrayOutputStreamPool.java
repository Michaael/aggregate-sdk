package com.tibbo.aggregate.common.protocol;

import java.io.ByteArrayOutputStream;

/**
 * Пул для переиспользования ByteArrayOutputStream объектов.
 * Снижает количество аллокаций при обработке команд протокола.
 * 
 * <p>Использует ThreadLocal для потокобезопасности и избежания
 * синхронизации между потоками.</p>
 */
public class ByteArrayOutputStreamPool {
    
    /**
     * Начальный размер буфера для ByteArrayOutputStream (4KB).
     * Это типичный размер для большинства команд.
     */
    private static final int INITIAL_BUFFER_SIZE = 4096;
    
    /**
     * ThreadLocal пул для каждого потока.
     * Каждый поток имеет свой собственный ByteArrayOutputStream,
     * что исключает необходимость синхронизации.
     */
    private static final ThreadLocal<ByteArrayOutputStream> pool = 
        ThreadLocal.withInitial(() -> new ByteArrayOutputStream(INITIAL_BUFFER_SIZE));
    
    /**
     * Получить ByteArrayOutputStream из пула.
     * Буфер автоматически сбрасывается перед использованием.
     * 
     * @return готовый к использованию ByteArrayOutputStream
     */
    public static ByteArrayOutputStream acquire() {
        ProtocolMetrics.incrementStreamsAcquired();
        ByteArrayOutputStream stream = pool.get();
        stream.reset();
        return stream;
    }
    
    /**
     * Получить ByteArrayOutputStream с предварительно заданным размером.
     * Если текущий буфер меньше требуемого размера, создается новый.
     * 
     * @param minSize минимальный требуемый размер
     * @return ByteArrayOutputStream с достаточным размером
     */
    public static ByteArrayOutputStream acquire(int minSize) {
        ProtocolMetrics.incrementStreamsAcquired();
        ByteArrayOutputStream stream = pool.get();
        
        // Если текущий буфер слишком маленький, создаем новый
        if (stream.size() < minSize) {
            // Создаем новый с запасом (удваиваем размер)
            stream = new ByteArrayOutputStream(Math.max(minSize * 2, INITIAL_BUFFER_SIZE));
            pool.set(stream);
        } else {
            stream.reset();
        }
        
        return stream;
    }
    
    /**
     * Вернуть ByteArrayOutputStream в пул.
     * В текущей реализации это не требуется, так как используется ThreadLocal,
     * но метод оставлен для совместимости и возможных будущих улучшений.
     * 
     * @param stream поток для возврата (не используется в текущей реализации)
     */
    public static void release(ByteArrayOutputStream stream) {
        // В ThreadLocal реализации освобождение происходит автоматически
        // при следующем вызове acquire()
    }
    
    /**
     * Очистить пул для текущего потока.
     * Полезно для освобождения памяти при завершении работы потока.
     */
    public static void clear() {
        pool.remove();
    }
}

