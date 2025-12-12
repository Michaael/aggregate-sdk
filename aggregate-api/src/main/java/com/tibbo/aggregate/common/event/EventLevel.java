package com.tibbo.aggregate.common.event;

import java.util.*;

import com.tibbo.aggregate.common.*;

public class EventLevel
{
  private static final Map<Integer, String> LEVELS = new LinkedHashMap();
  private static final Map<Object, String> SELECTION_VALUES = new LinkedHashMap();
  
  public static final int NUM_LEVELS = 5;
  
  public static final int NONE = 0;
  public static final int NOTICE = 1;
  public static final int INFO = 2;
  public static final int WARNING = 3;
  public static final int ERROR = 4;
  public static final int FATAL = 5;
  
  static
  {
    LEVELS.put(NOTICE, Cres.get().getString("conElNotice"));
    LEVELS.put(INFO, Cres.get().getString("conElInfo"));
    LEVELS.put(WARNING, Cres.get().getString("conElWarning"));
    LEVELS.put(ERROR, Cres.get().getString("conElError"));
    LEVELS.put(FATAL, Cres.get().getString("conElFatal"));
    LEVELS.put(NONE, Cres.get().getString("conElNotDefined"));
    
    SELECTION_VALUES.put(0, Cres.get().getString("none"));
    
    for (int i = 1; i <= EventLevel.NUM_LEVELS; i++)
    {
      SELECTION_VALUES.put(i, EventLevel.getName(i));
    }
  }
  
  public static boolean isValid(int level)
  {
    return LEVELS.containsKey(level);
  }
  
  public static String getName(Integer level)
  {
    return LEVELS.get(level);
  }
  
  public static Map<Object, String> getSelectionValues()
  {
    return new LinkedHashMap(SELECTION_VALUES);
  }
  
}
