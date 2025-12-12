package com.tibbo.aggregate.common;

public class AggreGateRuntimeException extends RuntimeException
{
  public AggreGateRuntimeException(String message)
  {
    super(message);
  }
  
  public AggreGateRuntimeException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public AggreGateRuntimeException(Throwable e)
  {
    super(e);
  }
}
