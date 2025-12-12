package com.tibbo.aggregate.common.server;

public interface JobContextConstants
{
  public static final int STATUS_ENABLED = 0;
  public static final int STATUS_DISABLED = 1;
  
  public static final String V_ADVANCED_TRIGGERS = "cronTriggersView";
  public static final String V_SIMPLE_TRIGGERS = "triggersView";
  public static final String V_STATUS = "status";
  public static final String V_RESULTS = "results";
  
  public static final String F_EXECUTE = "execute";
  
  public static final String A_EXECUTE = "execute";
  
  public static final String VF_STATUS_PREVIOUS_FIRE_TIME = "previousFireTime";
  public static final String VF_STATUS_NEXT_FIRE_TIME = "nextFireTime";
  public static final String VF_STATUS_PROGRESS = "progress";
  public static final String VF_STATUS_RESULT = "result";
  
  public static final String VF_STATUS_TARGET = "target";
  public static final String VF_STATUS_STATUS = "status";
  public static final String VF_STATUS_DATE = "date";
  public static final String VF_STATUS_ERROR = "error";
  
  public static final int STATUS_PENDING = 1;
  public static final int STATUS_IN_PROGRESS = 2;
  public static final int STATUS_SUCCESSFUL = 3;
  public static final int STATUS_ERROR = 4;
  public static final int STATUS_SKIPPED = 5;
  
}
