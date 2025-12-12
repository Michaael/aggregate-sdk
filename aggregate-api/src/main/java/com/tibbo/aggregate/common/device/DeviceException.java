package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.*;

public class DeviceException extends AggreGateException
{
  public DeviceException(String message)
  {
    super(message);
  }
  
  public DeviceException(Throwable cause)
  {
    super(cause);
  }
  
  public DeviceException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
