package com.tibbo.aggregate.common.protocol;

import java.util.*;
import java.util.concurrent.*;

public class CommandQueue
{
  private Queue<CommandTask> queue = new LinkedBlockingQueue<CommandTask>();
  
  private Semaphore activityLock = new Semaphore(1);
  
  public void add(CommandTask command)
  {
    queue.add(command);
  }
  
  public CommandTask poll()
  {
    return queue.poll();
  }
  
  public Semaphore getActivityLock()
  {
    return activityLock;
  }
  
}
