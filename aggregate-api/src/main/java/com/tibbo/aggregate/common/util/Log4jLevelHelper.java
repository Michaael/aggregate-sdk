package com.tibbo.aggregate.common.util;

import java.util.*;
import java.util.Map.*;

import org.apache.logging.log4j.*;

import com.tibbo.aggregate.common.event.*;

public class Log4jLevelHelper
{
  
  private static Map<Integer, Integer> LEVEL_TABLE = new Hashtable();
  
  static
  {
    LEVEL_TABLE.put(Level.DEBUG.intLevel(), EventLevel.NOTICE);
    LEVEL_TABLE.put(Level.INFO.intLevel(), EventLevel.INFO);
    LEVEL_TABLE.put(Level.WARN.intLevel(), EventLevel.WARNING);
    LEVEL_TABLE.put(Level.ERROR.intLevel(), EventLevel.ERROR);
    LEVEL_TABLE.put(Level.FATAL.intLevel(), EventLevel.FATAL);
  }
  
  public static int getLog4jLevelByAggreGateLevel(int aggreGateLevel)
  {
    for (Entry<Integer, Integer> entry : LEVEL_TABLE.entrySet())
    {
      if (entry.getValue().intValue() == aggreGateLevel)
      {
        return entry.getKey();
      }
    }
    
    return 0;
  }
  
  public static Integer getAggreGateLevelByLog4jLevel(int log4jLevel)
  {
    return LEVEL_TABLE.get(log4jLevel);
  }
}
