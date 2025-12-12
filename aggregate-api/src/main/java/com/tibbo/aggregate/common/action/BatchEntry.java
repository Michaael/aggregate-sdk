package com.tibbo.aggregate.common.action;

public class BatchEntry
{
  private ActionContext actionContext;
  private InitialRequest initialRequest;
  private boolean fulfilled;
  
  public BatchEntry(ActionContext actionContext, InitialRequest initialRequest)
  {
    if (actionContext == null)
    {
      throw new NullPointerException();
    }
    
    if (initialRequest == null)
    {
      throw new NullPointerException();
    }
    
    this.actionContext = actionContext;
    this.initialRequest = initialRequest;
  }
  
  public ActionContext getActionContext()
  {
    return actionContext;
  }
  
  public InitialRequest getInitialRequest()
  {
    return initialRequest;
  }
  
  public boolean isFulfilled()
  {
    return fulfilled;
  }
  
  protected void setFulfilled(boolean fulfilled)
  {
    this.fulfilled = fulfilled;
  }
  
  public String toString()
  {
    return actionContext + " (" + initialRequest + ")";
  }
}
