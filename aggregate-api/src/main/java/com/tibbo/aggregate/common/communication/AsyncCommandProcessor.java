package com.tibbo.aggregate.common.communication;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.net.ssl.SSLException;
import javax.swing.*;

import org.apache.log4j.Level;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.TimeHelper;

public class AsyncCommandProcessor<I extends Command, O extends Command, C extends AbstractDeviceController<I, O>> extends Thread
{
  private static final long PENDING_COMMAND_TIMEOUT = TimeHelper.DAY_IN_MS;
  
  private final C controller;
  
  // Queue contains commands without ids
  private final ConcurrentLinkedQueue<ReplyMonitor<O, I>> sentCommandsQueue = new ConcurrentLinkedQueue<>();
  
  // Map contains commands by ids
  private final ConcurrentHashMap<String, ReplyMonitor<O, I>> sentCommandsMap = new ConcurrentHashMap<>();
  
  private final Object commandQueueMonitor = new Object();
  
  private final CommandProcessorStatistics statistics = new CommandProcessorStatistics();
  
  public AsyncCommandProcessor(C controller)
  {
    super();
    this.controller = controller;
    setName("AsyncCommandProcessor/" + controller.toString() + "/" + getName());
  }
  
  public I sendSyncCommand(O cmd) throws DisconnectionException, IOException, InterruptedException
  {
    if (Log.COMMANDS.isDebugEnabled() && SwingUtilities.isEventDispatchThread())
    {
      Log.COMMANDS.debug("Device I/O from event dispatch thread", new Exception());
    }
    
    ReplyMonitor<O, I> mon = sendCommand(cmd);
    
    if (cmd.isAsync())
    {
      return null;
    }
    
    I reply = waitReplyMonitor(mon);
    
    statistics.updateOnSyncCommand(mon);
    
    return reply;
  }
  
  public void sendUnrepliedCommand(O cmd) throws DisconnectionException, IOException
  {
    sendCommandImplementation(cmd);
    if (controller.getLogger() != null && controller.getLogger().isDebugEnabled())
    {
      controller.getLogger().debug("Sent command: " + StringUtils.toHexString(cmd.toByteArray()));
    }
  }
  
  public void resetSentCommandTimeouts()
  {
    synchronized (commandQueueMonitor)
    {
      sentCommandsMap.forEach((s, cur) -> cur.reset());
      sentCommandsQueue.forEach(ReplyMonitor::reset);
    }
  }
  
  private void sendCommandImplementation(O cmd) throws DisconnectionException, IOException
  {
    try
    {
      controller.send(cmd);
    }
    catch (InterruptedException ex)
    {
      throw new IOException(ex);
    }
  }
  
  private synchronized ReplyMonitor sendCommand(final O cmd) throws DisconnectionException, IOException, InterruptedException
  {
    if (!isAlive())
    {
      synchronized (commandQueueMonitor)
      {
        sentCommandsMap.forEach((s, cur) -> cur.setReply(null));
        sentCommandsQueue.forEach(cur -> cur.setReply(null));
      }
      
      throw new DisconnectionException(Cres.get().getString("disconnected"));
    }
    
    final ReplyMonitor mon = new ReplyMonitor(cmd);
    
    Future<Throwable> future = SendersPool.get().submit(new Callable()
    {
      @Override
      public Throwable call() throws Exception
      {
        try
        {
          addSentCommand(mon);
          sendUnrepliedCommand(cmd);
          return null;
        }
        catch (Throwable ex)
        {
          return ex;
        }
      }
    });
    
    try
    {
      Throwable th = future.get();
      
      if (th == null)
      {
        return mon;
      }
      else if (th instanceof DisconnectionException)
      {
        throw (DisconnectionException) th;
      }
      else if (th instanceof IOException)
      {
        throw (IOException) th;
      }
      else if (th instanceof InterruptedException)
      {
        throw (InterruptedException) th;
      }
      else
      {
        throw new IOException(th.getMessage(), th);
      }
    }
    catch (ExecutionException ex)
    {
      throw new IOException(ex.getMessage(), ex);
    }
  }
  
  private I waitReplyMonitor(ReplyMonitor<O, I> mon) throws IOException, InterruptedException
  {
    if (mon.getReply() == null)
    {
      long timeout = mon.getCommand().getTimeout() != null ? mon.getCommand().getTimeout() : controller.getCommandTimeout();
      
      mon.waitReply(timeout);
    }
    String id = mon.getCommand().getId();
    if (id != null)
    {
      sentCommandsMap.remove(id);
    }
    else
    {
      sentCommandsQueue.remove(mon);
    }
    
    if (mon.getReply() != null)
    {
      return mon.getReply();
    }
    else
    {
      if (Log.COMMANDS.isDebugEnabled())
        throw new IOException(MessageFormat.format(Cres.get().getString("cmdTimeout"), mon.getCommand()));
      else
        throw new IOException(MessageFormat.format(Cres.get().getString("cmdTimeout"), id));
    }
  }
  
  private void addSentCommand(final ReplyMonitor<O, I> mon)
  {
    String id = mon.getCommand().getId();
    if (id == null)
    {
      sentCommandsQueue.add(mon);
    }
    else
    {
      sentCommandsMap.put(id, mon);
    }
  }
  
  @Override
  public void run()
  {
    
    I cmd;
    try
    {
      while (!isInterrupted())
      {
        cmd = controller.getCommandParser().readCommand();
        
        if (cmd != null)
        {
          if (controller.getLogger() != null && controller.getLogger().isDebugEnabled())
          {
            controller.getLogger().debug("Received command: " + StringUtils.toHexString(cmd.toByteArray()));
          }
          
          if (cmd.isAsync())
          {
            controller.processAsyncCommand(cmd);
            
            statistics.updateOnAsyncCommand(cmd);
          }
          else
          {
            String commandId = cmd.getId();
            ReplyMonitor<O, I> replyMonitor;
            if (commandId != null)
            {
              replyMonitor = sentCommandsMap.remove(commandId);
            }
            else
            {
              replyMonitor = sentCommandsQueue.poll();
            }
            
            if (replyMonitor != null)
            {
              synchronized (replyMonitor)
              {
                replyMonitor.setReply(cmd);
              }
            }
            else
            {
              if (controller.getLogger() != null && controller.getLogger().isDebugEnabled())
              {
                controller.getLogger().debug("Reply cannot be matched to a sent command: "
                    + (commandId == null ? "commands queue is empty" : "Command Id: " + commandId + ", commands in progress: " + sentCommandsMap.size()));
              }
            }
            removeExpiredCommands();
          }
        }
      }
      
      controller.disconnectImpl();
    }
    catch (DisconnectionException ex)
    {
      processError(Level.DEBUG, "Disconnection of peer detected in async processor", ex);
    }
    catch (ClosedByInterruptException ex)
    {
      processError(Level.DEBUG, "Async processor interrupted", ex);
    }
    catch (SocketException ex)
    {
      processError(Level.DEBUG, "Socket error in async processor", ex);
    }
    catch (SSLException ex)
    {
      processError(Level.DEBUG, "SSL error in async processor", ex);
    }
    catch (Exception ex)
    {
      processError(Level.ERROR, "Error in async processor", ex);
    }
    finally
    {
      sentCommandsQueue.forEach(ReplyMonitor::terminate);
      sentCommandsQueue.clear();
      sentCommandsMap.values().forEach(ReplyMonitor::terminate);
      sentCommandsMap.clear();
    }
  }
  
  private void removeExpiredCommands()
  {
    sentCommandsMap.entrySet().removeIf(entry -> isExpired(entry.getValue()));
    sentCommandsQueue.removeIf(this::isExpired);
  }
  
  private boolean isExpired(ReplyMonitor<O, I> oiReplyMonitor)
  {
    long thisTime = System.currentTimeMillis();
    if (thisTime - oiReplyMonitor.getTime() > PENDING_COMMAND_TIMEOUT)
    {
      if (controller.getLogger() != null && controller.getLogger().isInfoEnabled())
      {
        controller.getLogger().info("Removing expired reply monitor: " + oiReplyMonitor);
      }
      return true;
    }
    return false;
  }
  
  private void processError(Level priority, String message, Exception ex)
  {
    if (controller.getLogger() != null)
    {
      controller.getLogger().log(priority, message + ": " + ex);
    }
    try
    {
      controller.disconnectImpl();
    }
    catch (Exception ex1)
    {
      controller.getLogger().error("Disconnection error", ex1);
    }
  }
  
  public boolean isActive()
  {
    return !sentCommandsQueue.isEmpty() || !sentCommandsMap.isEmpty();
  }
  
  public List<ReplyMonitor<O, I>> getActiveCommands()
  {
    List<ReplyMonitor<O, I>> res = new LinkedList<>();
    
    synchronized (commandQueueMonitor)
    {
      res.addAll(sentCommandsMap.values());
      res.addAll(sentCommandsQueue);
    }
    
    return res;
  }
  
  @Override
  public void interrupt()
  {
    Log.CORE_THREAD.debug("Thread '" + getName() + "' is interrupted by '" + Thread.currentThread().getName() + "'");
    super.interrupt();
  }
  
  public C getController()
  {
    return controller;
  }
  
  public CommandProcessorStatistics getStatistics()
  {
    return statistics;
  }
  
}
