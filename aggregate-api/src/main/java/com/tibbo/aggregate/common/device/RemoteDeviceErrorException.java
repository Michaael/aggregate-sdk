package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.*;

public class RemoteDeviceErrorException extends AggreGateException
{
  public RemoteDeviceErrorException(String message)
  {
    super(message);
  }
  
  public RemoteDeviceErrorException(Throwable cause)
  {
    super(cause);
  }
  
  public RemoteDeviceErrorException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public RemoteDeviceErrorException(String message, String details)
  {
    super(message, details);
  }
}
