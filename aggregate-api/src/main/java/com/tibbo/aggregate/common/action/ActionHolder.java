package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.server.*;

public class ActionHolder
{
  private ServerContext context;
  private String actionName;
  private DataTable initialParameters;
  private DataTable inputData;
  
  public String getActionName()
  {
    return actionName;
  }
  
  public ServerContext getContext()
  {
    return context;
  }
  
  public DataTable getInitialParameters()
  {
    return initialParameters;
  }
  
  public DataTable getInputData()
  {
    return inputData;
  }
  
  public void setActionName(String actionName)
  {
    this.actionName = actionName;
  }
  
  public void setContext(ServerContext context)
  {
    this.context = context;
  }
  
  public void setInitialParameters(DataTable initialParameters)
  {
    this.initialParameters = initialParameters;
  }
  
  public void setInputData(DataTable inputData)
  {
    this.inputData = inputData;
  }
}
