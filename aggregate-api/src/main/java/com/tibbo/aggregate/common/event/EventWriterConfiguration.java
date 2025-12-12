package com.tibbo.aggregate.common.event;

public interface EventWriterConfiguration
{
  public int getType();
  
  public String getMask();
  
  public String getEvent();
  
  public String getName();
  
  public String getFormat();
  
}