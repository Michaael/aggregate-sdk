package com.tibbo.aggregate.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Простая реализация LRU (Least Recently Used) кэша.
 * 
 * <p>Этот класс реализует кэш с ограничением размера, который автоматически
 * удаляет наименее недавно использованные элементы при превышении максимального размера.</p>
 * 
 * <p><b>Особенности:</b></p>
 * <ul>
 *   <li>Потокобезопасность: НЕ потокобезопасен, для многопоточного использования
 *       необходимо использовать внешнюю синхронизацию или ConcurrentHashMap</li>
 *   <li>Производительность: O(1) для операций get() и put()</li>
 *   <li>Память: Использует LinkedHashMap для отслеживания порядка использования</li>
 * </ul>
 * 
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * LRUCache<String, Integer> cache = new LRUCache<>(100);
 * cache.put("key1", 1);
 * Integer value = cache.get("key1");
 * }</pre>
 * 
 * @param <K> тип ключа
 * @param <V> тип значения
 * @since 1.3.7
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
  private static final long serialVersionUID = 1L;
  
  private final int maxSize;
  
  /**
   * Создает новый LRU кэш с указанным максимальным размером.
   * 
   * @param maxSize максимальный размер кэша
   * @throws IllegalArgumentException если maxSize <= 0
   */
  public LRUCache(int maxSize)
  {
    super(16, 0.75f, true); // accessOrder = true для LRU поведения
    if (maxSize <= 0)
    {
      throw new IllegalArgumentException("Max size must be positive: " + maxSize);
    }
    this.maxSize = maxSize;
  }
  
  /**
   * Проверяет, нужно ли удалить наименее недавно использованный элемент.
   * Вызывается автоматически при добавлении нового элемента.
   * 
   * @param eldest наименее недавно использованный элемент
   * @return true, если элемент должен быть удален
   */
  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
  {
    return size() > maxSize;
  }
  
  /**
   * Получить максимальный размер кэша.
   * 
   * @return максимальный размер
   */
  public int getMaxSize()
  {
    return maxSize;
  }
  
  /**
   * Получить текущий размер кэша.
   * 
   * @return текущий размер
   */
  public int getCurrentSize()
  {
    return size();
  }
  
  /**
   * Проверить, заполнен ли кэш.
   * 
   * @return true, если кэш заполнен
   */
  public boolean isFull()
  {
    return size() >= maxSize;
  }
}

