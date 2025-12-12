package com.tibbo.aggregate.common.binding;

import com.tibbo.aggregate.common.*;

public class BindingException extends AggreGateException
{
  public BindingException(String message)
  {
    super(message);
  }
  
  public BindingException(Throwable cause)
  {
    super(cause);
  }
  
  public BindingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
