package com.tibbo.aggregate.common.expression.util;

public class TracerManager
{
  private static Tracer DEFAULT_TRACER = new DefaultTracer();
  
  public static Tracer getDefaultTracer()
  {
    return DEFAULT_TRACER;
  }
  
  public static void setDefaultTracer(Tracer tracer)
  {
    if (tracer == null)
    {
      throw new NullPointerException("Default tracer cannot be NULL");
    }
    
    DEFAULT_TRACER = tracer;
  }
}
