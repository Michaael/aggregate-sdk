package com.tibbo.aggregate.common.action;

class BatchAction implements Action
{
  private ActionManager actionManager;
  private ActionContext actionContext;
  private ActionIdentifier currentActionId;
  
  public BatchAction(ActionManager actionManager)
  {
    if (actionManager == null)
    {
      throw new NullPointerException();
    }
    
    this.actionManager = actionManager;
  }
  
  public synchronized void init(ActionContext actionContext, InitialRequest initialParameters)
  {
    if (actionContext == null)
    {
      throw new NullPointerException();
    }
    
    this.actionContext = actionContext;
  }
  
  public synchronized ActionCommand service(ActionResponse actionRequest)
  {
    BatchContext batchContext = actionContext.getBatchContext();
    
    if (batchContext == null)
    {
      // It's incorrect to use BatchAction without BatchContext
      throw new AssertionError();
    }
    
    do
    {
      ActionCommand actionCommand;
      
      if (currentActionId == null)
      {
        BatchEntry batchEntry = getNextEntry(batchContext);
        
        if (batchEntry == null)
        {
          return null;
        }
        
        ActionContext entryContext = batchEntry.getActionContext();
        
        entryContext.getRequestedIds().clear();
        
        currentActionId = actionManager.initAction(entryContext, batchEntry.getInitialRequest(), new ActionExecutionMode(ActionExecutionMode.BATCH), null);
        
        actionCommand = actionManager.service(currentActionId, null);
      }
      else
      {
        actionCommand = actionManager.service(currentActionId, actionRequest);
      }
      
      if (actionCommand != null)
      {
        actionCommand.setBatchEntry(true);
        return actionCommand;
      }
      else
      {
        currentActionId = null;
        batchContext.markAsPerfomed(batchContext.getCurrentEntry());
      }
    }
    while (true);
  }
  
  private BatchEntry getNextEntry(BatchContext batchContext) throws IllegalArgumentException
  {
    for (BatchEntry batchEntry : batchContext.getEntries())
    {
      if (!batchEntry.isFulfilled())
      {
        batchContext.setCurrentEntry(batchEntry);
        return batchEntry;
      }
    }
    
    return null;
  }
  
  public synchronized ActionResult destroy()
  {
    if (currentActionId != null)
    {
      actionManager.destroyAction(currentActionId);
    }
    
    actionManager = null;
    actionContext = null;
    currentActionId = null;
    
    return null;
  }
}
