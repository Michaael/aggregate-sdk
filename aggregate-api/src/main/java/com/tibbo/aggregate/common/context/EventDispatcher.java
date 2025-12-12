package com.tibbo.aggregate.common.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.util.NamedThreadFactory;
import com.tibbo.aggregate.common.util.WatchdogHolder;

public class EventDispatcher extends Thread
{
  public static final int CONCURRENT_DISPATCHER_KEEP_ALIVE_SECONDS = 10;
  public static final int DEFAULT_DISPATCHER_POOL_SIZE = 50;
  
  private final AtomicLong eventsScheduled = new AtomicLong(0);
  private final AtomicLong eventsProcessed = new AtomicLong(0);
  
  private final String parentThreadName;
  private final BlockingQueue<QueuedEvent> undispatchedEvents;
  private volatile ThreadPoolExecutor dispatcherPool = null;    // volatile to prevent memory cache incoherence issues
  
  private Supplier<ThreadPoolExecutor> concurrentDispatcherSupplier;
  
  public EventDispatcher(int queueLength, Supplier<ThreadPoolExecutor> concurrentDispatcherSupplier)
  {
    this(queueLength);
    
    if (concurrentDispatcherSupplier != null)
    {
      this.concurrentDispatcherSupplier = concurrentDispatcherSupplier;
    }
  }
  
  public static ThreadPoolExecutor createConcurrentEventDispatcherPool(int coreSize, int maxCoreSize, int queueLength, String parentThreadName)
  {
    ThreadPoolExecutor dispatcherPool = new ThreadPoolExecutor(coreSize, maxCoreSize, CONCURRENT_DISPATCHER_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(queueLength), new NamedThreadFactory("ConcurrentEventDispatcher/" + parentThreadName), new ThreadPoolExecutor.CallerRunsPolicy());
    dispatcherPool.allowCoreThreadTimeOut(true);
    return dispatcherPool;
  }
  
  private EventDispatcher(int queueLength)
  {
    parentThreadName = Thread.currentThread().getName();
    setName("EventDispatcher/" + parentThreadName);
    setPriority(Thread.MAX_PRIORITY - 1); // Setting very high priority to avoid bottlenecks
    undispatchedEvents = new LinkedBlockingQueue<>(queueLength);
    concurrentDispatcherSupplier = () -> createConcurrentEventDispatcherPool(DEFAULT_DISPATCHER_POOL_SIZE, DEFAULT_DISPATCHER_POOL_SIZE, queueLength, parentThreadName);
  }
  
  public void queue(final QueuedEvent ev, FireEventRequestController request) throws InterruptedException
  {
    int concurrency = ev.getEventData().getDefinition().getConcurrency();
    
    if (concurrency == EventDefinition.CONCURRENCY_CONCURRENT)
    {
      queueConcurrently(ev);
    }
    else if (concurrency == EventDefinition.CONCURRENCY_SEQUENTIAL)
    {
      queueSequentially(ev, request);
    }
    else
    {
      ev.getEventData().dispatch(ev.getEvent());
      registerProcessedEvent();
    }
  }
  
  private void queueSequentially(QueuedEvent ev, FireEventRequestController request) throws InterruptedException
  {
    // Protecting from deadlocks by prohibiting new event submission from the dispatcher thread
    if (Thread.currentThread() == EventDispatcher.this)
    {
      getDispatcherPool().submit(() -> {
        try
        {
          queueInternal(ev, request);
        }
        catch (InterruptedException ex)
        {
          // Ignoring
        }
      });
    }
    else
    {
      queueInternal(ev, request);
    }
  }
  
  private void queueInternal(final QueuedEvent ev, FireEventRequestController request) throws InterruptedException
  {
    if (request != null && request.isSuppressIfNotEnoughMemory())
    {
      if (WatchdogHolder.getInstance().isEnoughMemory())
      {
        undispatchedEvents.put(ev);
      }
      else
      {
        Log.CONTEXT_EVENTS.warn("Event '" + ev.getEvent().getName() + "' in context '" + ev.getEvent().getContext() + "' was suppressed due to lack of RAM");
      }
    }
    else
    {
      WatchdogHolder.getInstance().awaitForEnoughMemory();
      
      undispatchedEvents.put(ev);
    }
  }
  
  @Override
  public void run()
  {
    while (!isInterrupted())
    {
      try
      {
        final QueuedEvent ev;
        
        try
        {
          ev = undispatchedEvents.take();
        }
        catch (InterruptedException ex)
        {
          break;
        }
        
        ev.dispatch();
        
        registerProcessedEvent();
      }
      catch (Throwable ex)
      {
        // Normally all errors should be handled in EventData.dispatch(), so there are almost no chances we'll get here
        Log.CONTEXT_EVENTS.fatal("Unexpected critical error in event dispatcher", ex);
      }
    }
    
    if (dispatcherPool != null)
    {
      dispatcherPool.shutdown();
    }
    
    Log.CONTEXT_EVENTS.debug("Stopping event dispatcher");
  }
  
  private void queueConcurrently(QueuedEvent ev)
  {
    ev.getEventData().queue(ev);
    
    if (!ev.getEventData().isDispatching())
    {
      ev.getEventData().setDispatching(true);
      
      getDispatcherPool().submit(() -> ev.getEventData().dispatchAll(EventDispatcher.this));
    }
  }
  
  private ThreadPoolExecutor getDispatcherPool()
  {
    if (dispatcherPool == null)
    {
      synchronized (this)
      {
        if (dispatcherPool == null)   // double-check to account in-between modifications from other threads
        {
          dispatcherPool = concurrentDispatcherSupplier.get();
        }
      }
    }
    return dispatcherPool;
  }
  
  public int getQueueLength()
  {
    return undispatchedEvents.size();
  }
  
  public long getEventsProcessed()
  {
    return eventsProcessed.get();
  }
  
  public long getEventsScheduled()
  {
    return eventsScheduled.get();
  }
  
  public Map<String, Long> getEventQueueStatistics()
  {
    final Map<String, Long> result = new HashMap<>();
    
    for (QueuedEvent event : undispatchedEvents)
    {
      final String context = event.getEvent().getContext();
      Long count = result.get(context);
      
      if (count == null)
      {
        count = 1L;
      }
      else
      {
        count++;
      }
      
      result.put(context, count);
    }
    
    return result;
  }
  
  public void registerIncomingEvent()
  {
    eventsScheduled.incrementAndGet();
  }
  
  public void registerProcessedEvent()
  {
    eventsProcessed.incrementAndGet();
  }
}