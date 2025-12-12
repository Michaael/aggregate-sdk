package com.tibbo.aggregate.common.util;

import com.tibbo.aggregate.common.*;


public class SyntaxErrorException extends AggreGateException
{
  public SyntaxErrorException(String message)
  {
    super(message);
  }

  public SyntaxErrorException(Throwable cause)
  {
    super(cause);
  }

  public SyntaxErrorException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
