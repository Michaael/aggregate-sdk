package com.tibbo.aggregate.common.util;

import java.util.*;

public class TimeUnitsManager
{
  private int minUnit = 0;
  private int maxUnit = Integer.MAX_VALUE;
  // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
  private final List<TimeUnit> units = new ArrayList<TimeUnit>();
  
  public TimeUnitsManager()
  {
    
  }
  
  public TimeUnitsManager(String editorOptions)
  {
    if (editorOptions != null && editorOptions.length() > 0)
    {
      parseEditorOptions(editorOptions);
    }
    
    selectUnits();
  }
  
  public TimeUnitsManager(int minUnit, int maxUnit)
  {
    this.minUnit = minUnit;
    this.maxUnit = maxUnit;
    
    selectUnits();
  }
  
  private void selectUnits()
  {
    for (TimeUnit unit : TimeHelper.getUnits())
    {
      if (unit.getUnit() >= minUnit && unit.getUnit() <= maxUnit)
      {
        units.add(unit);
      }
    }
  }
  
  private void parseEditorOptions(String editorOptions)
  {
    List<String> parts = StringUtils.split(editorOptions, ' ');
    if (parts.size() > 0)
    {
      minUnit = Integer.parseInt(parts.get(0));
    }
    if (parts.size() > 1)
    {
      maxUnit = Integer.parseInt(parts.get(1));
    }
  }
  
  public int getMinUnit()
  {
    return minUnit;
  }
  
  public List<TimeUnit> getUnits()
  {
    return units;
  }
  
  public void setMinUnit(int minUnit)
  {
    this.minUnit = minUnit;
  }
  
  public int getMaxUnit()
  {
    return maxUnit;
  }
  
  public void setMaxUnit(int maxUnit)
  {
    this.maxUnit = maxUnit;
  }
  
  public TimeUnit selectUnitByPeriod(long newPeriod)
  {
    TimeUnit selectedUnit = TimeHelper.getTimeUnit(getMinUnit());
    
    for (TimeUnit unit : TimeHelper.getReversedUnits())
    {
      if (unit.getUnit() < getMinUnit() || unit.getUnit() > getMaxUnit())
      {
        continue;
      }
      
      if (unit.isSecondary())
      {
        continue;
      }
      
      if (newPeriod >= unit.getLength() && newPeriod % unit.getLength() == 0)
      {
        selectedUnit = unit;
        break;
      }
    }
    
    return selectedUnit;
  }
  
  public TimeUnit getUnitByDescription(String description)
  {
    for (TimeUnit tu : getUnits())
    {
      if (tu.getDescription().equals(description))
      {
        return tu;
      }
    }
    return null;
  }
  
  public String createTimeString(long period)
  {
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder result = new StringBuilder();
    
    int pass = 0;
    
    for (TimeUnit unit : TimeHelper.getReversedUnits())
    {
      if (unit.getUnit() > getMaxUnit())
      {
        continue;
      }
      
      if (unit.isSecondary())
      {
        continue;
      }
      
      long count = period / unit.getLength();
      
      boolean addZero = result.length() == 0 && unit.getUnit() == getMinUnit();
      
      if (count > 0 || addZero)
      {
        result.append((pass > 0 ? " " : "") + count + " " + unit.getDescription());
        
        period -= count * unit.getLength();
      }
      
      if (unit.getUnit() <= getMinUnit())
      {
        break;
      }
      
      pass++;
    }
    
    return result.toString().trim();
  }
}
