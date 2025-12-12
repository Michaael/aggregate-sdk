package com.tibbo.aggregate.common.communication;

import java.io.*;

public class SocketDisconnectionException extends IOException
{
  
  public SocketDisconnectionException()
  {
  }
  
  public SocketDisconnectionException(String message)
  {
    super(message);
  }
  
  public SocketDisconnectionException(Throwable cause)
  {
    super(cause);
  }
  
  public SocketDisconnectionException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
