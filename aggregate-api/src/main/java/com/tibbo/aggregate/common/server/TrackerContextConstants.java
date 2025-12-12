package com.tibbo.aggregate.common.server;

public interface TrackerContextConstants
{
  public static final int STATUS_ENABLED = 0;
  public static final int STATUS_DISABLED = 1;
  
  public static final String V_STATUSES = "statuses";
  public static final String V_TRACKER = "tracker";
  
  public static final String E_STATUS_CHANGE = "statusChange";
  
  public static final String VF_VALUE = "value";
}
