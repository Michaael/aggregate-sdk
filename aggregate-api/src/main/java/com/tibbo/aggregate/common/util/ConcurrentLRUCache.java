package com.tibbo.aggregate.common.util;

import java.util.Collections;
import java.util.Map;

/**
 * Потокобезопасная реализация LRU (Least Recently Used) кэша.
 * 
 * <p>Этот класс обеспечивает потокобезопасный доступ к LRU кэшу,
 * используя синхронизацию для всех операций.</p>
 * 
 * <p><b>Особенности:</b></p>
 * <ul>
 *   <li>Потокобезопасность: Все операции синхронизированы</li>
 *   <li>Производительность: O(1) для операций get() и put()</li>
 *   <li>Память: Автоматически удаляет наименее недавно использованные элементы</li>
 * </ul>
 * 
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * ConcurrentLRUCache<String, List<String>> cache = new ConcurrentLRUCache<>(1000);
 * cache.put("key1", Arrays.asList("value1", "value2"));
 * List<String> value = cache.get("key1");
 * }</pre>
 * 
 * @param <K> тип ключа
 * @param <V> тип значения
 * @since 1.3.7
 */
public class ConcurrentLRUCache<K, V>
{
  private final LRUCache<K, V> cache;
  private final Map<K, V> synchronizedCache;
  
  /**
   * Создает новый потокобезопасный LRU кэш с указанным максимальным размером.
   * 
   * @param maxSize максимальный размер кэша
   * @throws IllegalArgumentException если maxSize <= 0
   */
  public ConcurrentLRUCache(int maxSize)
  {
    this.cache = new LRUCache<>(maxSize);
    this.synchronizedCache = Collections.synchronizedMap(cache);
  }
  
  /**
   * Получить значение по ключу.
   * 
   * @param key ключ
   * @return значение или null, если ключ не найден
   */
  public V get(K key)
  {
    return synchronizedCache.get(key);
  }
  
  /**
   * Поместить значение в кэш.
   * 
   * @param key ключ
   * @param value значение
   * @return предыдущее значение или null, если ключа не было
   */
  public V put(K key, V value)
  {
    return synchronizedCache.put(key, value);
  }
  
  /**
   * Проверить, содержит ли кэш указанный ключ.
   * 
   * @param key ключ
   * @return true, если ключ присутствует в кэше
   */
  public boolean containsKey(K key)
  {
    return synchronizedCache.containsKey(key);
  }
  
  /**
   * Удалить значение из кэша.
   * 
   * @param key ключ
   * @return удаленное значение или null, если ключа не было
   */
  public V remove(K key)
  {
    return synchronizedCache.remove(key);
  }
  
  /**
   * Очистить кэш.
   */
  public void clear()
  {
    synchronizedCache.clear();
  }
  
  /**
   * Получить текущий размер кэша.
   * 
   * @return текущий размер
   */
  public int size()
  {
    return synchronizedCache.size();
  }
  
  /**
   * Получить максимальный размер кэша.
   * 
   * @return максимальный размер
   */
  public int getMaxSize()
  {
    return cache.getMaxSize();
  }
  
  /**
   * Проверить, заполнен ли кэш.
   * 
   * @return true, если кэш заполнен
   */
  public boolean isFull()
  {
    return cache.isFull();
  }
}

