package com.tibbo.aggregate.common.expression.util;


import com.tibbo.aggregate.common.*;

public class DefaultTracer implements Tracer
{
  @Override
  public void trace(Object value, String message)
  {
    traceToLog(value, message);
  }
  
  public static void traceToLog(Object value, String message)
  {
    Log.EXPRESSIONS.info("Trace: " + value + (message != null ? " (" + message + ")" : ""));
  }
}
