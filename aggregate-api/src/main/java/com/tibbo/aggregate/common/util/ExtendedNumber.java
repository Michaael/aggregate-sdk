package com.tibbo.aggregate.common.util;

import java.util.*;

public class ExtendedNumber extends Number
{
  private Number number;
  private Integer quality;
  private Date timestamp;
  
  public ExtendedNumber()
  {
    super();
  }
  
  public ExtendedNumber(Number number, Integer quality, Date timestamp)
  {
    super();
    this.number = number;
    this.quality = quality;
    this.timestamp = timestamp;
  }
  
  @Override
  public int intValue()
  {
    return number.intValue(); // Can't be NaN, so need to remember about null values handling
  }
  
  @Override
  public long longValue()
  {
    return number.longValue(); // Can't be NaN, so need to remember about null values handling
  }
  
  @Override
  public float floatValue()
  {
    return number != null ? number.floatValue() : Float.NaN;
  }
  
  @Override
  public double doubleValue()
  {
    return number != null ? number.doubleValue() : Double.NaN;
  }
  
  public Integer getQuality()
  {
    return quality;
  }
  
  public Number getNumber()
  {
    return number;
  }
  
  public Date getTimestamp()
  {
    return timestamp;
  }
  
  public void setQuality(Integer quality)
  {
    this.quality = quality;
  }
  
  public void setNumber(Number number)
  {
    this.number = number;
  }
  
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }
  
  @Override
  public String toString()
  {
    return "ExtendedNumber{" +
        "number=" + number +
        ", quality=" + quality +
        ", timestamp=" + timestamp +
        '}';
  }
}
