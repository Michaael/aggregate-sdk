package com.tibbo.aggregate.common.communication;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

import javax.swing.*;

import org.apache.log4j.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.device.*;
import com.tibbo.aggregate.common.util.*;

public abstract class AbstractDeviceController<I extends Command, O extends Command> implements CommandParserListener
{
  private long commandTimeout;
  
  private boolean resetTimeoutsOnData;
  
  private final Logger logger;
  
  private CommandParser<I> commandParser;
  
  private volatile AsyncCommandProcessor<I, O, AbstractDeviceController<I, O>> processor;
  
  private final Lock connectLock = new ReentrantLock();
  private volatile boolean connecting;
  private volatile boolean connected = false;
  
  private final Lock loginLock = new ReentrantLock();
  private boolean loggingIn;
  private boolean loggedIn = false;
  
  public AbstractDeviceController(long commandTimeout, Logger logger)
  {
    super();
    this.commandTimeout = commandTimeout;
    this.logger = logger;
  }
  
  abstract protected boolean connectImpl() throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException;
  
  abstract protected boolean loginImpl() throws ContextException;
  
  abstract protected void disconnectImpl() throws IOException, InterruptedException, RemoteDeviceErrorException;
  
  abstract protected void send(O cmd) throws DisconnectionException, IOException, InterruptedException;
  
  public void connect() throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException
  {
    if (isConnected() || connecting)
    {
      return;
    }
    if (!connectLock.tryLock())
    {
      return;
    }
    try
    {
      try
      {
        connecting = true;
        
        if (connectImpl())
        {
          setConnected(true);
        }
      }
      finally
      {
        connecting = false;
      }
    }
    finally
    {
      connectLock.unlock();
    }
  }
  
  protected void checkAndConnect() throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      if (logger != null)
      {
        logger.debug("Net I/O from event dispatcher thread", new Exception());
      }
    }
    
    connect();
  }
  
  public void login() throws ContextException
  {
    loginLock.lock();
    try
    {
      if (isLoggedIn() || loggingIn)
      {
        return;
      }
      
      try
      {
        loggingIn = true;
        if (loginImpl())
        {
          setLoggedIn(true);
        }
      }
      finally
      {
        loggingIn = false;
      }
    }
    finally
    {
      loginLock.unlock();
    }
  }
  
  public synchronized void disconnect() throws IOException, InterruptedException, RemoteDeviceErrorException
  {
    if (processor != null && !processor.isInterrupted())
    {
      processor.interrupt();
      processor.join(); // Processor will call controller's disconnectImpl() during shutdown
    }
    
    setLoggedIn(false);
    setConnected(false);
  }
  
  public I sendCommand(O cmd) throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException
  {
    checkAndConnect();
    
    I reply;
    try
    {
      reply = processor.sendSyncCommand(cmd);
    }
    catch (DisconnectionException ex)
    {
      setLoggedIn(false);
      setConnected(false);
      throw ex;
    }
    
    if (logger != null && logger.isDebugEnabled())
    {
      logger.debug("Received reply: " + (reply != null ? StringUtils.toHexString(reply.toByteArray()) : "null") +
          " to command: " + (cmd != null ? StringUtils.toHexString(cmd.toByteArray()) : "null"));
    }
    
    return reply;
  }
  
  public boolean isActive()
  {
    if (connecting || loggingIn)
    {
      return true;
    }
    if (processor != null)
    {
      return processor.isActive();
    }
    return false;
  }
  
  protected void processAsyncCommand(I cmd)
  {
    if (logger != null && logger.isInfoEnabled())
    {
      Log.COMMANDS.info("Received async command: " + cmd);
    }
  }
  
  @Override
  public void newDataReceived()
  {
    if (resetTimeoutsOnData)
    {
      resetCommandTimeouts();
    }
  }
  
  protected void resetCommandTimeouts()
  {
    processor.resetSentCommandTimeouts();
  }
  
  protected void startCommandProcessor()
  {
    processor = new AsyncCommandProcessor(this);
    processor.start();
  }
  
  protected void setCommandParser(CommandParser commandParser)
  {
    this.commandParser = commandParser;
    commandParser.setListener(this);
    startCommandProcessor();
  }
  
  public boolean isConnected()
  {
    return connected && (processor == null || processor.isAlive());
  }
  
  protected void setConnected(boolean connected)
  {
    this.connected = connected;
  }
  
  public boolean isLoggedIn()
  {
    return isConnected() && loggedIn;
  }
  
  public void setLoggedIn(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }
  
  protected CommandParser<I> getCommandParser()
  {
    return commandParser;
  }
  
  public void setResetTimeoutsOnData(boolean resetTimeoutWhenDataReceived)
  {
    this.resetTimeoutsOnData = resetTimeoutWhenDataReceived;
  }
  
  public List<ReplyMonitor<O, I>> getActiveCommands()
  {
    return processor != null ? processor.getActiveCommands() : new LinkedList();
  }
  
  public long getCommandTimeout()
  {
    return commandTimeout;
  }
  
  public void setCommandTimeout(long commandTimeout)
  {
    this.commandTimeout = commandTimeout;
  }
  
  public Logger getLogger()
  {
    return logger;
  }
  
  public CommandProcessorStatistics getStatistics()
  {
    return processor != null ? processor.getStatistics() : null;
  }
}
