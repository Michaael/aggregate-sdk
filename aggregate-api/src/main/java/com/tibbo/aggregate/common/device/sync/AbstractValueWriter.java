package com.tibbo.aggregate.common.device.sync;

public abstract class AbstractValueWriter implements ValueWriter
{
  private final String name;
  
  public AbstractValueWriter(String name)
  {
    this.name = name;
  }
  
  public AbstractValueWriter()
  {
    this.name = "Custom Synchronization Handler";
  }
  
  public String toString()
  {
    return name;
  }
}
