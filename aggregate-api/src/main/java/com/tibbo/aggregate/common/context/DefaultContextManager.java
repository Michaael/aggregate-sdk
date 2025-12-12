package com.tibbo.aggregate.common.context;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.ContextEventListenerSet;
import com.tibbo.aggregate.common.event.EventUtils;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.plugin.PluginDirector;

public class DefaultContextManager<T extends Context> implements ContextManager<T>
{
  private final boolean async;
  
  private T rootContext = null;
  
  private final CallerController callerController = new UncheckedCallerController();
  
  private EventDispatcher eventDispatcher;
  private boolean eventDispatcherOwner = true;
  
  private final Map<String, Map<String, ContextEventListenerSet>> eventListeners = new ConcurrentHashMap<>();
  
  private final Map<String, Map<String, ContextEventListenerSet>> maskListeners = new ConcurrentHashMap<>();
  
  private final Map<String, Map<String, ContextEventListenerSet>> univocalListeners = new ConcurrentHashMap<>();
  
  private final ReentrantReadWriteLock maskListenersLock = new ReentrantReadWriteLock();
  
  private ThreadPoolExecutor executorService;
  
  private boolean started;
  
  public DefaultContextManager(boolean async)
  {
    this(async, Integer.MAX_VALUE, null);
  }
  
  public DefaultContextManager(boolean async, EventDispatcher eventDispatcher)
  {
    this(async, Integer.MAX_VALUE, null, eventDispatcher);
  }
  
  public DefaultContextManager(boolean async, int eventQueueLength, Supplier<ThreadPoolExecutor> concurrentDispatcherSupplier)
  {
    this(async, eventQueueLength, concurrentDispatcherSupplier, null);
  }
  
  public DefaultContextManager(boolean async, int eventQueueLength, Supplier<ThreadPoolExecutor> concurrentDispatcherSupplier, EventDispatcher eventDispatcher)
  {
    super();
    this.async = async;
    if (eventDispatcher != null)
    {
      this.eventDispatcher = eventDispatcher;
      this.eventDispatcherOwner = false;
    }
    
    if (async)
    {
      ensureDispatcher(eventQueueLength, concurrentDispatcherSupplier);
    }
  }
  
  public DefaultContextManager(T rootContext, boolean async, EventDispatcher eventDispatcher)
  {
    this(async, eventDispatcher);
    setRoot(rootContext);
    start();
  }
  
  public DefaultContextManager(T rootContext, boolean async)
  {
    this(rootContext, async, null);
  }
  
  @Override
  public void start()
  {
    if (async && eventDispatcherOwner)
    {
      ensureDispatcher(Integer.MAX_VALUE, null);
      eventDispatcher.start();
    }
    if (rootContext != null)
    {
      rootContext.start();
    }
    started = true;
  }
  
  @Override
  public void stop()
  {
    started = false;
    if (eventDispatcher != null && eventDispatcherOwner)
    {
      eventDispatcher.interrupt();
      eventDispatcher = null;
    }
    if (rootContext != null)
    {
      rootContext.stop();
    }
  }
  
  @Override
  public void restart()
  {
    stop();
    start();
  }
  
  private void ensureDispatcher(int eventQueueLength, Supplier<ThreadPoolExecutor> concurrentDispatcherSupplier)
  {
    if (eventDispatcher == null)
    {
      eventDispatcher = new EventDispatcher(eventQueueLength, concurrentDispatcherSupplier);
    }
  }
  
  @Override
  public T getRoot()
  {
    return rootContext;
  }
  
  public void setRoot(T newRoot)
  {
    rootContext = newRoot;
    rootContext.setup(this);
    contextAdded(newRoot);
  }
  
  @Override
  public T get(String contextName, CallerController caller)
  {
    T root = getRoot();
    return root != null ? (T) root.get(contextName, caller) : null;
  }
  
  @Override
  public T get(String contextName)
  {
    T root = getRoot();
    return root != null ? (T) root.get(contextName) : null;
  }
  
  private void addEventListener(String context, String event, ContextEventListener listener, boolean mask, boolean weak)
  {
    // Distributed: ok, because remote events will be redirected to this server
    T con = get(context, listener.getCallerController());
    
    if (con != null)
    {
      List<EventDefinition> events = EventUtils.getEvents(con, event, listener.getCallerController());
      
      for (EventDefinition ed : events)
      {
        addListenerToContext(con, ed.getName(), listener, mask, weak);
      }
    }
    else
    {
      if (!mask)
      {
        ContextEventListenerSet eel = getListeners(context, event);
        
        if (!eel.contains(listener))
        {
          eel.addListener(listener, weak);
        }
      }
    }
  }
  
  protected void addListenerToContext(T con, String event, ContextEventListener listener, boolean mask, boolean weak)
  {
    EventDefinition ed = con.getEventDefinition(event, listener.getCallerController());
    if (ed != null)
    {
      con.addEventListener(event, listener, weak);
    }
  }
  
  private void removeEventListener(String context, String event, ContextEventListener listener, boolean mask)
  {
    T con = get(context, listener.getCallerController());
    
    if (con != null)
    {
      if (con.getEventDefinition(event) != null)
      {
        removeListenerFromContext(con, event, listener, mask);
      }
    }
    else
    {
      if (!mask)
      {
        ContextEventListenerSet eel = getListeners(context, event);
        
        if (eel != null)
        {
          eel.removeListener(listener);
        }
      }
    }
  }
  
  protected void removeListenerFromContext(T con, String event, ContextEventListener listener, boolean mask)
  {
    con.removeEventListener(event, listener);
  }
  
  @Override
  public void addMaskEventListener(String mask, String event, ContextEventListener listener)
  {
    addMaskEventListener(mask, event, listener, false);
  }
  
  @Override
  public void addMaskEventListener(String mask, String event, ContextEventListener listener, boolean weak)
  {
    List<String> contexts = ContextUtils.expandMaskToPaths(mask, this, listener.getCallerController());
    
    for (String con : contexts)
    {
      addEventListener(con, event, listener, true, weak);
    }
    
    ContextEventListenerSet listeners = getMaskListeners(mask, event);
    
    listeners.addListener(listener, weak);
  }
  
  @Override
  public void removeMaskEventListener(String mask, String event, ContextEventListener listener)
  {
    List<Context> contexts = ContextUtils.expandMaskToContexts(mask, this, listener.getCallerController());
    
    for (Context con : contexts)
    {
      if (!con.isInitializedEvents())
      {
        continue;
      }
      
      List<EventDefinition> events = EventUtils.getEvents(con, event, listener.getCallerController());
      
      for (EventDefinition ed : events)
      {
        removeEventListener(con.getPath(), ed.getName(), listener, true);
      }
    }
    
    ContextEventListenerSet listeners = getMaskListeners(mask, event);
    
    listeners.removeListener(listener);
  }
  
  protected ContextEventListenerSet getListeners(String context, String event)
  {
    Map<String, ContextEventListenerSet> cel = getContextListeners(context);
    
    ContextEventListenerSet cels = cel.get(event);
    
    if (cels == null)
    {
      cels = new ContextEventListenerSet(this);
      cel.put(event, cels);
    }
    
    return cels;
  }
  
  private Map<String, ContextEventListenerSet> getContextListeners(String context)
  {
    Map<String, ContextEventListenerSet> cel = eventListeners.get(context);
    
    if (cel == null)
    {
      cel = new ConcurrentHashMap<>();
      eventListeners.put(context, cel);
    }
    
    return cel;
  }
  
  private ContextEventListenerSet getMaskListeners(String mask, String event)
  {
    Map<String, ContextEventListenerSet> cel = getContextMaskListeners(mask);
    synchronized (this)
    {
      ContextEventListenerSet eel = cel.get(event);
      
      if (eel == null)
      {
        eel = new ContextEventListenerSet(this);
        cel.put(event, eel);
      }
      
      return eel;
    }
  }
  
  private Map<String, ContextEventListenerSet> getContextMaskListeners(String mask)
  {
    Map<String, ContextEventListenerSet> cel = null;
    
    Map<String, Map<String, ContextEventListenerSet>> localListeners = maskListeners;
    
    maskListenersLock.readLock().lock();
    try
    {
      if (!ContextUtils.isMask(mask))
      {
        localListeners = univocalListeners;
      }
      
      cel = localListeners.get(mask);
    }
    finally
    {
      maskListenersLock.readLock().unlock();
    }
    
    if (cel == null)
    {
      maskListenersLock.writeLock().lock();
      try
      {
        cel = localListeners.get(mask);
        if (cel == null)
        {
          cel = new ConcurrentHashMap<>();
          localListeners.put(mask, cel);
        }
      }
      finally
      {
        maskListenersLock.writeLock().unlock();
      }
    }
    return cel;
  }
  
  @Override
  public void contextAdded(T con)
  {
    Map<String, ContextEventListenerSet> cel = eventListeners.get(con.getPath());
    
    if (cel != null)
    {
      for (String event : cel.keySet())
      {
        ContextEventListenerSet cels = cel.get(event);
        
        if (con.getEventData(event) != null)
        {
          synchronized (cels)
          {
            cels.executeForEachListener(li -> con.addEventListener(event, li.getListener(), li.isWeak()));
          }
        }
      }
    }
    
    maskListenersLock.readLock().lock();
    try
    {
      for (String mask : maskListeners.keySet())
      {
        if (ContextUtils.matchesToMask(mask, con.getPath()))
        {
          addMaskListenerToContext(mask, con);
        }
      }
      
      if (univocalListeners.get(con.getPath()) != null)
      {
        addMaskListenerToContext(con.getPath(), con);
      }
    }
    finally
    {
      maskListenersLock.readLock().unlock();
    }
  }
  
  public Set<String> getMaskListenersMasks()
  {
    maskListenersLock.readLock().lock();
    try
    {
      return Sets.union(maskListeners.keySet(), univocalListeners.keySet());
    }
    finally
    {
      maskListenersLock.readLock().unlock();
    }
  }
  
  public void addMaskListenerToContext(String mask, T con)
  {
    Map<String, ContextEventListenerSet> mcel = getContextMaskListeners(mask);
    
    if (ContextUtils.matchesToMask(mask, con.getPath()))
    {
      for (String eventMask : mcel.keySet())
      {
        final ContextEventListenerSet listeners = mcel.get(eventMask);
        
        synchronized (listeners)
        {
          listeners.executeForEachListener(li -> {
            final List<EventDefinition> events = EventUtils.getEvents(con, eventMask, li.getListener().getCallerController());
            for (EventDefinition ed : events)
            {
              addListenerToContext(con, ed.getName(), li.getListener(), true, li.isWeak());
            }
          });
        }
      }
    }
  }
  
  @Override
  public void contextRemoved(T con)
  {
    try
    {
      con.accept(new DefaultContextVisitor()
      {
        @Override
        public void visit(Context vc)
        {
          maskListenersLock.readLock().lock();
          try
          {
            for (String mask : maskListeners.keySet())
            {
              if (ContextUtils.matchesToMask(mask, vc.getPath()))
              {
                removeListeners(vc, mask);
              }
            }
            
            if (univocalListeners.get(con.getPath()) != null)
            {
              removeListeners(vc, vc.getPath());
            }
          }
          finally
          {
            maskListenersLock.readLock().unlock();
          }
        }
        
        private void removeListeners(Context vc, String mask)
        {
          Map<String, ContextEventListenerSet> contextMaskListeners = getContextMaskListeners(mask);
          
          Map<String, ContextEventListenerSet> contextMaskListenersCopy;
          synchronized (contextMaskListeners)
          {
            contextMaskListenersCopy = new LinkedHashMap(contextMaskListeners);
          }
          
          for (String event : contextMaskListenersCopy.keySet())
          {
            ContextEventListenerSet cels = getMaskListeners(mask, event);
            cels.executeForEachListener(li -> {
              List<EventDefinition> events = EventUtils.getEvents(vc, event, li.getListener().getCallerController());
              
              for (EventDefinition ed : events)
              {
                vc.removeEventListener(ed.getName(), li.getListener());
              }
            });
          }
        }
      });
      
      con.accept(new DefaultContextVisitor()
      {
        @Override
        public void visit(Context vc)
        {
          Map<String, ContextEventListenerSet> cel = getContextListeners(vc.getPath());
          final List<EventDefinition> eventDefinitions = vc.getEventDefinitions(callerController);
          for (EventDefinition ed : eventDefinitions)
          {
            EventData edata = vc.getEventData(ed.getName());
            ContextEventListenerSet listeners = cel.get(ed.getName());
            if (listeners != null)
            {
              synchronized (listeners)
              {
                edata.addListeners(listeners);
              }
            }
          }
        }
      });
    }
    catch (ContextException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  @Override
  public void contextInfoChanged(T con)
  {
    
  }
  
  @Override
  public void variableAdded(Context con, VariableDefinition vd)
  {
    
  }
  
  @Override
  public void variableRemoved(Context con, VariableDefinition vd)
  {
    
  }
  
  @Override
  public void functionAdded(Context con, FunctionDefinition fd)
  {
    
  }
  
  @Override
  public void functionRemoved(Context con, FunctionDefinition fd)
  {
    
  }
  
  @Override
  public void eventAdded(T con, EventDefinition ed)
  {
    maskListenersLock.readLock().lock();
    try
    {
      for (String mask : maskListeners.keySet())
      {
        if (ContextUtils.matchesToMask(mask, con.getPath()))
        {
          addListenerToContext(con, ed, mask);
        }
      }
      
      if (univocalListeners.get(con.getPath()) != null)
      {
        addListenerToContext(con, ed, con.getPath());
      }
      
    }
    finally
    {
      maskListenersLock.readLock().unlock();
    }
  }
  
  private void addListenerToContext(T con, EventDefinition ed, String mask)
  {
    Map<String, ContextEventListenerSet> cel = getContextMaskListeners(mask);
    
    for (String eventMask : cel.keySet())
    {
      if (EventUtils.matchesToMask(eventMask, ed))
      {
        ContextEventListenerSet listeners = cel.get(eventMask);
        
        synchronized (listeners)
        {
          listeners.executeForEachListener(li -> addListenerToContext(con, ed.getName(), li.getListener(), true, li.isWeak()));
        }
      }
    }
  }
  
  @Override
  public void eventRemoved(Context con, EventDefinition ed)
  {
    
  }
  
  @Override
  public void queue(EventData ed, Event ev, FireEventRequestController request)
  {
    EventDispatcher dispatcher = eventDispatcher;
    
    if (dispatcher != null)
    {
      dispatcher.registerIncomingEvent();
    }
    
    if (!async || ed.getDefinition().getConcurrency() == EventDefinition.CONCURRENCY_SYNCHRONOUS)
    {
      ed.dispatch(ev);
      if (dispatcher != null)
      {
        dispatcher.registerProcessedEvent();
      }
    }
    else
    {
      if (!haveToBeProcessed(ed, ev))
      {
        if (dispatcher != null)
        {
          dispatcher.registerProcessedEvent();
        }
        return;
      }
      QueuedEvent qe = new QueuedEvent(ed, ev);
      
      try
      {
        if (dispatcher != null)
        {
          dispatcher.queue(qe, request);
        }
      }
      catch (InterruptedException ex1)
      {
        Log.CONTEXT_EVENTS.debug("Interrupted while queueing event: " + ev);
      }
      catch (NullPointerException ex1)
      {
        Log.CONTEXT_EVENTS.debug("Cannot queue event '" + ev + "': context manager is not running");
      }
    }
  }

  private boolean haveToBeProcessed(EventData eventData, Event event)
  {
    // We have to put event into the queue to further processing before ContextManager is started
    if (!isStarted())
    {
      return true;
    }
    if (!eventData.hasListeners())
    {
      return false;
    }
    
    return eventData.shouldHandle(event);
  }
  
  protected void setExecutorService(ThreadPoolExecutor executorService)
  {
    this.executorService = executorService;
  }
  
  @Override
  public ThreadPoolExecutor getExecutorService()
  {
    return executorService;
  }
  
  @Override
  public CallerController getCallerController()
  {
    return callerController;
  }
  
  @Override
  public int getEventQueueLength()
  {
    return eventDispatcher != null ? eventDispatcher.getQueueLength() : 0;
  }
  
  @Override
  public long getEventsScheduled()
  {
    return eventDispatcher != null ? eventDispatcher.getEventsScheduled() : 0;
  }
  
  @Override
  public long getEventsProcessed()
  {
    return eventDispatcher != null ? eventDispatcher.getEventsProcessed() : 0;
  }
  
  @Override
  public Map<String, Long> getEventQueueStatistics()
  {
    return eventDispatcher.getEventQueueStatistics();
  }
  
  @Override
  public PluginDirector getPluginDirector()
  {
    return null;
  }
  
  @Override
  public boolean isStarted()
  {
    return started;
  }
}
