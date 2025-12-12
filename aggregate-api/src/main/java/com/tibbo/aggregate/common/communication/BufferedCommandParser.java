package com.tibbo.aggregate.common.communication;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.WatchdogHolder;

public abstract class BufferedCommandParser<C extends Command> extends AbstractCommandParser<C>
{
  /**
   * Начальный размер буфера (1KB).
   * Оптимален для большинства команд.
   */
  private static final int INITIAL_BUFFER_SIZE = 1024;
  
  /**
   * Максимальный размер буфера.
   * Настраивается через системное свойство "aggregate.buffer.maxSize" (в мегабайтах).
   * По умолчанию: 1GB (для поддержки больших моделей).
   * Можно установить через -Daggregate.buffer.maxSize=2048 для 2GB и т.д.
   * Защита от чрезмерного использования памяти.
   */
  private static final int MAX_BUFFER_SIZE = getMaxBufferSize();
  
  /**
   * Получить максимальный размер буфера из системного свойства или использовать значение по умолчанию.
   * 
   * @return максимальный размер буфера в байтах
   */
  private static int getMaxBufferSize() {
    String prop = System.getProperty("aggregate.buffer.maxSize");
    if (prop != null) {
      try {
        long sizeMB = Long.parseLong(prop);
        if (sizeMB > 0 && sizeMB <= Integer.MAX_VALUE / (1024L * 1024L)) {
          return (int)(sizeMB * 1024L * 1024L);
        } else if (sizeMB > 0) {
          // Ограничиваем максимальным значением int (около 2GB)
          Log.COMMANDS.warn("Requested buffer size " + sizeMB + "MB exceeds maximum, using " + (Integer.MAX_VALUE / (1024 * 1024)) + "MB");
          return Integer.MAX_VALUE / 2; // Безопасное значение около 1GB
        }
      } catch (NumberFormatException e) {
        Log.COMMANDS.warn("Invalid value for aggregate.buffer.maxSize: " + prop + ", using default 512MB");
      }
    }
    // Значение по умолчанию: 1GB (достаточно для большинства больших моделей)
    return 1024 * 1024 * 1024;
  }
  
  /**
   * Коэффициент увеличения буфера при необходимости (удвоение).
   */
  private static final int BUFFER_GROWTH_FACTOR = 2;
  
  /**
   * Динамический буфер, который увеличивается при необходимости.
   * Начинается с INITIAL_BUFFER_SIZE и может расти до MAX_BUFFER_SIZE.
   */
  protected ByteBuffer buffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
  protected ReadableByteChannel channel;
  
  public BufferedCommandParser(ReadableByteChannel channel)
  {
    super();
    this.channel = channel;
    buffer.flip();
  }
  
  /**
   * Обеспечить достаточную емкость буфера.
   * Увеличивает размер буфера при необходимости, используя стратегию удвоения.
   * 
   * @param requiredCapacity требуемая емкость
   */
  protected void ensureBufferCapacity(int requiredCapacity)
  {
    if (requiredCapacity > MAX_BUFFER_SIZE)
    {
      throw new IllegalStateException("Required buffer capacity (" + requiredCapacity + 
          " bytes) exceeds maximum allowed size (" + MAX_BUFFER_SIZE + " bytes)");
    }
    
    if (buffer.capacity() < requiredCapacity)
    {
      int newCapacity = Math.max(buffer.capacity() * BUFFER_GROWTH_FACTOR, requiredCapacity);
      // Ограничиваем максимальным размером
      newCapacity = Math.min(newCapacity, MAX_BUFFER_SIZE);
      
      // Сохраняем текущую позицию и лимит
      int position = buffer.position();
      int limit = buffer.limit();
      
      // Создаем новый буфер большего размера
      ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
      
      // Копируем данные из старого буфера
      buffer.flip();
      newBuffer.put(buffer);
      newBuffer.position(position);
      newBuffer.limit(Math.min(limit, newCapacity));
      
      buffer = newBuffer;
    }
  }
  
  protected boolean commandCompleted()
  {
    while (buffer.hasRemaining())
    {
      try
      {
        byte cur = buffer.get();
        
        if (commandEnded(cur))
        {
          return true;
        }
      }
      catch (BufferUnderflowException ex)
      {
        Log.COMMANDS.debug("Buffer underflow error in " + toString(), ex);
        return false;
      }
    }
    
    return false;
  }
  
  @Override
  public C readCommand() throws IOException, DisconnectionException, SyntaxErrorException
  {
    int read;
    
    if (commandCompleted())
    {
      return buildCommand();
    }
    
    while (true)
    {
      // Обеспечиваем достаточную емкость буфера перед чтением
      // Если буфер заполнен, увеличиваем его размер
      if (!buffer.hasRemaining())
      {
        ensureBufferCapacity(buffer.capacity() * BUFFER_GROWTH_FACTOR);
      }
      
      buffer.clear();

      WatchdogHolder.getInstance().awaitForEnoughMemory();

      read = getChannel().read(buffer);
      
      buffer.flip();
      
      if (read > 0)
      {
        CommandParserListener listener = getListener();
        if (listener != null)
        {
          listener.newDataReceived();
        }
      }
      else
      {
        break;
      }
      
      if (commandCompleted())
      {
        return buildCommand();
      }
    }
    
    if (read == -1)
    {
      getChannel().close();
      throw new DisconnectionException(Cres.get().getString("disconnected"));
    }
    
    return buildCommand();
  }
  
  protected abstract C buildCommand() throws SyntaxErrorException;
  
  protected abstract boolean commandEnded(byte cur);
  
  public ReadableByteChannel getChannel()
  {
    return channel;
  }
}
