package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.*;

public class EventHandlingException extends AggreGateException
{
  public EventHandlingException(String message)
  {
    super(message);
  }
  
  public EventHandlingException(Throwable cause)
  {
    super(cause);
  }
  
  public EventHandlingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
