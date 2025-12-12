package com.tibbo.aggregate.common.agent;

import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.DefaultContextManager;
import com.tibbo.aggregate.common.context.DefaultContextVisitor;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.datatable.DataTableRegistry;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.encoding.LocalFormatCache;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.device.RemoteDeviceErrorException;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.SslHelper;
import com.tibbo.aggregate.common.util.BlockingChannel;
import com.tibbo.aggregate.common.util.NamedThreadFactory;
import com.tibbo.aggregate.common.util.SocketBlockingChannel;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class Agent
{
  public static final int DEFAULT_PORT = 6480;
  
  public static final int DEFAULT_SECURE_PORT = 6481;
  
  private final RemoteServer server;
  
  private final AgentContext context;
  
  private final ContextManager<Context> contextManager;
  
  private boolean useSecureConnection = false;
  
  private boolean useDataCompression = false;
  
  private AgentImplementationController controller;
  
  private ThreadPoolExecutor commandExecutionService;
  
  private int bufferSize;
  private int pendingEventsQueueCapacity = Integer.MAX_VALUE;
  private int maxCommandExecutors = Integer.MAX_VALUE;
  
  public Agent(RemoteServer server, String name, boolean eventConfirmation)
  {
    this.server = server;
    this.context = new AgentContext(server, name, eventConfirmation);
    this.contextManager = new AgentContextManager(context, false);
  }
  
  public Agent(AgentContext context)
  {
    this(context, false, false, 0);
  }
  
  public Agent(AgentContext context, boolean useSecureConnection, boolean useDataCompression, int bufferSize)
  {
    this.server = context.getServer();
    this.context = context;
    this.contextManager = new AgentContextManager(context, false);
    this.useSecureConnection = useSecureConnection;
    this.useDataCompression = useDataCompression;
    this.bufferSize = bufferSize;
  }
  
  public Agent(AgentContext context, boolean useSecureConnection, boolean useDataCompression, int bufferSize, int pendingEventsQueueCapacity, int maxCommandExecutors)
  {
    this(context, useSecureConnection, useDataCompression, bufferSize);
    this.pendingEventsQueueCapacity = pendingEventsQueueCapacity;
    this.maxCommandExecutors = maxCommandExecutors;
  }
  
  public void connect() throws RemoteDeviceErrorException
  {
    try
    {
      context.setSynchronized(false);
      
      Log.PROTOCOL.debug("Connecting to remote server (" + server + ")");
      
      BlockingChannel dataChannel = constructChannel();
      
      Log.PROTOCOL.debug("Connection with remote server established");
      
      contextManager.start();
      
      FormatCache formatCache = new LocalFormatCache(server.getInfo());
      context.setFormatCache(formatCache);
      
      DataTableRegistry dataTableRegistry = new DataTableRegistry();
      
      commandExecutionService = new ThreadPoolExecutor(maxCommandExecutors, maxCommandExecutors, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NamedThreadFactory("Agent/" + server));
      
      commandExecutionService.allowCoreThreadTimeOut(true);
      
      controller = new AgentImplementationController(dataChannel, contextManager, commandExecutionService, formatCache, dataTableRegistry, pendingEventsQueueCapacity);
      
      context.setKnownFormatCollector(controller.getKnownFormatCollector());
      
      contextManager.getRoot().accept(new DefaultContextVisitor()
      {
        @Override
        public void visit(Context context)
        {
          final List<EventDefinition> eventDefinitions = context.getEventDefinitions();
          for (EventDefinition ed : eventDefinitions)
          {
            if (ed.getGroup() != null)
            {
              context.addEventListener(ed.getName(), controller.getDefaultEventListener(), true);
            }
          }
        }
      });
    }
    catch (Exception ex)
    {
      throw new RemoteDeviceErrorException(MessageFormat.format(Cres.get().getString("devErrConnecting"), server.getDescription() + " (" + server.getInfo() + ")") + ex.getMessage(), ex);
    }
  }
  
  protected BlockingChannel constructChannel() throws IOException
  {
    if (useSecureConnection)
    {
      SSLSocketFactory sslFactory = SslHelper.getTrustedSocketFactory();
      
      SSLSocket socket = (SSLSocket) sslFactory.createSocket(server.getAddress(), server.getPort());
      
      socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
      socket.setSoTimeout((int) server.getConnectionTimeout());
      socket.startHandshake();
      
      return new SocketBlockingChannel(socket, useDataCompression, bufferSize);
    }
    else
    {
      Socket socket = SocketFactory.getDefault().createSocket(server.getAddress(), server.getPort());
      
      socket.setSoTimeout((int) server.getConnectionTimeout());
      
      return new SocketBlockingChannel(socket, useDataCompression, bufferSize);
    }
  }
  
  public void disconnect()
  {
    if (controller != null)
    {
      controller.shutdown();
    }
    
    if (contextManager != null)
    {
      contextManager.stop();
    }
    
    context.setSynchronized(false);
    
    if (commandExecutionService != null)
    {
      commandExecutionService.shutdown();
    }
  }
  
  public void run() throws DisconnectionException, SyntaxErrorException, IOException
  {
    controller.runImpl();
  }
  
  public long getStartMessageCount()
  {
    return controller.getStartMessageCount();
  }
  
  public Long getLastDataTimestamp()
  {
    return controller.getLastDataTimestamp();
  }
  
  public RemoteServer getServer()
  {
    return server;
  }
  
  public AgentContext getContext()
  {
    return context;
  }
  
  public boolean isConnected()
  {
    return controller != null && controller.isConnected();
  }
  
  public int getPendingEventsCount()
  {
    if (controller == null)
      return 0;
    
    return controller.getPendingEventsCount();
  }
  
  private class AgentContextManager extends DefaultContextManager<Context>
  {
    public AgentContextManager(Context rootContext, boolean async)
    {
      super(rootContext, async);
    }
    
    @Override
    public void eventAdded(Context con, EventDefinition ed)
    {
      super.eventAdded(con, ed);
      
      if (ed.getGroup() != null && controller != null)
      {
        con.addEventListener(ed.getName(), controller.getDefaultEventListener(), true);
      }
    }
    
  }
}
