package com.tibbo.aggregate.common.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.CallerData;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.ContextEventListenerSet;

public abstract class AbstractClientController<T extends CallerData>
{
  private ContextManager contextManager;
  private CallerController callerController;
  
  private final Map<String, Map<String, ContextEventListenerSet>> listeners = Collections.synchronizedMap(new Hashtable<String, Map<String, ContextEventListenerSet>>());
  
  public AbstractClientController()
  {
    super();
  }
  
  public AbstractClientController(ContextManager contextManager)
  {
    this.contextManager = contextManager;
  }
  
  public AbstractClientController(ContextManager contextManager, CallerController callerController)
  {
    this.contextManager = contextManager;
    this.callerController = callerController;
  }
  
  public CallerController getCallerController()
  {
    return callerController;
  }
  
  public void setCallerController(CallerController callerController)
  {
    this.callerController = callerController;
  }
  
  public void addMaskListener(String context, String name, ContextEventListener cel, boolean weak)
  {
    contextManager.addMaskEventListener(context, name, cel, weak);
    if (weak)
    {
      // Set Will be cleared upon shutdown, causing weak listeners GC
      
      Map<String, ContextEventListenerSet> contextListeners = listeners.get(context);
      
      if (contextListeners == null)
      {
        contextListeners = new HashMap<String, ContextEventListenerSet>();
        listeners.put(context, contextListeners);
      }
      
      ContextEventListenerSet eventListeners = contextListeners.get(name);
      
      if (eventListeners == null)
      {
        eventListeners = new ContextEventListenerSet(contextManager);
        contextListeners.put(name, eventListeners);
      }
      
      eventListeners.addListener(cel, false);
    }
  }
  
  public void removeMaskListener(String context, String name, ContextEventListener cel)
  {
    contextManager.removeMaskEventListener(context, name, cel);
    
    ContextEventListenerSet eventListeners = getEventListeners(context, name);
    
    if (eventListeners != null)
    {
      eventListeners.removeListener(cel);
    }
  }
  
  public ContextManager getContextManager()
  {
    return contextManager;
  }
  
  public void setContextManager(ContextManager contextManager)
  {
    this.contextManager = contextManager;
  }

  public void shutdown()
  {
    if (callerController != null)
    {
      callerController.logout();
    }
    listeners.clear();
  }
  
  public ContextEventListenerSet getEventListeners(String context, String name)
  {
    Map<String, ContextEventListenerSet> contextListeners = listeners.get(context);
    
    if (contextListeners == null)
    {
      return null;
    }
    
    return contextListeners.get(name);
  }
  public abstract boolean run();
}