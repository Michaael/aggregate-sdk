package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class OutgoingAggreGateCommand extends AggreGateCommand
{
  private static final byte[] CLIENT_COMMAND_SEPARATOR = AggreGateCommand.CLIENT_COMMAND_SEPARATOR.getBytes();
  
  protected int paramCount = 0;
  
  protected String id;
  
  protected boolean async;
  
  public OutgoingAggreGateCommand()
  {
    super();
  }
  
  @Override
  public String header()
  {
    return Character.toString((char) AggreGateCommand.START_CHAR);
  }
  
  @Override
  public String footer()
  {
    return Character.toString((char) AggreGateCommand.END_CHAR);
  }
  
  public OutgoingAggreGateCommand addParam(String param)
  {
    if (paramCount != 0)
    {
      write(CLIENT_COMMAND_SEPARATOR, 0, CLIENT_COMMAND_SEPARATOR.length);
    }
    
    if (paramCount == AggreGateCommand.INDEX_ID)
    {
      this.id = param;
    }
    
    try
    {
      if (param.length() > TransferEncodingHelper.LARGE_DATA_SIZE)
      {
        writeLargeData(param);
      }
      else
      {
        byte[] paramBytes = param.getBytes(StringUtils.UTF8_CHARSET);
        ensureCapacity(size() + paramBytes.length);
        write(paramBytes);
      }
    }
    catch (Exception ex)
    {
      Log.COMMANDS.warn(ex.getMessage(), ex);
    }
    
    paramCount++;
    return this;
  }
  
  /**
   * Обеспечить достаточную емкость буфера.
   * Использует стратегию удвоения размера для минимизации количества копирований.
   * 
   * @param minCapacity минимальная требуемая емкость
   */
  private void ensureCapacity(int minCapacity)
  {
    if (minCapacity > buf.length)
    {
      // Удваиваем размер буфера, но не меньше требуемого размера
      int newCapacity = Math.max(buf.length * 2, minCapacity);
      // Ограничиваем максимальный размер для защиты от чрезмерного использования памяти
      int maxCapacity = 100 * 1024 * 1024; // 100MB
      if (newCapacity > maxCapacity)
      {
        newCapacity = Math.max(minCapacity, maxCapacity);
      }
      buf = Arrays.copyOf(buf, newCapacity);
    }
  }
  
  private void writeLargeData(String param) throws IOException
  {
    // Оптимизация: упрощенный расчет размера без создания substring
    // Приблизительная оценка: UTF-8 может быть до 4 байт на символ, но для ASCII это 1 байт
    // Используем консервативную оценку: 2 байта на символ для смешанного контента
    int estimatedSize = param.length() * 2;
    ensureCapacity(size() + estimatedSize);
    
    // Оптимизация: работаем напрямую с байтами, избегая множественных substring
    byte[] paramBytes = param.getBytes(StringUtils.UTF8_CHARSET);
    int chunkSize = TransferEncodingHelper.MB;
    
    // Записываем данные по частям, если они очень большие
    if (paramBytes.length > chunkSize)
    {
      for (int i = 0; i < paramBytes.length; i += chunkSize)
      {
        int end = Math.min(i + chunkSize, paramBytes.length);
        write(paramBytes, i, end - i);
      }
    }
    else
    {
      write(paramBytes);
    }
  }
  
  @Override
  public String getId()
  {
    return id;
  }
  
  public void setAsync(boolean async)
  {
    this.async = async;
  }
  
  @Override
  public boolean isAsync()
  {
    return async;
  }
  
  public void constructReply(String id, String code)
  {
    if (paramCount > 0)
    {
      throw new IllegalStateException("Can't construct reply - parameters already added to command");
    }
    
    addParam(String.valueOf(AggreGateCommand.COMMAND_CODE_REPLY));
    addParam(id);
    addParam(code);
  }
  
  public void constructReply(String id, String code, String message)
  {
    constructReply(id, code);
    addParam(TransferEncodingHelper.encode(message));
  }
  
  public void constructReply(String id, String code, String message, String details)
  {
    constructReply(id, code, message);
    addParam(TransferEncodingHelper.encode(details));
  }
  
  public void constructEvent(String context, String name, int level, String encodedDataTable, Long eventId, Date creationtime, Integer listener)
  {
    // Оптимизация: используем константу вместо new String()
    this.id = "";
    
    setAsync(true);
    
    addParam(String.valueOf(AggreGateCommand.COMMAND_CODE_MESSAGE));
    addParam(this.id);
    addParam(String.valueOf(AggreGateCommand.MESSAGE_CODE_EVENT));
    addParam(context);
    addParam(name);
    addParam(String.valueOf(level));
    addParam(eventId != null ? eventId.toString() : "");
    addParam(listener != null ? listener.toString() : "");
    addParam(encodedDataTable);
    addParam(creationtime != null ? String.valueOf(creationtime.getTime()) : "");
  }
}
