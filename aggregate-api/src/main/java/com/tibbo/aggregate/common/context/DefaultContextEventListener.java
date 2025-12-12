package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.expression.*;

public abstract class DefaultContextEventListener<T extends CallerController> implements ContextEventListener
{
  private T callerController;
  private ContextManager contextManager;
  
  private Integer listenerCode;
  private Expression filter;
  private boolean acceptEventsWithoutListenerCode;
  
  private String fingerprint;
  
  public DefaultContextEventListener()
  {
  }
  
  public DefaultContextEventListener(T callerController)
  {
    this();
    this.callerController = callerController;
  }
  
  public DefaultContextEventListener(T callerController, Integer listenerCode)
  {
    this(callerController);
    this.listenerCode = listenerCode;
  }
  
  public DefaultContextEventListener(T callerController, ContextManager contextManager, Integer listenerCode, Expression filter)
  {
    this(callerController, listenerCode);
    this.contextManager = contextManager;
    this.filter = filter;
  }
  
  public DefaultContextEventListener(T callerController, ContextManager contextManager, Integer listenerCode, Expression filter, String fingerprint)
  {
    this(callerController, contextManager, listenerCode, filter);
    this.fingerprint = fingerprint;
  }
  
  public DefaultContextEventListener(Integer listenerCode)
  {
    this();
    this.listenerCode = listenerCode;
  }
  
  public DefaultContextEventListener(Integer listenerCode, boolean acceptEventsWithoutListenerCode)
  {
    this();
    this.listenerCode = listenerCode;
    this.acceptEventsWithoutListenerCode = acceptEventsWithoutListenerCode;
  }
  
  @Override
  public boolean shouldHandle(Event ev) throws EventHandlingException
  {
    if (filter != null)
    {
      Evaluator evaluator = new Evaluator(getLocalContextManager(), null, null, null);
      evaluator.getDefaultResolver().setContextManager(contextManager);
      prepareEvaluator(evaluator, ev);
      
      try
      {
        if (!evaluator.evaluateToBoolean(filter))
        {
          return false;
        }
      }
      catch (Exception ex)
      {
        throw new EventHandlingException(ex.getMessage(), ex);
      }
    }
    
    if (listenerCode != null)
    {
      if (ev.getListener() != null && !listenerCode.equals(ev.getListener()))
      {
        return false;
      }
      
      return ev.getListener() != null || acceptEventsWithoutListenerCode;
    }
    else
    {
      return ev.getListener() == null || acceptEventsWithoutListenerCode;
    }
    
  }
  
  private void prepareEvaluator(Evaluator evaluator, Event ev)
  {
    EventEnvironmentResolver resolver = new EventEnvironmentResolver(evaluator.getEnvironmentResolver(), ev);
    
    evaluator.setResolver(Reference.SCHEMA_ENVIRONMENT, resolver);
    evaluator.getDefaultResolver().setDefaultTable(ev.getData());
    evaluator.getDefaultResolver().setCallerController(getCallerController());
  }
  
  @Override
  public T getCallerController()
  {
    return callerController;
  }
  
  public ContextManager getLocalContextManager()
  {
    return contextManager;
  }
  
  @Override
  public Integer getListenerCode()
  {
    return listenerCode;
  }
  
  @Override
  public void setListenerCode(Integer listenerCode)
  {
    this.listenerCode = listenerCode;
  }
  
  public void setCallerController(T callerController)
  {
    this.callerController = callerController;
  }
  
  public void setAcceptEventsWithoutListenerCode(boolean acceptEventsWithoutListenerCode)
  {
    this.acceptEventsWithoutListenerCode = acceptEventsWithoutListenerCode;
  }
  
  public void setFilter(Expression filter)
  {
    this.filter = filter;
  }
  
  public void setFingerprint(String fingerprint)
  {
    this.fingerprint = fingerprint;
  }
  
  @Override
  public Expression getFilter()
  {
    return filter;
  }
  
  @Override
  public String getFingerprint()
  {
    return fingerprint;
  }
  
  @Override
  public boolean isAsync()
  {
    return false;
  }
  
  public void setContextManager(ContextManager contextManager)
  {
    this.contextManager = contextManager;
  }
  
  @Override
  public void handle(Event event, EventDefinition ed) throws EventHandlingException
  {
    handle(event);
  }
}
