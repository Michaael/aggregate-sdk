package com.tibbo.aggregate.common.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.function.Function;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.communication.Command;
import com.tibbo.aggregate.common.communication.CommandWriter;
import com.tibbo.aggregate.common.communication.SocketDisconnectionException;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.BlockingChannel;
import com.tibbo.aggregate.common.util.StringUtils;

public class DefaultCommandWriter<C extends Command> implements CommandWriter<C>
{
  /**
   * Пул буферов для переиспользования ByteBuffer объектов.
   * Используется статический экземпляр для всех писателей.
   */
  private static final ByteBufferPool bufferPool = new ByteBufferPool();
  
  /**
   * Получить экземпляр пула буферов.
   * Используется для доступа к пулу из подклассов.
   * 
   * @return экземпляр ByteBufferPool
   */
  protected static ByteBufferPool getBufferPool()
  {
    return bufferPool;
  }
  
  @Override
  public void write(C command, BlockingChannel channel, boolean needsFlushing) throws IOException, DisconnectionException
  {
    command.complete();

    ByteBuffer byteBuffer = composeByteBuffer(command, channel, C::toByteArray);

    try
    {
      writeBufferToChannel(byteBuffer, command, channel, needsFlushing);
    }
    finally
    {
      // Возвращаем буфер в пул после использования
      bufferPool.release(byteBuffer);
    }

    log(command, ProtocolVersion.V2);
  }

  protected ByteBuffer composeByteBuffer(C command,
                                         BlockingChannel channel,
                                         Function<C, byte[]> commandBodyExtractor)
          throws DisconnectionException
  {
    if (channel == null || !channel.isOpen())
    {
      throw new DisconnectionException(Cres.get().getString("disconnected"));
    }

    String header = command.header();
    String footer = command.footer();

    byte[] commandBody = commandBodyExtractor.apply(command);

    int bufferSize = (header != null ? header.length() : 0) + commandBody.length + (footer != null ? footer.length() : 0);

    // Используем пул буферов вместо прямого выделения
    ByteBuffer buff = bufferPool.acquire(bufferSize);
    
    if (header != null)
    {
      buff.put(header.getBytes(StringUtils.UTF8_CHARSET));
    }
    buff.put(commandBody);
    if (footer != null)
    {
      buff.put(footer.getBytes(StringUtils.UTF8_CHARSET));
    }
    buff.flip();

    return buff;
  }

  public void writeBufferToChannel(ByteBuffer buff, C command, BlockingChannel channel, boolean needsFlushing)
          throws DisconnectionException, IOException
  {
    try
    {
      // syncing is needed to ensure that no thread invaded into writing to the channel, or before it has been flushed
      //noinspection SynchronizationOnLocalVariableOrMethodParameter
      synchronized (channel)
      {
        int sent = 0;
        do
        {
          sent += channel.write(buff);
        }
        while (sent < buff.capacity());

        if (needsFlushing && !command.isAsync())
        {
          channel.flush(); // Delivering replies immediately
        }
      }
    }
    catch (SocketDisconnectionException | ClosedChannelException ex)
    {
      throw new DisconnectionException(Cres.get().getString("disconnected"), ex);
    }
  }

  @Override
  public void setVersion(ProtocolVersion version)
  {
  }
  
  @Override
  public void setVersionAfterNextWrite(ProtocolVersion version)
  {
  }
  
  public void log(C command, ProtocolVersion protocolVersion)
  {
    if (Log.COMMANDS.isTraceEnabled())
    {
      Log.COMMANDS.trace("Use " + protocolVersion + " protocol version to send: " + command);
    }
  }
}
