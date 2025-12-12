package com.tibbo.aggregate.common.action;

import java.util.*;

import com.tibbo.aggregate.common.context.*;

public abstract class ActionContext
{
  
  public interface ChangeActionStateListener
  {
    void changeActionState(ActionState oldState, ActionState newState);
  }
  
  public enum ActionState
  {
    CREATED, INITIALIZED, WORKING, DESTROYED
  }
  
  private ActionDefinition actionDefinition;
  private BatchContext batchContext;
  private RequestCache requestCache;
  private ActionState actionState = ActionState.CREATED;
  private ActionManager actionManager;
  private final Set<RequestIdentifier> requestedIds = new LinkedHashSet();
  private ActionExecutionMode actionExecutionMode;
  
  private ChangeActionStateListener changeActionStateListener = null;

  private boolean executedAsDefault;
  public ActionContext(ActionDefinition actionDefinition, ActionManager actionManager)
  {
    setActionDefinition(actionDefinition);
    setActionManager(actionManager);
  }
  
  public abstract CallerController getCallerController();
  
  public abstract Context getDefiningContext();
  
  public ActionDefinition getActionDefinition()
  {
    return actionDefinition;
  }
  
  public BatchContext getBatchContext()
  {
    return batchContext;
  }
  
  public RequestCache getRequestCache()
  {
    return requestCache;
  }
  
  public ActionState getActionState()
  {
    return actionState;
  }
  
  public ActionManager getActionManager()
  {
    return actionManager;
  }
  
  Set<RequestIdentifier> getRequestedIds()
  {
    return requestedIds;
  }
  
  void setActionDefinition(ActionDefinition actionDefinition)
  {
    if (actionDefinition == null)
    {
      throw new NullPointerException();
    }
    
    this.actionDefinition = actionDefinition;
  }
  
  public void setBatchContext(BatchContext batchContext)
  {
    this.batchContext = batchContext;
  }
  
  public void setRequestCache(RequestCache requestCache)
  {
    this.requestCache = requestCache;
  }
  
  void setActionState(ActionState actionState)
  {
    if (actionState == null)
    {
      throw new NullPointerException();
    }
    ActionState old = this.actionState;
    this.actionState = actionState;
    
    if (changeActionStateListener != null && old != actionState)
    {
      changeActionStateListener.changeActionState(old, actionState);
    }
  }
  
  void setActionManager(ActionManager actionManager)
  {
    if (actionManager == null)
    {
      throw new NullPointerException();
    }
    
    this.actionManager = actionManager;
  }
  
  public void setChangeActionStateListener(ChangeActionStateListener changeActionStateListener)
  {
    this.changeActionStateListener = changeActionStateListener;
  }
  
  public ActionExecutionMode getActionExecutionMode()
  {
    return actionExecutionMode;
  }
  
  public void setActionExecutionMode(ActionExecutionMode actionExecutionMode)
  {
    this.actionExecutionMode = actionExecutionMode;
  }

  public boolean isExecutedAsDefault()
  {
    return executedAsDefault;
  }

  public void setExecutedAsDefault(boolean executedAsDefault)
  {
    this.executedAsDefault = executedAsDefault;
  }}
