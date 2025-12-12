package com.tibbo.aggregate.common.action;

import java.util.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.*;

public class ActionManager
{
  private final ActionDirectory actionDirectory;
  private final ActionIdGenerator actionIdGenerator = new ActionIdGenerator();
  private final Map<ActionIdentifier, Action> actions = Collections.synchronizedMap(new HashMap<>());
  private final Map<Action, ActionIdentifier> actionIDs = Collections.synchronizedMap(new HashMap<>());
  private final Map<Action, ActionContext> actionContexts = Collections.synchronizedMap(new HashMap<>());
  
  public ActionManager(ActionDirectory actionDirectory)
  {
    if (actionDirectory == null)
    {
      throw new NullPointerException();
    }
    
    this.actionDirectory = actionDirectory;
  }
  
  public List<ActionDefinition> resolveDefinitions(List<ActionLocator> actionLocators)
  {
    if (actionLocators == null)
    {
      throw new NullPointerException();
    }
    
    List<ActionDefinition> actionDefinitions = new LinkedList<>();
    for (ActionLocator actionLocator : actionLocators)
    {
      ActionDefinition actionDefinition = actionDirectory.getActionDefinition(actionLocator);
      
      if (actionDefinition == null)
      {
        throw new IllegalArgumentException("Can't resolve: " + actionLocator);
      }
      
      actionDefinitions.add(actionDefinition);
    }
    
    return actionDefinitions;
  }
  
  public ActionIdentifier initActions(List<BatchEntry> entries, ActionContext batchActionContext)
  {
    if (entries == null)
    {
      throw new NullPointerException();
    }
    
    if (batchActionContext == null)
    {
      throw new NullPointerException();
    }
    
    RequestCache requestCache = new RequestCache();
    BatchContext batchContext = new BatchContext();
    for (BatchEntry entry : entries)
    {
      if (entry == null)
      {
        throw new IllegalArgumentException("Entries list contains nulls");
      }
      
      ActionContext actionContext = entry.getActionContext();
      actionContext.setBatchContext(batchContext);
      actionContext.setRequestCache(requestCache);
      
      batchContext.addBatchEntry(entry);
    }
    
    batchActionContext.setBatchContext(batchContext);
    batchActionContext.setRequestCache(requestCache);
    
    BatchAction batchAction = new BatchAction(this);
    batchAction.init(batchActionContext, null);
    
    batchActionContext.setActionState(ActionContext.ActionState.INITIALIZED);
    
    return registerAction(batchActionContext, batchAction, new ActionExecutionMode(ActionExecutionMode.BATCH), null);
  }
  
  public ActionIdentifier initAction(ActionContext actionContext, InitialRequest initialParameters, ActionExecutionMode mode, String customActionId)
  {
    ActionDefinition actionDefinition = actionContext.getActionDefinition();
    
    ReentrantLock lock = actionDefinition.getExecutionLock();
    
    if (lock.isLocked() && !lock.isHeldByCurrentThread())
    {
      throw new IllegalStateException(Cres.get().getString("acActionBeingExecuted"));
    }
    
    Action action = instantiateAction(actionDefinition);
    
    actionContext.setActionState(ActionContext.ActionState.CREATED);
    
    action.init(actionContext, initialParameters);
    
    actionContext.setActionState(ActionContext.ActionState.INITIALIZED);
    
    return registerAction(actionContext, action, mode, customActionId);
  }
  
  protected Action instantiateAction(ActionDefinition actionDefinition)
  {
    if (actionDefinition == null)
    {
      throw new NullPointerException();
    }
    
    return actionDefinition.instantiate();
  }
  
  public ActionCommand service(ActionIdentifier actionId, ActionResponse actionRequest)
  {
    if (actionId == null)
    {
      throw new NullPointerException();
    }
    
    Action action = actions.get(actionId);
    
    if (action == null)
    {
      throw new IllegalStateException("Action with id '" + actionId + "' doesn't exists");
    }
    
    ActionContext actionContext = actionContexts.get(action);
    
    if (actionRequest == null && actionContext.getActionState() != ActionContext.ActionState.INITIALIZED)
    {
      throw new IllegalArgumentException("Null actionRequest is allowed only within first call to service()");
    }
    
    actionContext.setActionState(ActionContext.ActionState.WORKING);
    
    ActionCommand actionCommand = null;
    ActionResponse activeRequest = actionRequest;
    do
    {
      RequestCache requestCache = actionContext.getRequestCache();
      
      if (activeRequest != null && activeRequest.getRequestId() != null && activeRequest.shouldRemember())
      {
        if (requestCache == null)
        {
          requestCache = new RequestCache();
          actionContext.setRequestCache(requestCache);
        }
        
        requestCache.addRequest(activeRequest.getRequestId(), activeRequest);
      }
      
      actionCommand = action.service(activeRequest);
      
      if (requestCache != null && actionCommand != null && actionCommand.getRequestId() != null && !actionContext.getRequestedIds().contains(actionCommand.getRequestId()))
      {
        activeRequest = requestCache.getRequest(actionCommand.getRequestId());
      }
      else
      {
        activeRequest = null;
      }
      
      if (actionCommand != null && actionCommand.getRequestId() != null)
      {
        actionContext.getRequestedIds().add(actionCommand.getRequestId());
      }
    }
    while (activeRequest != null);
    
    return actionCommand;
  }
  
  public ActionResult destroyAction(ActionIdentifier actionId)
  {
    if (actionId == null)
    {
      throw new NullPointerException();
    }
    
    Action action = actions.get(actionId);
    
    if (action == null)
    {
      Log.CONTEXT_ACTIONS.debug("Action with id '" + actionId + "' doesn't exists");
      return null;
    }
    
    try
    {
      actionContexts.get(action).setActionState(ActionContext.ActionState.DESTROYED);
      return action.destroy();
    }
    finally
    {
      actions.remove(actionId);
      actionIDs.remove(action);
      actionContexts.remove(action);
    }
  }
  
  public void destroyAll()
  {
    for (ActionIdentifier actionId : new LinkedHashSet<ActionIdentifier>(actions.keySet()))
    {
      destroyAction(actionId);
    }
  }
  
  public ActionContext getActionContext(ActionIdentifier actionId)
  {
    Action action = actions.get(actionId);
    
    return actionContexts.get(action);
  }
  
  public ActionIdentifier getActionID(Action action)
  {
    return actionIDs.get(action);
  }
  
  public Action getAction(ActionIdentifier actionID)
  {
    return actions.get(actionID);
  }
  
  public ActionDirectory getActionDirectory()
  {
    return actionDirectory;
  }
  
  protected ActionIdentifier registerAction(ActionContext actionContext, Action action, ActionExecutionMode mode, String customActionId)
  {
    ActionIdentifier actionId;
    
    if (customActionId == null)
      actionId = actionIdGenerator.generate(action);
    else
      actionId = new ActionIdentifier(customActionId);
    
    actionContext.setActionExecutionMode(mode);
    if (actions.containsKey(actionId)) {
      throw new IllegalArgumentException("Action: " + actionId.getId() + " is already existed.");
    }
    actions.put(actionId, action);
    actionIDs.put(action, actionId);
    actionContexts.put(action, actionContext);
    
    return actionId;
  }
}
