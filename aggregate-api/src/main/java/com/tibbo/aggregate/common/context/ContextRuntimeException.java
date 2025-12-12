package com.tibbo.aggregate.common.context;

public class ContextRuntimeException extends RuntimeException
{
  public ContextRuntimeException(String message)
  {
    super(message);
  }
  
  public ContextRuntimeException(Throwable cause)
  {
    super(cause);
  }
  
  public ContextRuntimeException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
