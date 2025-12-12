package com.tibbo.aggregate.common.context;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.ContextEventListenerInfo;
import com.tibbo.aggregate.common.event.ContextEventListenerSet;
import com.tibbo.aggregate.common.event.Enrichment;
import com.tibbo.aggregate.common.util.Util;

public class EventData implements Comparable<EventData>
{
  public static final int UNDISPATCHED_EVENTS_QUEUE_LENGTH = 10_000;
  
  private EventDefinition definition;
  
  private final ContextEventListenerSet listeners;

  private long subscribeCount;

  private long unsubscribeCount;

  private final List<Event> history = Collections.synchronizedList(new LinkedList());
  
  private long fireCount;
  
  private final ReentrantReadWriteLock duplicateProcessingLock = new ReentrantReadWriteLock();
  
  private BlockingQueue<QueuedEvent> undispatchedEvents;
  
  private volatile boolean dispatching;
  private long handleOffers;
  private long handleExecutions;
  
  public EventData(EventDefinition definition, ContextManager contextManager)
  {
    this.definition = definition;
    listeners = new ContextEventListenerSet(contextManager);
  }
  
  public EventData(EventDefinition definition, AbstractContext context)
  {
    this.definition = definition;
    listeners = new ContextEventListenerSet(context);
  }
  
  public void registerFiredEvent()
  {
    fireCount++;
  }
  
  public void registerHandleOffer()
  {
    handleOffers++;
  }
  
  public void registerHandleExecution()
  {
    handleExecutions++;
  }
  
  public EventDefinition getDefinition()
  {
    return definition;
  }
  
  public long getFireCount()
  {
    return fireCount;
  }
  
  public ReentrantReadWriteLock getDuplicateProcessingLock()
  {
    return duplicateProcessingLock;
  }
  
  public boolean addListener(ContextEventListener listener, boolean weak)
  {
    boolean actuallyAdded = listeners.addListener(listener, weak);
    // may be false if such listener has already been added before
    if (actuallyAdded)
    {
      subscribeCount++;
    }
    return actuallyAdded;
  }
  
  public boolean removeListener(ContextEventListener listener)
  {
    boolean actuallyRemoved = listeners.removeListener(listener);
    // may be false if there was no such listener
    if (actuallyRemoved)
    {
      unsubscribeCount++;
    }
    return actuallyRemoved;
  }
  
  public void clearListeners()
  {
    listeners.clear();
  }
  
  public boolean hasListeners()
  {
    return listeners.hasListeners();
  }
  
  public void dispatch(Event event)
  {
    listeners.dispatch(event, getDefinition(), this);
  }
  
  public Event store(Event event, Integer customMemoryStorageSize)
  {
    Integer memoryStorageSize = customMemoryStorageSize != null ? customMemoryStorageSize : definition.getMemoryStorageSize();
    
    if (memoryStorageSize == null)
    {
      return null;
    }
    
    Event duplicate = null;
    
    synchronized (history)
    {
      for (Iterator<Event> iterator = history.iterator(); iterator.hasNext();)
      {
        Event cur = iterator.next();
        
        // Removing if expired
        if (cur.getExpirationtime() != null && cur.getExpirationtime().getTime() < System.currentTimeMillis())
        {
          iterator.remove();
          continue;
        }
        
        // Adding for persistent storage if in-memory history size exceeded
        if (history.size() > memoryStorageSize)
        {
          iterator.remove();
          continue;
        }
        
        if (event.getDeduplicationId() != null && Util.equals(event.getDeduplicationId(), cur.getDeduplicationId()))
        {
          if (duplicate != null)
          {
            Log.CONTEXT_EVENTS.warn("Event history of event " + event + " contains more than one duplicate with ID: " + event.getDeduplicationId());
          }
          
          duplicate = cur;
        }
      }
    }
    
    if (duplicate == null)
    {
      if (history.size() < memoryStorageSize)
      {
        history.add(event);
      }
      return null;
    }
    else
    {
      Log.CONTEXT_EVENTS.debug("Found duplicate of event " + event + " (duplicate ID: " + event.getDeduplicationId() + "): " + duplicate);
      if (duplicate.getCount() == 1)
      {
        duplicate.addEnrichment(new Enrichment(Enrichment.FIRST_OCCURRENCE, DateFieldFormat.dateToString(duplicate.getCreationtime()), duplicate.getCreationtime(), null));
      }
      duplicate.setCreationtime(event.getCreationtime());
      duplicate.setCount(duplicate.getCount() + 1);
      return duplicate;
    }
  }
  
  public void updateContext(String oldPath, String newPath)
  {
    for (Event ev : history)
    {
      if (ev.getContext().equals(oldPath))
      {
        ev.setContext(newPath);
      }
    }
  }
  
  public List<Event> getHistory()
  {
    return new LinkedList(history);
  }
  
  @Override
  public String toString()
  {
    return definition + " - " + listeners.size() + " listeners";
  }
  
  @Override
  public int compareTo(EventData d)
  {
    return definition.compareTo(d.getDefinition());
  }
  
  public void queue(QueuedEvent ev)
  {
    if (undispatchedEvents == null)
    {
      undispatchedEvents = new LinkedBlockingQueue(definition.getQueueLength());
    }
    
    try
    {
      undispatchedEvents.put(ev);
    }
    catch (InterruptedException ex)
    {
      // Ignoring
    }
  }
  
  public void dispatchAll(EventDispatcher ed)
  {
    try
    {
      // Prevent multiple dispatch processing, syncing on EventData can cause a deadlock, so using undispatchedEvents here
      synchronized (undispatchedEvents)
      {
        do
        {
          if (undispatchedEvents.size() <= 1)
          {
            dispatching = false;
          }
          
          QueuedEvent ev = undispatchedEvents.poll();
          
          if (ev == null)
          {
            return;
          }
          
          ev.dispatch();
          ed.registerProcessedEvent();
        }
        while (true);
      }
    }
    finally
    {
      dispatching = false;
    }
  }
  
  public boolean isDispatching()
  {
    return dispatching;
  }
  
  public void setDispatching(boolean dispatching)
  {
    this.dispatching = dispatching;
  }
  
  public void setDefinition(EventDefinition eventDefinition)
  {
    definition = eventDefinition;
  }
  
  public long getHandleOffers()
  {
    return handleOffers;
  }
  
  public long getHandleExecutions()
  {
    return handleExecutions;
  }
  
  public long getListenersCount()
  {
    return listeners.size();
  }

  public long getSubscribeCount()
  {
    return subscribeCount;
  }

  public long getUnsubscribeCount()
  {
    return unsubscribeCount;
  }

  public long getQueueLength()
  {
    return undispatchedEvents == null ? 0 : undispatchedEvents.size();
  }
  
  public boolean shouldHandle(Event event)
  {
    return listeners.shouldHandle(event);
  }
  
  public void addListeners(ContextEventListenerSet targetListeners)
  {
    listeners.executeForEachListener(li -> targetListeners.addListener(li.getListener(), li.isWeak()));
  }
  
  public void doWithListeners(Consumer<ContextEventListenerInfo> action)
  {
    synchronized (listeners)
    {
      listeners.executeForEachListener(action);
    }
  }
  
  public boolean contains(ContextEventListener listener)
  {
    return listeners.contains(listener);
  }
}
