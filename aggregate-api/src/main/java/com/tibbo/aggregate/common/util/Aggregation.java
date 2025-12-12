package com.tibbo.aggregate.common.util;

import java.util.*;

import com.tibbo.aggregate.common.*;

public class Aggregation
{
  public static final int AVERAGE = 0;
  public static final int MINIMUM = 1;
  public static final int MAXIMUM = 2;
  public static final int SUMMATION = 3;
  public static final int FIRST = 4;
  public static final int LAST = 5;
  public static final int FIRST_DATE = 6;
  public static final int LAST_DATE = 7;
  public static final int TOTAL_COUNT = 8;
  public static final int VALID_COUNT = 9;
  public static final int MINIMUM_DATE = 10;
  public static final int MAXIMUM_DATE = 11;
  
  private static Map<Object, String> SELECTION_VALUES = new LinkedHashMap();
  static
  {
    SELECTION_VALUES.put(AVERAGE, Cres.get().getString("average"));
    SELECTION_VALUES.put(MINIMUM, Cres.get().getString("minimum"));
    SELECTION_VALUES.put(MAXIMUM, Cres.get().getString("maximum"));
    SELECTION_VALUES.put(SUMMATION, Cres.get().getString("summation"));
    SELECTION_VALUES.put(FIRST, Cres.get().getString("first"));
    SELECTION_VALUES.put(LAST, Cres.get().getString("last"));
  }
  
  public static Map<Object, String> getSelectionValues()
  {
    return SELECTION_VALUES;
  }
}
