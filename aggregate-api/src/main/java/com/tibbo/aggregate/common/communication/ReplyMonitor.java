package com.tibbo.aggregate.common.communication;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import com.tibbo.aggregate.common.*;

public class ReplyMonitor<C extends Command, R extends Command>
{
  private C command = null;
  
  private R reply = null;
  
  private final long startTime;
  private final long time;
  
  private final Lock lock = new ReentrantLock();
  private final Condition commandReceivedCondition = lock.newCondition();
  
  private boolean timeoutReset;
  private boolean terminated;
  
  public ReplyMonitor(C command)
  {
    super();
    this.command = command;
    startTime = System.currentTimeMillis();
    time = startTime;
  }
  
  public C getCommand()
  {
    return command;
  }
  
  public R getReply()
  {
    return reply;
  }
  
  public void setReply(R reply)
  {
    lock.lock();
    try
    {
      this.reply = reply;
      commandReceivedCondition.signalAll();
    }
    finally
    {
      lock.unlock();
    }
    if (Log.COMMANDS.isDebugEnabled())
      Log.COMMANDS.debug("Command replied in " + (System.currentTimeMillis() - time) + " ms: command '" + command + "', reply '" + reply + "'");
  }
  
  public void terminate()
  {
    lock.lock();
    try
    {
      terminated = true;
      commandReceivedCondition.signalAll();
    }
    finally
    {
      lock.unlock();
    }
  }
  
  public void reset()
  {
    lock.lock();
    try
    {
      timeoutReset = true;
      commandReceivedCondition.signalAll();
    }
    finally
    {
      lock.unlock();
    }
  }
  
  public boolean waitReply(long timeout) throws InterruptedException
  {
    lock.lockInterruptibly();
    try
    {
      do
      {
        if (reply != null)
        {
          return true;
        }
        if (terminated)
        {
          return false;
        }
        timeoutReset = false;
        commandReceivedCondition.await(timeout, TimeUnit.MILLISECONDS);
      }
      while (timeoutReset);
    }
    finally
    {
      lock.unlock();
    }
    
    return reply != null;
  }
  
  public long getTime()
  {
    return time;
  }
  
  public long getStartTime()
  {
    return startTime;
  }
  
  @Override
  public String toString()
  {
    return "ReplyMonitor [command: " + command + ", reply: " + reply + "]";
  }
}
