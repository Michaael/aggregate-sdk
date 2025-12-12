package com.tibbo.aggregate.common.util;

import java.time.temporal.ChronoUnit;

public class TimeUnit
{
  private final int unit;
  private final long length;
  private final String description;
  private final boolean secondary;
  private final Integer calendarField;
  private ChronoUnit chronoUnit;
  
  public TimeUnit(int unit, long length, String description, Integer calendarField, boolean secondary)
  {
    this.unit = unit;
    this.length = length;
    this.description = description;
    this.calendarField = calendarField;
    this.secondary = secondary;
  }
  
  public TimeUnit(int unit, long length, String description, Integer calendarField, boolean secondary, ChronoUnit chronoUnit)
  {
    this(unit, length, description, calendarField, secondary);
    this.chronoUnit = chronoUnit;
  }
  
  public int getUnit()
  {
    return unit;
  }
  
  public long getLength()
  {
    return length;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public int getCalendarField()
  {
    return calendarField;
  }
  
  public boolean isSecondary()
  {
    return secondary;
  }
  
  public String toString()
  {
    return description;
  }
  
  public ChronoUnit getChronoUnit()
  {
    return chronoUnit;
  }
  
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + unit;
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    TimeUnit other = (TimeUnit) obj;
    if (unit != other.unit)
    {
      return false;
    }
    return true;
  }
}
