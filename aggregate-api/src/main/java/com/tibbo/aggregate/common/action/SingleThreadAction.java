package com.tibbo.aggregate.common.action;

import java.util.concurrent.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.server.*;

public abstract class SingleThreadAction<I extends InitialRequest, C extends ActionCommand, R extends ActionResponse> extends SequentialAction<I, C, R>
{
  private static final int BINARY_SEMAPHOR_CAPACITY = 1;
  
  private ActionContext actionContext;
  private ActionThread executionThread;
  private final Semaphore waitCommand = new Semaphore(BINARY_SEMAPHOR_CAPACITY, true);
  private final Semaphore waitResponse = new Semaphore(BINARY_SEMAPHOR_CAPACITY, true);
  private ActionCommand lastCommand;
  private R lastResponse;
  
  private boolean destroyActionInThreadRun = true;
  
  public SingleThreadAction()
  {
  }
  
  @Override
  public synchronized final void init(ActionContext actionContext, I initialParameters)
  {
    if (executionThread != null)
    {
      throw new IllegalStateException("Already initialized");
    }
    
    if (actionContext == null)
    {
      throw new NullPointerException();
    }
    
    this.actionContext = actionContext;
    
    try
    {
      waitCommand.acquire(BINARY_SEMAPHOR_CAPACITY);
      waitResponse.acquire(BINARY_SEMAPHOR_CAPACITY);
    }
    catch (InterruptedException ex)
    {
      throw new ExceptionInInitializerError(ex);
    }
    
    executionThread = new ActionThread("Action / " + toString());
    executionThread.setActionRequest(initialParameters);
  }
  
  @Override
  public String toString()
  {
    return getActionContext().getActionDefinition().toString();
  }
  
  @Override
  protected R send(C actionCommand) throws DisconnectionException
  {
    if (actionCommand == null)
    {
      throw new NullPointerException();
    }
    
    if (Thread.currentThread().isInterrupted())
    {
      throw new DisconnectionException(Cres.get().getString("interrupted"));
    }
    
    this.lastCommand = actionCommand;
    
    waitCommand.release();
    try
    {
      waitResponse.acquire();
    }
    catch (InterruptedException ex)
    {
      throw new DisconnectionException(Cres.get().getString("interrupted"));
    }
    
    return lastResponse;
  }
  
  @Override
  public synchronized final C service(R actionRequest)
  {
    if (executionThread == null)
    {
      throw new IllegalStateException();
    }
    
    if (lastCommand != null)
    {
      if (!lastCommand.isResponseValid(actionRequest))
      {
        throw new IllegalArgumentException("Action response " + actionRequest + " doesn't match last command " + lastCommand);
      }
      
      lastCommand = null;
    }
    
    boolean newThread = Thread.State.NEW == executionThread.getState();
    
    if (newThread)
    {
      executionThread.start();
    }
    
    try
    {
      if (Thread.currentThread() == executionThread)
      {
        throw new IllegalThreadStateException("Call to service() from the execution thread");
      }
      
      if (!executionThread.isAlive())
      {
        throw new IllegalThreadStateException("Execution thread has finished");
      }
      
      if (newThread)
      {
        this.lastResponse = null;
      }
      else
      {
        this.lastResponse = actionRequest;
      }
      
      if (!newThread)
      {
        waitResponse.release();
      }
      waitCommand.acquire();
      
      return (C) lastCommand;
    }
    catch (InterruptedException e)
    {
      Log.CONTEXT_ACTIONS.info("Action was destroyed", e);
      
      return null;
    }
    catch (Throwable t)
    {
      executionThread.interrupt();
      throw new RuntimeException("Error servicing action command: " + t.getMessage(), t);
    }
  }
  
  protected ActionResult redirect(ActionDefinition actionDefinition, I initialRequest) throws DisconnectionException
  {
    ActionManager actionManager = actionContext.getActionManager();
    
    ActionDefinition currentActionDefinition = actionContext.getActionDefinition();
    
    try
    {
      actionContext.setActionDefinition(actionDefinition);
      
      ActionIdentifier actionId = actionManager.initAction(actionContext, initialRequest, new ActionExecutionMode(ActionExecutionMode.REDIRECT), null);
      
      return redirectById(actionId);
    }
    finally
    {
      actionContext.setActionDefinition(currentActionDefinition);
    }
  }
  
  protected ActionResult redirectById(ActionIdentifier actionId) throws DisconnectionException
  {
    final ActionManager actionManager = actionContext.getActionManager();
    
    disableActionDestructionInThreadRun(actionId, actionManager);
    
    try
    {
      C cmd = (C) actionManager.service(actionId, null);
      
      while (cmd != null)
      {
        ActionResponse actionRequest = send(cmd);
        
        cmd = (C) actionManager.service(actionId, actionRequest);
      }
    }
    catch (Exception e)
    {
      processError(e);
    }
    
    return actionManager.destroyAction(actionId);
  }
  
  private void disableActionDestructionInThreadRun(ActionIdentifier actionId, ActionManager actionManager)
  {
    final Action action = actionManager.getAction(actionId);
    
    if (action instanceof SingleThreadAction)
    {
      SingleThreadAction singleThreadAction = (SingleThreadAction) action;
      singleThreadAction.setDestroyActionInThreadRun(false);
    }
  }
  
  protected void processError(Throwable ex)
  {
    Log.CONTEXT_ACTIONS.warn("Error during action execution", ex);
  }
  
  private void setDestroyActionInThreadRun(boolean destroyActionInThreadRun)
  {
    this.destroyActionInThreadRun = destroyActionInThreadRun;
  }
  
  @Override
  public synchronized final ActionResult destroy()
  {
    if (executionThread != null)
    {
      executionThread.interrupt();
    }
    
    ActionResult actionResult = executionThread != null ? executionThread.getActionResult() : null;
    
    executionThread = null;
    lastCommand = null;
    lastResponse = null;
    waitCommand.release();
    waitResponse.release();
    
    return actionResult;
  }
  
  protected ActionContext getActionContext()
  {
    return actionContext;
  }
  
  class ActionThread extends Thread
  {
    private I initialRequest;
    private ActionResult actionResult;
    
    public ActionThread(String name)
    {
      super(name);
    }
    
    public void setActionRequest(I actionRequest)
    {
      if (!(actionRequest instanceof InitialRequest))
      {
        throw new IllegalArgumentException("actionRequest should be InitialRequest");
      }
      
      initialRequest = actionRequest;
    }
    
    public ActionResult getActionResult()
    {
      return actionResult;
    }
    
    @Override
    public void run()
    {
      ActionDefinition def = actionContext.getActionDefinition();
      ContextManager cm = actionContext.getDefiningContext().getContextManager();
      Context utilitiesContext = cm.get(Contexts.CTX_UTILITIES, actionContext.getCallerController());
      String actionId = actionContext.getActionManager().getActionID(SingleThreadAction.this).getId();
      TableFormat tf = utilitiesContext.getEventDefinition(UtilitiesContextConstants.E_ACTION_FINISHED).getFormat();
      DataTable eventData = new SimpleDataTable(tf, 1);
      if (!def.isConcurrent())
      {
        def.getExecutionLock().lock();
      }
      try
      {
        actionResult = invoke(initialRequest);
        DataTable resultTable = null;
        if (actionResult != null)
          resultTable = actionResult.getResult();
        eventData.rec().setValue(UtilitiesContextConstants.EF_ACTION_FINISHED_ACTION_ID, actionId)
            .setValue(UtilitiesContextConstants.EF_ACTION_FINISHED_ACTION_RESULT, resultTable);
        utilitiesContext.fireEvent(UtilitiesContextConstants.E_ACTION_FINISHED, actionContext.getCallerController(), eventData);
      }
      catch (Throwable ex)
      {
        processError(ex);
      }
      finally
      {
        if (!def.isConcurrent())
        {
          def.getExecutionLock().unlock();
        }
        waitCommand.release();
        
        if (destroyActionInThreadRun)
        {
          destroyActionInThreadRun();
        }
      }
    }
    
    private void destroyActionInThreadRun()
    {
      final ActionManager actionManager = actionContext.getActionManager();
      final ActionIdentifier actionID = actionManager != null ? actionManager.getActionID(SingleThreadAction.this) : null;
      if (actionID != null)
        actionManager.destroyAction(actionID);
    }
  }
}
