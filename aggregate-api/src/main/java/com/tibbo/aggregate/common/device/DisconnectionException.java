package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.*;

public class DisconnectionException extends AggreGateException
{
  public DisconnectionException(String message)
  {
    super(message);
  }
  
  public DisconnectionException(Throwable cause)
  {
    super(cause);
  }
  
  public DisconnectionException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
