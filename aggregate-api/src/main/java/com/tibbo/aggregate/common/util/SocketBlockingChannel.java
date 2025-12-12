package com.tibbo.aggregate.common.util;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class SocketBlockingChannel implements BlockingChannel
{
  private final Socket socket;
  
  private final InputStream is;
  private final OutputStream os;
  
  private BufferedOutputStream bos;
  
  private final ReadableByteChannel rbc;
  private final WritableByteChannel wbc;
  
  private final int sendBufferSize;
  private final ByteBuffer buf;
  
  private boolean usesCompression;
  
  private int bufferSize;
  
  public SocketBlockingChannel(Socket socket) throws IOException
  {
    this(socket, false, 0);
  }
  
  public SocketBlockingChannel(Socket socket, boolean compressData, int bufferSize) throws IOException
  {
    this.socket = socket;
    
    this.bufferSize = bufferSize;
    
    is = socket.getInputStream();
    os = socket.getOutputStream();
    
    rbc = Channels.newChannel(is);
    
    if (bufferSize > 0)
    {
      bos = new BufferedOutputStream(os, bufferSize);
      wbc = Channels.newChannel(bos);
    }
    else
    {
      wbc = Channels.newChannel(os);
    }
    
    setUsesCompression(compressData);
    
    sendBufferSize = socket.getSendBufferSize();
    
    buf = ByteBuffer.allocateDirect(sendBufferSize);
  }
  
  public SocketBlockingChannel()
  {
    // Creates an empty channel to be used in tests
    socket = null;
    is = null;
    os = null;
    bos = null;
    rbc = null;
    wbc = null;
    sendBufferSize = 0;
    
    buf = null;
  }
  
  @Override
  public int read(ByteBuffer dst) throws IOException
  {
    try
    {
      return rbc.read(dst);
    }
    catch (SocketTimeoutException e)
    {
      return 0;
    }
    catch (IOException ex)
    {
      throw ex;
    }
  }
  
  @Override
  public int write(ByteBuffer byteBuffer) throws IOException
  {
    int startPosition;
    
    int bytesWritten = 0;
    
    int expectedWrite;
    byte[] byteArray = byteBuffer.array();
    
    if (byteBuffer.remaining() == 0)
    {
      return 0;
    }
    
    for (startPosition = 0; startPosition < byteArray.length; startPosition += sendBufferSize)
    {
      final int numberOfBytesToSend = byteArray.length - startPosition;
      
      buf.clear();
      
      if (numberOfBytesToSend < sendBufferSize)
      {
        buf.put(byteArray, startPosition, numberOfBytesToSend);
        expectedWrite = numberOfBytesToSend;
      }
      else
      {
        buf.put(byteArray, startPosition, sendBufferSize);
        expectedWrite = sendBufferSize;
      }
      
      buf.flip();
      
      bytesWritten = wbc.write(buf);
      
      if (bufferSize == 0)
      {
        os.flush();
      }
      
      if (bytesWritten < expectedWrite)
      {
        break;
      }
    }
    
    if (startPosition > byteArray.length)
    {
      return byteArray.length;
    }
    else
    {
      return startPosition + bytesWritten;
    }
  }
  
  @Override
  public void flush() throws IOException
  {
    if (bos != null)
    {
      bos.flush();
    }
  }
  
  @Override
  public boolean isUsesCompression()
  {
    return usesCompression;
  }
  
  @Override
  public void setUsesCompression(boolean usesCompression)
  {
    this.usesCompression = usesCompression;
  }
  
  @Override
  public void close() throws IOException
  {
    try
    {
      socket.close();
    }
    catch (Exception ex)
    {
      throw new IOException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public boolean isOpen()
  {
    return !socket.isClosed() && socket.isConnected();
  }
  
  public Socket getSocket()
  {
    return socket;
  }
  
  @Override
  public String getChannelAddress()
  {
    return getSocket().getInetAddress().getHostAddress();
  }
}
