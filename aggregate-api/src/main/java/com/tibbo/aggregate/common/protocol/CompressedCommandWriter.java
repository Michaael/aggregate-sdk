package com.tibbo.aggregate.common.protocol;

import static com.tibbo.aggregate.common.protocol.ProtocolVersion.V2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.BlockingChannel;

public class CompressedCommandWriter<C extends OutgoingAggreGateCommand> extends DefaultCommandWriter<C>
{
  public static final byte TYPE_RAW = 0;
  public static final byte TYPE_COMPRESSED = 1;
  
  // Tested that commands more than 128 bytes usually has at least 1.2 compression ratio. Smaller commands could have negative compression effect.
  public static final int MINIMAL_COMMAND_SIZE_TO_COMPRESS = 128;
  
  private final ThreadLocal<Deflater> compressor = ThreadLocal.withInitial(Deflater::new);

  private volatile ProtocolVersion version = V2; //volatile to let neighbour threads to know of version change instantly
  private volatile ProtocolVersion versionAfterNextWrite = version;
  
  @Override
  public void write(C command, BlockingChannel channel, boolean needsFlushing) throws IOException, DisconnectionException
  {
    switch (version)
    {
      case V2:
        // When we open a connection the first message should be sent in 2 version of AG protocol for compatibility reasons
        super.write(command, channel, needsFlushing);
        break;

      case V3:
      case V4:
      default:
        command.complete();

        ByteBuffer byteBuffer = composeByteBuffer(command, channel, cmd -> extractCommandBody(cmd, channel));

        try
        {
          writeBufferToChannel(byteBuffer, command, channel, needsFlushing);
        }
        finally
        {
          // Возвращаем буфер в пул после использования
          // Используем protected метод из родительского класса для доступа к bufferPool
          releaseBuffer(byteBuffer);
        }

        log(command, version);
    }

    version = versionAfterNextWrite;
  }
  
  /**
   * Освободить буфер, возвращая его в пул.
   * Метод доступен для переопределения в подклассах.
   */
  protected void releaseBuffer(ByteBuffer buffer)
  {
    // Используем тот же пул, что и в DefaultCommandWriter
    DefaultCommandWriter.getBufferPool().release(buffer);
  }

  protected byte[] extractCommandBody(OutgoingAggreGateCommand command, BlockingChannel channel)
  {
    boolean commandRequiresCompression = command.size() > MINIMAL_COMMAND_SIZE_TO_COMPRESS;
    boolean channelUsesCompression = channel.isUsesCompression();
    boolean compress = channelUsesCompression && commandRequiresCompression;

    byte type = compress ? TYPE_COMPRESSED : TYPE_RAW;
    
    byte[] contents = getContents(command, compress);
    
    int lengthInBytes = 4;
    int typeInBytes = 1;
    int bodyLength = typeInBytes + contents.length;
    
    // Используем пул буферов вместо прямого выделения
    ByteBufferPool pool = getBufferPool();
    ByteBuffer byteBuffer = pool.acquire(lengthInBytes + bodyLength);
    
    try
    {
      byteBuffer.putInt(bodyLength);
      byteBuffer.put(type);
      byteBuffer.put(contents);
      
      byte[] result = new byte[byteBuffer.position()];
      byteBuffer.flip();
      byteBuffer.get(result);
      return result;
    }
    finally
    {
      // Возвращаем буфер в пул
      pool.release(byteBuffer);
    }
  }
  
  private byte[] getContents(OutgoingAggreGateCommand command, boolean compress)
  {
    byte[] commandBytes = command.toByteArray();
    
    if (compress)
    {
      int commandLength = commandBytes.length;
      // Используем пул для переиспользования ByteArrayOutputStream
      ByteArrayOutputStream stream = ByteArrayOutputStreamPool.acquire(commandLength);

      Deflater localCompressor = compressor.get();    // the instance that's not shared with other threads
      localCompressor.reset();
      localCompressor.setInput(commandBytes, 0, command.size());
      localCompressor.finish();
      
      while (!localCompressor.finished())
      {
        byte[] localBuffer = new byte[2048];
        int bytesWritten = localCompressor.deflate(localBuffer);
        stream.write(localBuffer, 0, bytesWritten);
      }
      
      byte[] compressedCommandBytes = stream.toByteArray();
      
      if (Log.COMMANDS.isTraceEnabled())
      {
        int compressedLength = compressedCommandBytes.length;
        float compressionRatio = (float) commandLength / compressedLength;
        
        Log.COMMANDS.trace("Raw size: " + commandLength + ". \tCompressed size: " + compressedLength + ". \tCompression ratio: " + compressionRatio + ".");
      }
      
      return compressedCommandBytes;
    }
    else
    {
      return commandBytes;
    }
  }
  
  @Override
  public void setVersion(ProtocolVersion version)
  {
    this.version = version;
    versionAfterNextWrite = version;
  }
  
  @Override
  public void setVersionAfterNextWrite(ProtocolVersion version)
  {
    versionAfterNextWrite = version;
  }
}
