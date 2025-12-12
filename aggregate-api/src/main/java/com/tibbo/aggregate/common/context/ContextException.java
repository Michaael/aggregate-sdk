package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.*;

public class ContextException extends AggreGateException
{
  public ContextException(String message)
  {
    super(message);
  }
  
  public ContextException(Throwable cause)
  {
    super(cause);
  }
  
  public ContextException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public ContextException(String message, Throwable cause, String details)
  {
    super(message, cause, details);
  }
}
