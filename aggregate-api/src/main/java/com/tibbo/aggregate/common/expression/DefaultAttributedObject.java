package com.tibbo.aggregate.common.expression;

import java.util.*;

public class DefaultAttributedObject implements AttributedObject
{
  private Object value;
  private Date timestamp;
  private Integer quality;
  
  public DefaultAttributedObject(Object value)
  {
    super();
    this.value = value;
  }
  
  public DefaultAttributedObject(Object value, Date timestamp, Integer quality)
  {
    super();
    this.value = value;
    this.timestamp = timestamp;
    this.quality = quality;
  }
  
  @Override
  public Object getValue()
  {
    return value;
  }
  
  public void setValue(Object value)
  {
    this.value = value;
  }
  
  @Override
  public Date getTimestamp()
  {
    return timestamp;
  }
  
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }
  
  @Override
  public Integer getQuality()
  {
    return quality;
  }
  
  public void setQuality(Integer quality)
  {
    this.quality = quality;
  }
  
  @Override
  public String toString()
  {
    return "DefaultAttributedObject [value=" + value + "]";
  }
  
}
