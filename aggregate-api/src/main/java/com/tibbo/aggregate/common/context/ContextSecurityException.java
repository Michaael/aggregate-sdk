package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.protocol.*;

public class ContextSecurityException extends ContextException
{
  public ContextSecurityException(String message)
  {
    super(message);
    setCode(AggreGateCodes.REPLY_CODE_DENIED);
  }
  
  public ContextSecurityException(String message, Throwable cause)
  {
    super(message, cause);
    setCode(AggreGateCodes.REPLY_CODE_DENIED);
  }
}
