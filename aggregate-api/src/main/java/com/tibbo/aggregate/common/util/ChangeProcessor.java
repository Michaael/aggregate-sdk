package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.*;

/**
 * This class mimics the logic of org.rrd4j.core.Datasource
 */
public class ChangeProcessor
{
  public static final int GAUGE = 0;
  public static final int COUNTER = 1;
  public static final int DERIVE = 2;
  public static final int ABSOLUTE = 3;
  
  public static final int OUT_OF_RANGE_IGNORE = 0;
  public static final int OUT_OF_RANGE_DISCARD = 1;
  public static final int OUT_OF_RANGE_NORMALIZE = 2;
  
  private static Map<Object, String> SELECTION_VALUES = new LinkedHashMap();
  
  static
  {
    SELECTION_VALUES.put(GAUGE, Cres.get().getString("changeTypeGauge"));
    SELECTION_VALUES.put(COUNTER, Cres.get().getString("changeTypeCounter"));
    SELECTION_VALUES.put(DERIVE, Cres.get().getString("changeTypeDerive"));
    SELECTION_VALUES.put(ABSOLUTE, Cres.get().getString("changeTypeAbsolute"));
  }
  
  public static final Map<Object, String> getSelectionValues()
  {
    return SELECTION_VALUES;
  }
  
  public static final double MAX_32_BIT = Math.pow(2, 32);
  public static final double MAX_64_BIT = Math.pow(2, 64);
  
  private final int type;
  private final int outOfRangeValuesHandling;
  private final Double minValue;
  private final Double maxValue;
  
  private long lastUpdateTime = 0;
  private Double lastValue;
  
  public ChangeProcessor(int type)
  {
    this(type, OUT_OF_RANGE_IGNORE, null, null);
  }
  
  public ChangeProcessor(int type, int outOfRangeValuesHandling, Double minValue, Double maxValue)
  {
    this.type = type;
    this.outOfRangeValuesHandling = outOfRangeValuesHandling;
    this.minValue = outOfRangeValuesHandling == OUT_OF_RANGE_IGNORE ? null : minValue;
    this.maxValue = outOfRangeValuesHandling == OUT_OF_RANGE_IGNORE ? null : maxValue;
  }
  
  public Double process(long newTime, Double newValue)
  {
    if (newValue == null)
    {
      return null;
    }
    
    // Adding own protection from incorrect updates
    if (lastUpdateTime >= newTime && type != GAUGE)
    {
      return null;
    }
    
    long oldTime = lastUpdateTime;
    Double oldValue = lastValue;
    Double updateValue = calculateUpdateValue(oldTime, oldValue, newTime, newValue);
    
    lastUpdateTime = newTime; // In RRD4J, this is performed from RrdDb.store(Sample)
    
    return updateValue;
  }
  
  public ExtendedNumber processExtendedNumber(long newTime, ExtendedNumber newValue)
  {
    if (newValue == null)
    {
      return null;
    }
    
    Double newDoubleValue = newValue.getNumber() != null ? newValue.doubleValue() : null;
    return new ExtendedNumber(process(newTime, newDoubleValue), newValue.getQuality(), newValue.getTimestamp());
  }
  
  private Double calculateUpdateValue(long oldTime, Double oldValue, long newTime, Double newValue)
  {
    Double updateValue = null;
    
    if (type == GAUGE)
    {
      updateValue = newValue;
    }
    else if (type == COUNTER)
    {
      if (newValue != null && oldValue != null)
      {
        Double diff = newValue - oldValue;
        if (diff < 0)
        {
          diff += MAX_32_BIT;
        }
        if (diff < 0)
        {
          diff += MAX_64_BIT - MAX_32_BIT;
        }
        if (diff >= 0)
        {
          updateValue = 1000 * diff / (newTime - oldTime); // Units per second
        }
      }
    }
    else if (type == ABSOLUTE)
    {
      if (newValue != null)
      {
        updateValue = 1000 * newValue / (newTime - oldTime); // Units per second
      }
    }
    else if (type == DERIVE)
    {
      if (newValue != null && oldValue != null)
      {
        updateValue = 1000 * (newValue - oldValue) / (newTime - oldTime); // Units per second
      }
    }
    
    if (updateValue != null)
    {
      if (minValue != null && updateValue < minValue)
      {
        updateValue = outOfRangeValuesHandling == OUT_OF_RANGE_NORMALIZE ? minValue : null;
      }
      else if (maxValue != null && updateValue > maxValue)
      {
        updateValue = outOfRangeValuesHandling == OUT_OF_RANGE_NORMALIZE ? maxValue : null;
      }
    }
    
    lastValue = newValue;
    
    return updateValue;
  }
}
