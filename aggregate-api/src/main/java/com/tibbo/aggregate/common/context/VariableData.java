package com.tibbo.aggregate.common.context;

import java.lang.reflect.*;
import java.util.concurrent.locks.*;

public class VariableData implements Comparable<VariableData>
{
  private VariableDefinition definition;
  
  private Object value;
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private long getCount;
  private long setCount;
  private boolean getterCached;
  private boolean setterCached;
  private Method getterMethod;
  private Method setterMethod;
  
  public VariableData(VariableDefinition definition)
  {
    this.definition = definition;
  }
  
  public void registerGetOperation()
  {
    getCount++;
  }
  
  public void registerSetOperation()
  {
    setCount++;
  }
  
  public VariableDefinition getDefinition()
  {
    return definition;
  }
  
  Object getValue()
  {
    return value;
  }
  
  void setValue(Object value)
  {
    this.value = value;
  }
  
  public ReentrantReadWriteLock getReadWriteLock()
  {
    return readWriteLock;
  }
  
  public long getGetCount()
  {
    return getCount;
  }
  
  public long getSetCount()
  {
    return setCount;
  }
  
  public boolean isGetterCached()
  {
    return getterCached;
  }
  
  public void setGetterCached(boolean getterCached)
  {
    this.getterCached = getterCached;
  }
  
  public boolean isSetterCached()
  {
    return setterCached;
  }
  
  public void setSetterCached(boolean setterCached)
  {
    this.setterCached = setterCached;
  }
  
  public Method getGetterMethod()
  {
    return getterMethod;
  }
  
  public void setGetterMethod(Method getter)
  {
    this.getterMethod = getter;
  }
  
  public Method getSetterMethod()
  {
    return setterMethod;
  }
  
  public void setSetterMethod(Method setter)
  {
    this.setterMethod = setter;
  }
  
  public int compareTo(VariableData d)
  {
    if (d != null)
    {
      return definition.compareTo(d.getDefinition());
    }
    
    return 0;
  }

  public void setDefinition(VariableDefinition definition)
  {
    this.definition = definition;
    getterCached = false;
    setterCached = false;
    getterMethod = null;
    setterMethod = null;
  }
}
