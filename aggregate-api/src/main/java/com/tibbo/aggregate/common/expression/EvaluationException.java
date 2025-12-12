package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.*;

public class EvaluationException extends AggreGateException
{
  public EvaluationException(String message)
  {
    super(message);
  }
  
  public EvaluationException(Throwable cause)
  {
    super(cause);
  }
  
  public EvaluationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
