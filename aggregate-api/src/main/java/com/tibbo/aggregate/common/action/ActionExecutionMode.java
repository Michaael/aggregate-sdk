package com.tibbo.aggregate.common.action;

public class ActionExecutionMode
{
  public static final int NORMAL = 0;
  public static final int REDIRECT = 1;
  public static final int BATCH = 2;
  public static final int TEST = 3;
  public static final int HEADLESS = 4;
  
  private final int code;
  
  public ActionExecutionMode(int code)
  {
    super();
    this.code = code;
  }
  
  public int getCode()
  {
    return code;
  }
}