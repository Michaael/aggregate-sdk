package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.tests.*;

public class TestEventData extends CommonsTestCase
{
  private TestListener weak1;
  private TestListener weak2;
  private TestListener weak3;
  
  private TestListener strong1;
  private TestListener strong2;
  private TestListener strong3;
  
  public void testWeakAndStrongListeners()
  {
    EventData ed = new EventData(new EventDefinition("test", new TableFormat()), (ContextManager) null);
    
    strong1 = new TestListener("s1");
    strong2 = new TestListener("s2");
    strong3 = new TestListener("s3");
    
    assertTrue(ed.addListener(strong1, false)); // First-time adding
    ed.addListener(strong2, false);
    ed.addListener(strong3, false);
    assertEquals(3, ed.getListenersCount());
    
    assertFalse(ed.addListener(strong1, false)); // Adding duplicate
    assertEquals(3, ed.getListenersCount());
    
    weak1 = new TestListener("w1");
    weak2 = new TestListener("w2");
    weak3 = new TestListener("w3");
    
    ed.addListener(weak1, true);
    ed.addListener(weak2, true);
    ed.addListener(weak3, true);
    System.gc();
    
    // Weak listeners still are referenced from somewhere else, should not have been GC'ed
    assertEquals(6, ed.getListenersCount());
    
    ed.addListener(weak1, true); // Trying to add weak dupe
    assertEquals(6, ed.getListenersCount());
    
    weak1 = null;
    weak2 = null;
    System.gc();
    
    // Weak listeners not are referenced anymore, should have been GC'ed now
    assertEquals(4, ed.getListenersCount());
    
    strong1 = null;
    strong2 = null;
    System.gc();
    
    // Strong listener should not have been GC'ed
    assertEquals(4, ed.getListenersCount());
    
    ed.removeListener(weak3);
    assertEquals(3, ed.getListenersCount());
    
    ed.removeListener(strong3);
    assertEquals(2, ed.getListenersCount());
    
  }
  
  public void testShouldHandleEventListener() throws EventHandlingException
  {
    DefaultContextEventListener contextEventListener = new DefaultContextEventListener()
    {
      @Override
      public void handle(Event event) throws EventHandlingException
      {
      
      }
    };
    Event ev1 = new Event();
    Event ev2 = new Event();
    assertEquals(true, contextEventListener.shouldHandle(ev1));
    
    assertEquals(true, contextEventListener.shouldHandle(ev2));
    
    ev1.setListener(12345);
    assertEquals(false, contextEventListener.shouldHandle(ev1));
    
    ev2.setListener(54321);
    assertEquals(false, contextEventListener.shouldHandle(ev2));
    
    contextEventListener.setAcceptEventsWithoutListenerCode(true);
    
    ev1.setListener(12345);
    assertEquals(true, contextEventListener.shouldHandle(ev1));
    
    ev2.setListener(54321);
    assertEquals(true, contextEventListener.shouldHandle(ev2));
    
    contextEventListener = new DefaultContextEventListener(98765)
    {
      @Override
      public void handle(Event event) throws EventHandlingException
      {
        
      }
    };
    
    assertEquals(false, contextEventListener.shouldHandle(ev1));
    
    assertEquals(false, contextEventListener.shouldHandle(ev2));
    
    ev1.setListener(null);
    ev2.setListener(null);
    
    assertEquals(false, contextEventListener.shouldHandle(ev1));
    
    assertEquals(false, contextEventListener.shouldHandle(ev2));
    
    contextEventListener.setAcceptEventsWithoutListenerCode(true);
    
    assertEquals(true, contextEventListener.shouldHandle(ev1));
    
    assertEquals(true, contextEventListener.shouldHandle(ev2));
    
    ev1.setListener(98765);
    ev2.setListener(56789);
    
    assertEquals(true, contextEventListener.shouldHandle(ev1));
    
    assertEquals(false, contextEventListener.shouldHandle(ev2));
    
    ev2.setListener(98765);
    
    assertEquals(true, contextEventListener.shouldHandle(ev1));
  }
  
  private static class TestListener extends DefaultContextEventListener
  {
    private final String name;
    
    public TestListener(String name)
    {
      this.name = name;
    }
    
    @Override
    public void handle(Event event) throws EventHandlingException
    {
    }
    
    @Override
    public String toString()
    {
      return name;
    }
    
  }
}
