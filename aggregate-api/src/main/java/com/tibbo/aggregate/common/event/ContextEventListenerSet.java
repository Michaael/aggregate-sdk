package com.tibbo.aggregate.common.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextRuntimeException;
import com.tibbo.aggregate.common.context.EventData;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class ContextEventListenerSet
{
  // Cannot use Collections.newSetFromMap(WeakHashMap) here since filterListeners order must be preserved!
  private final ConcurrentLinkedQueue<Object> filterListeners = new ConcurrentLinkedQueue<>();
  private final Map<String, Queue<Object>> fingerprintListeners = new ConcurrentHashMap<>();
  private final ReentrantLock queueLock = new ReentrantLock();
  
  private ContextManager contextManager;
  // 'ContextManager' can be available also via 'context' (this avoid problems when contexts are [de]attached)
  private AbstractContext context;
  
  public ContextEventListenerSet(AbstractContext context)
  {
    this.context = context;
  }
  
  public ContextEventListenerSet(ContextManager contextManager)
  {
    this.contextManager = contextManager;
  }
  
  public void dispatch(Event event, EventDefinition eventDefinition, final EventData eventData)
  {
    try
    {
      if (Log.CONTEXT_EVENTS.isDebugEnabled())
      {
        Log.CONTEXT_EVENTS.debug("Dispatching event: " + event);
      }
      
      final Collection fingerprintListeners = fingerprintListeners(event, eventDefinition);
      
      if (fingerprintListeners != null)
      {
        dispatchEventToListeners(event, eventDefinition, eventData, fingerprintListeners);
      }
      
      dispatchEventToListeners(event, eventDefinition, eventData, this.filterListeners);
    }
    catch (Throwable ex)
    {
      Log.CONTEXT_EVENTS.error("Unexpected error occurred while dispatching event '" + event + "'", ex);
    }
  }
  
  private void dispatchEventToListeners(Event event, EventDefinition eventDefinition, EventData eventData, Collection listeners)
  {
    for (Iterator iterator = listeners.iterator(); iterator.hasNext();)
    {
      Object ref = iterator.next();
      
      ContextEventListenerInfo li = getListenerInfo(ref);
      
      if (li == null)
      {
        iterator.remove();
        continue;
      }
      
      ContextEventListener eventListener = li.getListener();
      
      final ContextManager contextManager = getContextManager();
      if (eventListener.isAsync() && contextManager != null && contextManager.getExecutorService() != null)
      {
        contextManager.getExecutorService().submit(new Runnable()
        {
          @Override
          public void run()
          {
            handleInListener(event, eventDefinition, eventListener, eventData);
          }
        });
      }
      else
      {
        handleInListener(event, eventDefinition, eventListener, eventData);
      }
    }
  }
  
  private Collection fingerprintListeners(Event event, EventDefinition eventDefinition) throws SyntaxErrorException, EvaluationException
  {
    if (eventDefinition.getFingerprintExpression() != null)
    {
      final ContextManager contextManager = getContextManager();
      if (contextManager != null)
      {
        Evaluator evaluator = new Evaluator(contextManager, contextManager.getCallerController());
        evaluator.setDefaultTable(event.getData());
        String fingerprint = evaluator.evaluateToString(eventDefinition.getCachedFingerprintExpression());
        return fingerprintListeners.get(fingerprint);
      }
      else
      {
        Log.CONTEXT_EVENTS.warn("Can't handle event with fingerprint because of no ContextManager: '" + event + "' ");
      }
    }
    return null;
  }
  
  private void handleInListener(Event event, EventDefinition eventDefinition, ContextEventListener eventListener, EventData eventData)
  {
    try
    {
      eventData.registerHandleOffer();
      
      if (!eventListener.shouldHandle(event))
      {
        if (Log.CONTEXT_EVENTS.isDebugEnabled())
        {
          Log.CONTEXT_EVENTS.debug("Listener '" + eventListener + "' does not want to handle event: " + event);
        }
        return;
      }
      
      eventData.registerHandleExecution();
      
      if (eventDefinition.isSessionBound())
      {
        Long eventSessionId = event.getSessionID();
        Long listenerSessionID = eventListener.getCallerController() != null && eventListener.getCallerController().getSessionIdCounter() != null ? eventListener.getCallerController().getSessionIdCounter() : -1;
        if (eventSessionId != null && !eventSessionId.equals(listenerSessionID))
        {
          if (Log.CONTEXT_EVENTS.isDebugEnabled())
          {
            Log.CONTEXT_EVENTS.debug("Listener '" + eventListener + "' should not handle a session bound event: " + event);
          }
          return;
        }
      }
      
      if (Log.CONTEXT_EVENTS.isDebugEnabled())
      {
        Log.CONTEXT_EVENTS.debug("Listener '" + eventListener + "' is going to handle event: " + event);
      }
      
      eventListener.handle(event, eventDefinition);
    }
    catch (Exception ex)
    {
      Log.CONTEXT_EVENTS.warn("Error handling event '" + event.toString() + "'", ex);
    }
  }
  
  public boolean shouldHandle(Event event)
  {
    return applyForEachListenerAndReturnIfTrue((li, iterator) -> {
      try
      {
        return li.getListener().shouldHandle(event);
      }
      catch (EventHandlingException e)
      {
        Log.CONTEXT_EVENTS.debug("Unexpected error occurred while checking an event '" + event + "' in listener '" + li + "'", e);
        throw e;
      }
    });
  }
  
  public void executeForEachListener(Consumer<ContextEventListenerInfo> action)
  {
    executeForEachQueue(queue -> {
      executeForEachListenerInQueue(queue, action);
    });
  }
  
  private void executeForEachQueue(Consumer<Queue> action)
  {
    action.accept(filterListeners);
    fingerprintListeners.values().forEach(action);
  }
  
  private boolean applyForEachListenerAndReturnIfTrue(ExceptionalFunction<ContextEventListenerInfo> action)
  {
    return applyForEachQueue(queue -> applyForEachListenerInQueue(queue, action, true), true);
  }
  
  private boolean applyForEachListener(ExceptionalFunction<ContextEventListenerInfo> action)
  {
    return applyForEachQueue(queue -> applyForEachListenerInQueue(queue, action, false), false);
  }
  
  private boolean applyForEachQueue(Function<Queue, Boolean> action, boolean returnOnTrue)
  {
    boolean result = action.apply(filterListeners);
    if (result && returnOnTrue)
    {
      return true;
    }
    for (Queue<Object> queue : fingerprintListeners.values())
    {
      result |= action.apply(queue);
      if (result && returnOnTrue)
      {
        return true;
      }
    }
    return result;
  }
  
  private boolean applyForEachListenerInQueue(Queue queue, ExceptionalFunction<ContextEventListenerInfo> action, boolean returnOnTrue)
  {
    boolean result = false;
    for (Iterator iterator = queue.iterator(); iterator.hasNext();)
    {
      Object ref = iterator.next();
      
      ContextEventListenerInfo li = getListenerInfo(ref);
      
      if (li == null)
      {
        iterator.remove();
        continue;
      }
      try
      {
        result |= action.apply(li, iterator);
        if (result && returnOnTrue)
        {
          return true;
        }
      }
      catch (EventHandlingException ignored)
      {
      }
    }
    return result;
  }
  
  private void executeForEachListenerInQueue(Queue queue, Consumer<ContextEventListenerInfo> action)
  {
    for (Iterator iterator = queue.iterator(); iterator.hasNext();)
    {
      Object ref = iterator.next();
      ContextEventListenerInfo li = getListenerInfo(ref);
      
      if (li == null)
      {
        iterator.remove();
        continue;
      }
      
      action.accept(li);
    }
  }
  
  public boolean hasListeners()
  {
    return applyForEachListenerAndReturnIfTrue((li, iterator) -> true);
  }
  
  public int size()
  {
    AtomicInteger size = new AtomicInteger(0);
    executeForEachListener(li -> size.incrementAndGet());
    return size.get();
  }
  
  public boolean addListener(ContextEventListener listener, boolean weak)
  {
    Queue<Object> listeners = filterListeners;
    
    final String fingerprint = listener.getFingerprint();
    
    if (fingerprint != null)
    {
      try
      {
        queueLock.lock();
        listeners = fingerprintListeners.putIfAbsent(fingerprint, new ConcurrentLinkedQueue<>());
        if (listeners == null)
        {
          fingerprintListeners.get(fingerprint).add(weak ? new WeakReference<>(listener) : listener);
          return true;
        }
      }
      finally
      {
        queueLock.unlock();
      }
      
    }
    try
    {
      queueLock.lock();
      if (containsIn(listener, listeners))
      {
        return false;
      }
      listeners.add(weak ? new WeakReference<>(listener) : listener);
      return true;
    }
    finally
    {
      queueLock.unlock();
    }
  }
  
  public boolean removeListener(ContextEventListener listener)
  {
    return applyForEachListenerAndReturnIfTrue((li, iterator) -> {
      if (li.getListener().equals(listener))
      {
        iterator.remove();
        return true;
      }
      return false;
    });
  }
  
  public boolean contains(ContextEventListener listener)
  {
    return applyForEachQueue(queue -> containsIn(listener, queue), true);
  }
  
  public void clear()
  {
    executeForEachQueue(Collection::clear);
  }
  
  private boolean containsIn(ContextEventListener listener, Queue listeners)
  {
    return applyForEachListenerInQueue(listeners,
        (contextEventListenerInfo, iterator) -> contextEventListenerInfo.getListener().equals(listener), true);
  }
  
  private static ContextEventListenerInfo getListenerInfo(Object ref)
  {
    if (ref instanceof ContextEventListener)
    {
      return new ContextEventListenerInfo((ContextEventListener) ref, false);
    }
    else if (ref instanceof Reference)
    {
      ContextEventListener cel = ((Reference<ContextEventListener>) ref).get();
      return cel != null ? new ContextEventListenerInfo(cel, true) : null;
    }
    else
    {
      throw new ContextRuntimeException("Unexpected reference: " + ref);
    }
  }
  
  public ContextManager getContextManager()
  {
    return context != null ? context.getContextManager() : contextManager;
  }
  
  private interface ExceptionalFunction<T>
  {
    boolean apply(T t, Iterator<T> iterator) throws EventHandlingException;
  }
}
