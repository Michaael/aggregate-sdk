package com.tibbo.aggregate.common.script;

import com.tibbo.aggregate.common.*;

public class ScriptException extends AggreGateException
{
  public ScriptException(String message)
  {
    super(message);
  }
  
  public ScriptException(Throwable cause)
  {
    super(cause);
  }
  
  public ScriptException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
