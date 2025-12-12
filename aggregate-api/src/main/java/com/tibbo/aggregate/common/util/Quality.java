package com.tibbo.aggregate.common.util;

public class Quality
{
  public static int BAD_NON_SPECIFIC = 0;
  public static int BAD_CONFIGURATION_ERROR = 4;
  public static int BAD_NOT_CONNECTED = 8;
  public static int BAD_DEVICE_FAILURE = 12;
  
  public static int GOOD_NON_SPECIFIC = 192;

  public static boolean isBad(int quality)
  {
    return quality <= 32; // 32 means BAD_WAITING_FOR_INITIAL_DATA, the standard error with the highest numerical value.
  }
}
