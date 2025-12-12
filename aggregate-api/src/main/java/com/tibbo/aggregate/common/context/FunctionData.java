package com.tibbo.aggregate.common.context;

import java.lang.reflect.*;
import java.util.concurrent.locks.*;

public class FunctionData implements Comparable<FunctionData>
{
  private FunctionDefinition definition;
  
  private final ReentrantLock executionLock = new ReentrantLock();
  
  private long executionCount;
  
  private boolean implementationCached;
  
  private Method implementationMethod;
  
  public FunctionData(FunctionDefinition definition)
  {
    super();
    this.definition = definition;
  }
  
  public void registerExecution()
  {
    executionCount++;
  }
  
  public FunctionDefinition getDefinition()
  {
    return definition;
  }
  
  public ReentrantLock getExecutionLock()
  {
    return executionLock;
  }
  
  public long getExecutionCount()
  {
    return executionCount;
  }
  
  public boolean isImplementationCached()
  {
    return implementationCached;
  }
  
  public void setImplementationCached(boolean implementationCached)
  {
    this.implementationCached = implementationCached;
  }
  
  public Method getImplementationMethod()
  {
    return implementationMethod;
  }
  
  public void setImplementationMethod(Method implementationMethod)
  {
    this.implementationMethod = implementationMethod;
  }
  
  public int compareTo(FunctionData d)
  {
    if (d != null)
    {
      return definition.compareTo(d.getDefinition());
    }
    
    return 0;
  }

  public void setDefinition(FunctionDefinition definition)
  {
    this.definition = definition;
    implementationCached = false;
    implementationMethod = null;
  }
}
