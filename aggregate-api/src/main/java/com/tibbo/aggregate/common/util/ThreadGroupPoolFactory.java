package com.tibbo.aggregate.common.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ThreadGroupPoolFactory implements ThreadFactory
{
  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  private final ThreadGroup group;
  private final AtomicInteger threadNumber;
  private final String namePrefix;
  
  public ThreadGroupPoolFactory(ThreadGroup group, String name)
  {
    this.group = group;
    threadNumber = new AtomicInteger(1);
    StringBuilder buffer = new StringBuilder();
    buffer.append(group.getName()).append("/");
    buffer.append(name).append("-").append(poolNumber.getAndIncrement()).append("-thread-");
    namePrefix = buffer.toString();
  }
  
  @Override
  public Thread newThread(Runnable r)
  {
    Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
    if (t.isDaemon())
      t.setDaemon(false);
    if (t.getPriority() != Thread.NORM_PRIORITY)
      t.setPriority(Thread.NORM_PRIORITY);
    return t;
  }
}