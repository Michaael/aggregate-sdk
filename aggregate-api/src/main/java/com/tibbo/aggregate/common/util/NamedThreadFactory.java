package com.tibbo.aggregate.common.util;

import java.util.concurrent.*;

public class NamedThreadFactory implements ThreadFactory
{
  private String name;
  private Integer priority;
  private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
  
  public NamedThreadFactory()
  {
  }
  
  public NamedThreadFactory(String name)
  {
    this.name = name;
  }
  
  public NamedThreadFactory(String name, int priority)
  {
    this.name = name;
    this.priority = priority;
  }
  
  protected String getName()
  {
    if (name == null)
    {
      throw new IllegalStateException("No factory name defined");
    }
    
    return name;
  }
  
  public Thread newThread(Runnable r)
  {
    Thread t = threadFactory.newThread(r);
    t.setName(getName() + "/" + t.getName());
    t.setDaemon(true);
    if (priority != null)
    {
      t.setPriority(priority);
    }
    return t;
  }
}