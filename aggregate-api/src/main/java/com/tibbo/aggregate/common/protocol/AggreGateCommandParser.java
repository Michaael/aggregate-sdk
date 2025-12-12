package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.nio.channels.*;
import java.util.zip.*;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.communication.*;
import com.tibbo.aggregate.common.util.*;

public class AggreGateCommandParser extends SimpleCommandParser<IncomingAggreGateCommand>
{
  private static final byte TYPE_COMPRESSED = 1;
  
  /**
   * Максимальный размер команды.
   * Настраивается через системное свойство "aggregate.command.maxSize" (в мегабайтах).
   * По умолчанию: 1GB (для поддержки больших моделей).
   * Можно установить через -Daggregate.command.maxSize=2048 для 2GB и т.д.
   * Команды больше этого размера будут отклонены для защиты от OutOfMemoryError.
   */
  private static final int MAX_COMMAND_SIZE = getMaxCommandSize();
  
  /**
   * Получить максимальный размер команды из системного свойства или использовать значение по умолчанию.
   * 
   * @return максимальный размер команды в байтах
   */
  private static int getMaxCommandSize() {
    String prop = System.getProperty("aggregate.command.maxSize");
    if (prop != null) {
      try {
        long sizeMB = Long.parseLong(prop);
        if (sizeMB > 0 && sizeMB <= Integer.MAX_VALUE / (1024L * 1024L)) {
          return (int)(sizeMB * 1024L * 1024L);
        } else if (sizeMB > 0) {
          // Ограничиваем максимальным значением int (около 2GB)
          Log.PROTOCOL.warn("Requested command size " + sizeMB + "MB exceeds maximum, using " + (Integer.MAX_VALUE / (1024 * 1024)) + "MB");
          return Integer.MAX_VALUE / 2; // Безопасное значение около 1GB
        }
      } catch (NumberFormatException e) {
        Log.PROTOCOL.warn("Invalid value for aggregate.command.maxSize: " + prop + ", using default 512MB");
      }
    }
    // Значение по умолчанию: 1GB (достаточно для большинства больших моделей)
    return 1024 * 1024 * 1024;
  }
  
  /**
   * Буфер для декомпрессии данных.
   * Размер 1024 байта оптимален для большинства случаев.
   */
  public final byte[] decompressor_buffer = new byte[1024];
  
  /**
   * ThreadLocal Inflater для потокобезопасной декомпрессии.
   * Каждый поток имеет свой собственный Inflater, что исключает
   * проблемы с многопоточностью и снижает количество аллокаций.
   */
  private final ThreadLocal<Inflater> decompressor = 
      ThreadLocal.withInitial(Inflater::new);
  
  protected CommandData commandData;
  private ProtocolVersion version = ProtocolVersion.V2;
  
  public AggreGateCommandParser(ReadableByteChannel channel)
  {
    this(channel, AggreGateCommand.START_CHAR, AggreGateCommand.END_CHAR);
  }
  
  public AggreGateCommandParser(ReadableByteChannel channel, byte startChar, byte endChar)
  {
    super(channel, startChar, endChar);
    commandData = new CommandData(startChar);
  }
  
  @Override
  protected IncomingAggreGateCommand buildCommand() throws SyntaxErrorException
  {
    if (ProtocolVersion.V2.equals(version))
      return super.buildCommand();
    
    IncomingAggreGateCommand commandFrom = buildCommandFrom(commandData);
    reset();
    return commandFrom;
  }
  
  @Override
  protected boolean commandEnded(byte cur)
  {
    if (ProtocolVersion.V2.equals(version))
      return super.commandEnded(cur);
    
    commandData.addNextByte(cur);
    return commandData.isCompleted();
  }
  
  @Override
  public void reset()
  {
    super.reset();
    if (commandData != null)
      commandData.reset();
  }
  
  @Override
  protected IncomingAggreGateCommand createCommandFromBufferContent() throws SyntaxErrorException
  {
    return new IncomingAggreGateCommand(clearData());
  }
  
  public void setVersion(ProtocolVersion version)
  {
    this.version = version;
  }
  
  protected IncomingAggreGateCommand buildCommandFrom(CommandData commandData) throws SyntaxErrorException
  {
    if (!commandData.isCompleted())
    {
      return null;
    }
    
    CommandData.Body body = commandData.getBody();
    
    // Проверка максимального размера команды
    int bodyLength = body.getContents().length;
    if (bodyLength > MAX_COMMAND_SIZE)
    {
      ProtocolMetrics.incrementOversizedCommandsRejected();
      throw new SyntaxErrorException("Command size (" + bodyLength + 
          " bytes) exceeds maximum allowed size (" + MAX_COMMAND_SIZE + " bytes)");
    }
    
    // Обновляем метрики
    ProtocolMetrics.incrementCommandsProcessed();
    ProtocolMetrics.addCommandSize(bodyLength);
    
    byte[] data = body.getContents();
    
    boolean usesCompression = Util.equals(body.getType(), TYPE_COMPRESSED);
    
    if (usesCompression)
    {
      // Используем пул для переиспользования ByteArrayOutputStream
      ByteArrayOutputStream stream = ByteArrayOutputStreamPool.acquire();
      
      // Получаем ThreadLocal Inflater для текущего потока
      Inflater localDecompressor = decompressor.get();
      localDecompressor.reset();
      localDecompressor.setInput(data);
      
      try
      {
        // decompressor.finished() - is buggy and always return false, so using getRemaining() here
        while (localDecompressor.getRemaining() != 0)
        {
          int bytesWritten = localDecompressor.inflate(decompressor_buffer);
          stream.write(decompressor_buffer, 0, bytesWritten);
        }
      }
      catch (DataFormatException e)
      {
        throw new SyntaxErrorException("Error decompressing command.");
      }
      finally
      {
        // Сбрасываем Inflater для следующего использования
        localDecompressor.reset();
      }
      
      data = stream.toByteArray();
    }
    
    return new IncomingAggreGateCommand(data);
  }
  
}
