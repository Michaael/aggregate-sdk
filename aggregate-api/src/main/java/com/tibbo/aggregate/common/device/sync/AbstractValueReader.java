package com.tibbo.aggregate.common.device.sync;

public abstract class AbstractValueReader implements ValueReader
{
  private final String name;
  
  public AbstractValueReader()
  {
    this.name = "Custom Synchronization Handler";
  }
  
  public AbstractValueReader(String name)
  {
    this.name = name;
  }
  
  public String toString()
  {
    return name;
  }
  
}
