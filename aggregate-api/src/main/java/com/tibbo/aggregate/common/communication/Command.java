package com.tibbo.aggregate.common.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.StringUtils;

public abstract class Command extends ByteArrayOutputStream
{
  private Long timeout;
  protected boolean completed = false;
  
  public Command()
  {
    
  }
  
  public Command(String data)
  {
    super();
    add(data);
  }
  
  public Command(byte[] data)
  {
    super();
    write(data, 0, data.length);
  }
  
  public String header()
  {
    return null;
  }
  
  public String footer()
  {
    return null;
  }
  
  public String getContent()
  {
    try
    {
      return super.toString(StringUtils.UTF8_CHARSET.name());
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new IllegalStateException();
    }
  }
  
  public boolean isContentEmpty()
  {
    return this.buf.length <= 0;
  }
  
  public void add(String data)
  {
    try
    {
      write(data.getBytes(StringUtils.UTF8_CHARSET));
    }
    catch (IOException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public void send(ByteChannel byteChannel) throws DisconnectionException, IOException
  {
    send(byteChannel, true);
  }
  
  public void send(ByteChannel byteChannel, boolean encapsulate) throws DisconnectionException, IOException
  {
    if (byteChannel == null || !byteChannel.isOpen())
    {
      throw new DisconnectionException(Cres.get().getString("disconnected"));
    }
    
    try
    {
      String header = encapsulate ? header() : null;
      String footer = encapsulate ? footer() : null;
      
      int size = (header != null ? header.length() : 0) + size() + (footer != null ? footer.length() : 0);
      
      ByteBuffer buff = ByteBuffer.allocate(size);
      buff.clear();
      if (header != null)
      {
        buff.put(header.getBytes(StringUtils.UTF8_CHARSET));
      }
      buff.put(toByteArray());
      if (footer != null)
      {
        buff.put(footer.getBytes(StringUtils.UTF8_CHARSET));
      }
      buff.flip();
      
      int sent = 0;
      
      do
      {
        sent += byteChannel.write(buff);
      }
      while (sent < size);
    }
    catch (SocketDisconnectionException | ClosedChannelException ex)
    {
      throw new DisconnectionException(Cres.get().getString("disconnected"), ex);
    }
  }
  
  public synchronized void send(OutputStream stream) throws IOException
  {
    String header = header();
    if (header != null)
    {
      stream.write(header.getBytes(StringUtils.UTF8_CHARSET));
    }
    stream.write(toByteArray());
    String footer = footer();
    if (footer != null)
    {
      stream.write(footer.getBytes(StringUtils.UTF8_CHARSET));
    }
  }
  
  public String getId()
  {
    return null;
  }
  
  public boolean isAsync()
  {
    return false;
  }
  
  public Long getTimeout()
  {
    return timeout;
  }
  
  public void setTimeout(Long timeout)
  {
    this.timeout = timeout;
  }
  
  public void complete()
  {
    completed = true;
  }
}
