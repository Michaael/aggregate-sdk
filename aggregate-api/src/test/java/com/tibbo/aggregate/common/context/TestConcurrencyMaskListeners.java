package com.tibbo.aggregate.common.context;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ArrayListMultimap;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.ContextEventListenerInfo;
import com.tibbo.aggregate.common.event.ContextEventListenerSet;
import com.tibbo.aggregate.common.event.EventHandlingException;
import com.tibbo.aggregate.common.expression.Expression;

public class TestConcurrencyMaskListeners
{
  public static final String MASK = "mask";
  public static final int TOTAL_ITERATIONS = 1000;
  public static final String EVENT = "test_event";
  private Map<String, Map<String, ContextEventListenerSet>> maskListeners = new Hashtable();
  private ExecutorService pool = Executors.newFixedThreadPool(200);
  private final ReentrantReadWriteLock maskListenersLock = new ReentrantReadWriteLock();
  private AtomicInteger deviationsCount;
  
  @Test
  public void testAddMaskListener() throws InterruptedException
  {
    runAsyncIterations(() -> addMaskEventListener(MASK, EVENT, new DefaultContextEventListener()
    {
      @Override
      public void handle(Event event) throws EventHandlingException
      {
      }
    }, false));
    Assertions.assertEquals(maskListeners.get(MASK).get(EVENT).size(), TOTAL_ITERATIONS);
  }
  
  @Test
  public void testAddEventMaskListeners() throws InterruptedException
  {
    for (int i = 0; i < 200; i++)
    {
      addEventMaskListenersIteration(i);
    }
  }
  
  private void addEventMaskListenersIteration(int i) throws InterruptedException
  {
    EventData ed = new EventData(new EventDefinition("test" + i, new TableFormat()), (ContextManager) null);
    ArrayListMultimap<String, ContextEventListener> resultMap = ArrayListMultimap.create();
    CopyOnWriteArrayList<ContextEventListener> listenersCache = new CopyOnWriteArrayList<>();
    runAsyncIterations(() -> {
      ContextEventListener contextEventListener = addEventDataEventListener(ed);
      if (listenersCache.size() < 10)
      {
        synchronized (this)
        {
          if (listenersCache.size() < 10)
          {
            listenersCache.add(contextEventListener);
          }
        }
      }
      else
      {
        ed.addListener(listenersCache.get(ThreadLocalRandom.current().nextInt(1, 9)), false);
        ed.addListener(listenersCache.get(ThreadLocalRandom.current().nextInt(1, 9)), false);
        ed.addListener(listenersCache.get(ThreadLocalRandom.current().nextInt(1, 9)), false);
        ed.addListener(listenersCache.get(ThreadLocalRandom.current().nextInt(1, 9)), false);
        ed.addListener(listenersCache.get(ThreadLocalRandom.current().nextInt(1, 9)), false);
      }
      synchronized (this)
      {
        resultMap.put(contextEventListener.getFingerprint() == null ? "null" : contextEventListener.getFingerprint(),
            contextEventListener);
      }
    });
    
    AtomicInteger totalFilterListeners = new AtomicInteger(0);
    AtomicInteger totalFing1 = new AtomicInteger(0);
    AtomicInteger totalFing2 = new AtomicInteger(0);
    ed.doWithListeners(li -> {
      if (li.getListener() != null)
      {
        if (li.getListener().getFingerprint() == null)
        {
          totalFilterListeners.incrementAndGet();
        }
        else if (li.getListener().getFingerprint().equals("fing1"))
        {
          totalFing1.incrementAndGet();
        }
        else if (li.getListener().getFingerprint().equals("fing2"))
        {
          totalFing2.incrementAndGet();
        }
      }
    });
    Assertions.assertEquals(resultMap.get("null").size(), totalFilterListeners.intValue());
    Assertions.assertEquals(resultMap.get("fing1").size(), totalFing1.intValue());
    Assertions.assertEquals(resultMap.get("fing2").size(), totalFing2.intValue());
  }
  
  private ContextEventListener addEventDataEventListener(final EventData eventData)
  {
    ContextEventListener listener = getRandomFingerprintListener();
    eventData.addListener(listener, false);
    return listener;
    
  }
  
  private ContextEventListener getRandomFingerprintListener()
  {
    ContextEventListener listener;
    int i = ThreadLocalRandom.current().nextInt(1, 10);
    if (i <= 2)
    {
      listener = createListener("fing1");
    }
    else if (i <= 6)
    {
      listener = createListener("fing2");
      
    }
    else
    {
      listener = createListener(null);
      
    }
    return listener;
  }
  
  private ContextEventListener createListener(String fingerPrint)
  {
    return new ContextEventListener()
    {
      
      @Override
      public String getFingerprint()
      {
        return fingerPrint;
      }
      
      @Override
      public boolean shouldHandle(Event ev) throws EventHandlingException
      {
        return true;
      }
      
      @Override
      public void handle(Event event) throws EventHandlingException
      {
      }
      
      @Override
      public void handle(Event event, EventDefinition ed) throws EventHandlingException
      {
      }
      
      @Override
      public CallerController getCallerController()
      {
        return null;
      }
      
      @Override
      public Integer getListenerCode()
      {
        return null;
      }
      
      @Override
      public Expression getFilter()
      {
        return null;
      }
      
      @Override
      public boolean isAsync()
      {
        return false;
      }
      
      @Override
      public void setListenerCode(Integer listenerCode)
      {
        
      }
    };
  }
  
  @Test
  public void testMaskListeners() throws InterruptedException
  {
    runIterations(this::getContextMaskListenersThreadSafe);
    System.out.println("Thread safe. Total iterations: " + TOTAL_ITERATIONS + ". Failed iterations: " + deviationsCount.get());
    Assertions.assertEquals(0, deviationsCount.get());
    runIterations(this::getContextMaskListenersNotThreadSafe);
    System.out.println("Not Thread Safe. Total iterations: " + TOTAL_ITERATIONS + ". Failed iterations: " + deviationsCount.get());
  }
  
  private void runAsyncIterations(Runnable runnable, Object... args) throws InterruptedException
  {
    
    List<Callable<Void>> tasks = new ArrayList<>();
    for (int i = 0; i < TOTAL_ITERATIONS; i++)
    {
      tasks.add(() -> {
        try
        {
          runnable.run();
        }
        catch (Exception e)
        {
        }
        return null;
      });
    }
    pool.invokeAll(tasks);
  }
  
  private void runIterations(Function<String, Map<String, ContextEventListenerSet>> function) throws InterruptedException
  {
    deviationsCount = new AtomicInteger(0);
    for (int i = 0; i < TOTAL_ITERATIONS; i++)
    {
      maskListeners = new Hashtable();
      runIteration(function);
    }
  }
  
  private void runIteration(Function<String, Map<String, ContextEventListenerSet>> f) throws InterruptedException
  {
    AtomicInteger counter = new AtomicInteger(0);
    runAsyncIterations(() -> {
      Map<String, ContextEventListenerSet> mask = f.apply(MASK);
      maskListenersLock.writeLock().lock();
      mask.put("Thread" + counter.get(),
          new ContextEventListenerSet(new AbstractContext("Thread" + counter.get())
          {
          }));
      counter.getAndIncrement();
      maskListenersLock.writeLock().unlock();
    });
    if (maskListeners.get(MASK).size() != TOTAL_ITERATIONS)
    {
      deviationsCount.getAndIncrement();
    }
  }
  
  private void addMaskEventListener(String mask, String event, ContextEventListener listener, boolean weak)
  {
    ContextEventListenerSet listeners = getMaskListeners(mask, event);
    listeners.addListener(listener, weak);
  }
  
  private ContextEventListenerSet getMaskListeners(String mask, String event)
  {
    Map<String, ContextEventListenerSet> cel = getContextMaskListenersThreadSafe(mask);
    synchronized (this)
    {
      ContextEventListenerSet eel = cel.get(event);
      if (eel == null)
      {
        eel = new ContextEventListenerSet(new AbstractContext("Thread")
        {
        });
        cel.put(event, eel);
      }
      return eel;
    }
  }
  
  private Map<String, ContextEventListenerSet> getContextMaskListenersThreadSafe(String mask)
  {
    Map<String, ContextEventListenerSet> cel = null;
    maskListenersLock.readLock().lock();
    try
    {
      cel = maskListeners.get(mask);
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
        cel = maskListeners.get(mask);
        if (cel == null)
        {
          cel = new ConcurrentHashMap<>();
          maskListeners.put(mask, cel);
        }
      }
      finally
      {
        maskListenersLock.writeLock().unlock();
      }
    }
    return cel;
  }
  
  private Map<String, ContextEventListenerSet> getContextMaskListenersNotThreadSafe(String mask)
  {
    Map<String, ContextEventListenerSet> cel;
    maskListenersLock.readLock().lock();
    try
    {
      cel = maskListeners.get(mask);
    }
    finally
    {
      maskListenersLock.readLock().unlock();
    }
    
    if (cel == null)
    {
      cel = new ConcurrentHashMap<>();
      
      maskListenersLock.writeLock().lock();
      try
      {
        maskListeners.put(mask, cel);
      }
      finally
      {
        maskListenersLock.writeLock().unlock();
      }
    }
    
    return cel;
  }
  
}
