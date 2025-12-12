package com.tibbo.aggregate.common.action;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.DefaultContextEventListener;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.event.EventHandlingException;
import com.tibbo.aggregate.common.server.*;

import java.util.concurrent.locks.ReentrantLock;

public class EditTemplateActionDefinition extends ServerActionDefinition
{
  private static final String V_SESSION_ID = "id";
  
  private ReentrantLock widgetExecutionLock;
  
  public EditTemplateActionDefinition(String name, String description, Class actionClass,ReentrantLock lock)
  {
    super(name, description, actionClass);
    widgetExecutionLock = lock;
  }
  
  @Override
  public ReentrantLock getExecutionLock()
  {
    return widgetExecutionLock;
  }
  
  public static class LogoutEventListener extends DefaultContextEventListener
  {
    private CallerController callerController;
    private Context lockContext;
    
    public LogoutEventListener(Context lockContext, CallerController callerController)
    {
      this.lockContext = lockContext;
      this.callerController = callerController;
    }
    
    @Override
    public boolean shouldHandle(Event ev) throws EventHandlingException
    {
      DataRecord rec = ev.getData().rec();
      return super.shouldHandle(ev) && rec.hasField(V_SESSION_ID) && rec.getLong(V_SESSION_ID).equals(getCallerController().getSessionIdCounter());
    }
    
    @Override
    public void handle(Event event) throws EventHandlingException
    {
      try
      {
        getLockContext().callFunction(InstallableContextConstants.F_EXECUTE_UNLOCK, getCallerController());
      }
      catch (ContextException e)
      {
        throw new EventHandlingException(e);
      }
    }
    
    Context getLockContext()
    {
      return lockContext;
    }
    
    @Override
    public CallerController getCallerController()
    {
      return callerController;
    }
  }
  
}
