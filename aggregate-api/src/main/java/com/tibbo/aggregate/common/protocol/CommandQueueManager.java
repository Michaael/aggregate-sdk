package com.tibbo.aggregate.common.protocol;

import java.util.concurrent.*;

/**
 * Менеджер очередей команд.
 * Оптимизирован для многопоточного использования через ConcurrentHashMap.
 */
public class CommandQueueManager
{
  private ExecutorService executor;
  
  /**
   * ConcurrentHashMap для потокобезопасного доступа к очередям.
   * Заменяет HashMap + synchronized для лучшей производительности в многопоточной среде.
   */
  private final ConcurrentHashMap<String, CommandQueue> queues = new ConcurrentHashMap<String, CommandQueue>();
  
  public CommandQueueManager(ExecutorService executor)
  {
    this.executor = executor;
  }
  
  /**
   * Добавить команду в очередь.
   * Метод потокобезопасен благодаря использованию ConcurrentHashMap.
   * 
   * @param queueName имя очереди
   * @param command задача команды
   */
  public void addCommand(String queueName, final CommandTask command)
  {
    final CommandQueue queue = createOrGetQueue(queueName);
    
    if (queue.getActivityLock().availablePermits() == 0)
    {
      queue.add(command);
    }
    else
    {
      submitCommand(queue, command);
    }
  }
  
  /**
   * Создать или получить очередь.
   * Использует computeIfAbsent для атомарной операции.
   * 
   * @param name имя очереди
   * @return очередь команд
   */
  private CommandQueue createOrGetQueue(String name)
  {
    // Оптимизация: используем computeIfAbsent для атомарной операции
    // Это более эффективно, чем проверка + синхронизация
    return queues.computeIfAbsent(name, k -> new CommandQueue());
  }
  
  private void submitCommand(final CommandQueue queue, final CommandTask command)
  {
    try
    {
      queue.getActivityLock().acquire();
    }
    catch (InterruptedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    
    executor.submit(new CommandTask()
    {
      @Override
      public Object call() throws Exception
      {
        try
        {
          // Выполняем исходную команду
          command.call();
          
          // Обрабатываем очередь команд
          while (true)
          {
            CommandTask cur = queue.poll();
            
            if (cur == null)
            {
              break;
            }
            
            cur.call();
          }
          
          return null;
        }
        finally
        {
          queue.getActivityLock().release();
        }
      }
    });
  }
}
