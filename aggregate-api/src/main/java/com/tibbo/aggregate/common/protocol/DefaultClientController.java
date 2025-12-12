package com.tibbo.aggregate.common.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.SoftwareVersion;
import com.tibbo.aggregate.common.communication.CommandParserListener;
import com.tibbo.aggregate.common.communication.CommandWriter;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.CallerData;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.DefaultContextEventListener;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableRegistry;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.event.EventHandlingException;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.server.RootContextConstants;
import com.tibbo.aggregate.common.util.BlockingChannel;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.StringWrapper;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public abstract class DefaultClientController<T extends CallerData> extends AbstractClientController<T> implements CommandParserListener
{
  public static final int KEEP_ALIVE_PERIOD = 10000; // Ms
  private static final int KEEP_ALIVE_MULTIPLIER = 10;
  protected final boolean json;
  private final BlockingChannel dataChannel;
  private final AggreGateCommandParser commandParser;
  private final CommandWriter<OutgoingAggreGateCommand> commandWriter;
  private final ContextEventListener defaultEventListener;
  
  private final ExecutorService commandExecutionService;
  
  private final CommandQueueManager queueManager;
  
  private final boolean useCompression;
  
  private final BlockingQueue<OutgoingAggreGateCommand> pendingEventCommandsQueue;
  private final DataTableRegistry globalDataTableRegistry;
  protected long discardedEventsCount;
  protected KnownFormatCollector knownFormatCollector;
  private FormatCache formatCache;
  private boolean shutDown = false;
  private Future pendingEventProcessingTask;
  private long startMessageCount;
  private Long lastDataTimestamp;
  private ProtocolVersion protocolVersion;
  private boolean keepAliveDisabled;
  
  public DefaultClientController(BlockingChannel dataChannel, ContextManager contextManager, ExecutorService commandExecutionService, FormatCache formatCache,
      DataTableRegistry globalDataTableRegistry, int pendingEventsQueueCapacity)
  {
    this(dataChannel, contextManager, commandExecutionService, formatCache, globalDataTableRegistry, pendingEventsQueueCapacity, false, false);
  }
  
  public DefaultClientController(BlockingChannel dataChannel, ContextManager contextManager, ExecutorService commandExecutionService, FormatCache formatCache,
      DataTableRegistry globalDataTableRegistry, int pendingEventsQueueCapacity, boolean json, boolean keepAliveDisabled)
  {
    super(contextManager);
    
    this.defaultEventListener = new ForwardingEventListener(contextManager, null, null, null);
    
    this.dataChannel = dataChannel;
    this.commandExecutionService = commandExecutionService;
    this.formatCache = formatCache;
    this.globalDataTableRegistry = globalDataTableRegistry;
    this.useCompression = dataChannel.isUsesCompression();
    this.json = json;

    this.keepAliveDisabled = keepAliveDisabled;
    
    if (useCompression)
    {
      dataChannel.setUsesCompression(false); // never compress start message
    }
    
    // Start parser for version 2 protocol. Will switch to 3 after start message.
    commandParser = new AggreGateCommandParser(dataChannel);
    commandWriter = new CompressedCommandWriter<>();
    
    commandParser.setListener(this);
    
    queueManager = new CommandQueueManager(commandExecutionService);
    
    pendingEventCommandsQueue = new LinkedBlockingQueue(pendingEventsQueueCapacity);
    
    knownFormatCollector = new KnownFormatCollector();
  }
  
  public static String searchForCode(Throwable ex)
  {
    if (ex instanceof AggreGateException)
    {
      AggreGateException aex = (AggreGateException) ex;
      if (aex.getCode() != null)
      {
        return aex.getCode();
      }
    }
    
    return ex.getCause() != null ? searchForCode(ex.getCause()) : AggreGateCodes.REPLY_CODE_ERROR;
  }
  
  public void setKnownFormatCollector(KnownFormatCollector knownFormatCollector)
  {
    this.knownFormatCollector = knownFormatCollector;
  }
  
  public void processOperationGetVar(String id, Context con, String name, OutgoingAggreGateCommand ans) throws ContextException
  {
    DataTable result;
    
    if (Log.CLIENTS.isDebugEnabled())
    {
      Log.CLIENTS.debug("Getting variable '" + name + "' from context '" + con.getPath() + "'");
    }
    
    VariableDefinition vd = getVariableDefinition(con, name);
    
    if (vd == null)
    {
      ans.constructReply(id, AggreGateCodes.REPLY_CODE_DENIED, Cres.get().getString("conVarNotAvail") + name);
      return;
    }
    
    result = getVariable(con, name);
    
    if (!result.isSimple())
    {
      final long tableId = result.getId() != null ? result.getId() : globalDataTableRegistry.add(result);
      final DataTable previousValue = getCallerController().getCallerData().addToLocalRegistry(tableId, result); // as long as a table is stored at least in one local session, the GC will not remove
                                                                                                                 // the corresponding table from the global data table registry
      if (previousValue != null)
        Log.CLIENTS.warn("Data table '" + previousValue + "' with id " + tableId + " was replaced with data table '"
            + result + "' in the local registry of caller controller: " + getCallerController());
    }
    
    ans.constructReply(id, AggreGateCodes.REPLY_CODE_OK);
    
    ans.addParam(result.encode(createClassicEncodingSettings(vd.getFormat() != null)));
  }
  
  protected DataTable getVariable(Context con, String name) throws ContextException
  {
    return con.getVariable(name, getCallerController());
  }
  
  public void processOperationSetVar(String id, Context con, String name, String encodedValue, OutgoingAggreGateCommand ans) throws ContextException
  {
    if (Log.CLIENTS.isDebugEnabled())
    {
      Log.CLIENTS.debug("Setting variable '" + name + "' of context '" + con.getPath() + "'");
    }
    
    VariableDefinition vd = getVariableDefinition(con, name);
    
    if (vd == null)
    {
      ans.constructReply(id, AggreGateCodes.REPLY_CODE_DENIED, Cres.get().getString("conVarNotAvail") + name);
      return;
    }
    
    ClassicEncodingSettings settings = new ClassicEncodingSettings(false, vd.getFormat());
    settings.setProtocolVersion(protocolVersion);
    
    DataTable value = new SimpleDataTable(encodedValue, settings, true);
    
    setVariable(con, name, value);
    
    ans.constructReply(id, AggreGateCodes.REPLY_CODE_OK);
  }
  
  protected VariableDefinition getVariableDefinition(Context con, String name)
  {
    return con.getVariableDefinition(name);
  }
  
  protected void setVariable(Context con, String name, DataTable value) throws ContextException
  {
    con.setVariable(name, getCallerController(), value);
  }
  
  public OutgoingAggreGateCommand processOperationCallFunction(String id, Context con, String name, String encodedParameters, String flags, OutgoingAggreGateCommand ans) throws ContextException
  {
    try
    {
      DataTable result;
      
      if (Log.CLIENTS.isDebugEnabled())
      {
        Log.CLIENTS.debug("Calling function '" + name + "' of context '" + con.getPath() + "'");
      }
      
      FunctionDefinition fd = getFunctionDefinition(con, name);
      
      if (fd == null)
      {
        ans.constructReply(id, AggreGateCodes.REPLY_CODE_DENIED, Cres.get().getString("conFuncNotAvail") + name);
        return ans;
      }
      
      ClassicEncodingSettings settings = new ClassicEncodingSettings(false, fd.getInputFormat());
      settings.setProtocolVersion(protocolVersion);
      
      DataTable parameters = new SimpleDataTable(encodedParameters, settings, true);
      
      result = callFunction(con, name, parameters);
      
      if (AbstractAggreGateDeviceController.FLAG_NO_REPLY.equals(flags))
      {
        return null;
      }
      
      ans.constructReply(id, AggreGateCodes.REPLY_CODE_OK);
      return ans.addParam(result.encode(createClassicEncodingSettings(fd.getOutputFormat() != null)));
    }
    catch (OutOfMemoryError ex)
    {
      Log.COMMANDS.warn(ex.getMessage(), ex);
      
      if (AbstractAggreGateDeviceController.FLAG_NO_REPLY.equals(flags))
      {
        return null;
      }
      return ans;
    }
    
  }
  
  protected FunctionDefinition getFunctionDefinition(Context con, String name)
  {
    return con.getFunctionDefinition(name);
  }
  
  protected DataTable callFunction(Context con, String name, DataTable parameters) throws ContextException
  {
    return con.callFunction(name, getCallerController(), parameters);
  }
  
  protected boolean addNormalListener(String context, String name, ContextEventListener cel)
  {
    // Distributed: ok, because remote events will be redirected to this server
    Context con = getContext(context);
    if (con != null)
    {
      return con.addEventListener(name, cel, true);
    }
    else
    {
      return false;
    }
  }
  
  public void processOperationAddEventListener(String id, String context, String name, Integer listener, String filter, String fingerprint, OutgoingAggreGateCommand ans)
  {
    if (Log.CLIENTS.isDebugEnabled())
    {
      Log.CLIENTS.debug("Adding listener for event '" + name + "' of context '" + context + "'");
    }
    
    ContextEventListener cel = createListener(listener, filter != null ? new Expression(filter) : null, fingerprint);
    
    addMaskListener(context, name, cel, true);
    
    ans.constructReply(id, AggreGateCodes.REPLY_CODE_OK);
  }
  
  public void processOperationRemoveEventListener(String id, String context, String name, Integer listenerHashCode, String filter, String fingerprint, OutgoingAggreGateCommand ans)
  {
    if (Log.CLIENTS.isDebugEnabled())
    {
      Log.CLIENTS.debug("Removing listener for event '" + name + "' of context '" + context + "'");
    }
    ContextEventListener cel = createListener(listenerHashCode, filter != null ? new Expression(filter) : null, fingerprint);
    removeMaskListener(context, name, cel);
    ans.constructReply(id, AggreGateCodes.REPLY_CODE_OK);
  }
  
  public OutgoingAggreGateCommand processMessageStart(IncomingAggreGateCommand cmd, OutgoingAggreGateCommand ans)
  {
    final String receivedVersion = cmd.getParameter(AggreGateCommand.INDEX_PROTOCOL_VERSION).getString();
    protocolVersion = StringUtils.isEmpty(receivedVersion) ? ProtocolVersion.V2 : ProtocolVersion.byNotation(receivedVersion);
    
    Log.CLIENTS.debug("Processing start command, client protocol version: " + protocolVersion + "', compression '" + Boolean.toString(useCompression) + "'");
    
    if (protocolVersion == null)
    {
      ans.constructReply(cmd.getId(), AggreGateCodes.REPLY_CODE_DENIED);
      Log.CLIENTS.debug("Rejecting client connection with unsupported version '" + protocolVersion + "'");
      return ans;
    }
    
    ans.constructReply(cmd.getId(), AggreGateCodes.REPLY_CODE_OK); // INDEX = 0,1,2
    ans.addParam(protocolVersion.notation()); // INDEX_START_PROTOCOL_VERSION = 3
    
    if (useCompression)
    {
      ans.addParam(String.valueOf(AggreGateCommand.MESSAGE_CODE_COMPRESSION)); // INDEX_START_COMPRESSION = 4
    }
    
    commandParser.setVersion(protocolVersion);
    commandWriter.setVersionAfterNextWrite(protocolVersion);
    
    startMessageCount++;
    return ans;
  }
  
  protected OutgoingAggreGateCommand processMessageOperation(IncomingAggreGateCommand cmd, OutgoingAggreGateCommand ans) throws SyntaxErrorException, ContextException
  {
    String operation = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_CODE).getString();
    String context = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_CONTEXT).getString();
    String target = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_TARGET).getString();
    
    String originalThreadName = Thread.currentThread().getName();
    Thread.currentThread().setName(originalThreadName + ": " + operation + "/" + context + "/" + target);
    try
    {
      if (operation.length() > 1)
      {
        throw new SyntaxErrorException(Cres.get().getString("clInvalidOpcode") + operation);
      }
      
      if (Log.CLIENTS.isDebugEnabled())
      {
        Log.CLIENTS.debug("Processing message, context '" + context + "', target '" + target + "', operation '" + operation + "'");
      }
      
      switch (operation.charAt(0))
      {
        case AggreGateCommand.COMMAND_OPERATION_ADD_EVENT_LISTENER:
          String listenerStr = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_LISTENER_CODE).getString();
          Integer listener = listenerStr.length() > 0 ? new Integer(listenerStr) : null;
          String filter = cmd.hasParameter(AggreGateCommand.INDEX_OPERATION_FILTER) ? TransferEncodingHelper.decode(cmd.getParameter(AggreGateCommand.INDEX_OPERATION_FILTER)) : null;
          String fingerprint = cmd.hasParameter(AggreGateCommand.INDEX_OPERATION_FINGERPRINT) ? TransferEncodingHelper.decode(cmd.getParameter(AggreGateCommand.INDEX_OPERATION_FINGERPRINT)) : null;
          processOperationAddEventListener(cmd.getId(), context, target, listener, filter, fingerprint, ans);
          return ans;
        
        case AggreGateCommand.COMMAND_OPERATION_REMOVE_EVENT_LISTENER:
          listenerStr = cmd.getParameter(AggreGateCommand.INDEX_OPERATION_LISTENER_CODE).getString();
          listener = listenerStr.length() > 0 ? new Integer(listenerStr) : null;
          filter = cmd.hasParameter(AggreGateCommand.INDEX_OPERATION_FILTER) ? TransferEncodingHelper.decode(cmd.getParameter(AggreGateCommand.INDEX_OPERATION_FILTER)) : null;
          fingerprint = cmd.hasParameter(AggreGateCommand.INDEX_OPERATION_FINGERPRINT) ? TransferEncodingHelper.decode(cmd.getParameter(AggreGateCommand.INDEX_OPERATION_FINGERPRINT)) : null;
          processOperationRemoveEventListener(cmd.getId(), context, target, listener, filter, fingerprint, ans);
          return ans;
      }
      
      Context con = getContext(context);
      if (con == null)
      {
        throw new ContextException(Cres.get().getString("conNotAvail") + context);
      }
      
      if (addNormalListener(con.getPath(), AbstractContext.E_DESTROYED, defaultEventListener))
      {
        addNormalListener(con.getPath(), AbstractContext.E_CHILD_ADDED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_CHILD_REMOVED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_VARIABLE_ADDED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_VARIABLE_REMOVED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_FUNCTION_ADDED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_FUNCTION_REMOVED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_EVENT_ADDED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_EVENT_REMOVED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_INFO_CHANGED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_ACTION_ADDED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_ACTION_REMOVED, defaultEventListener);
        addNormalListener(con.getPath(), AbstractContext.E_ACTION_STATE_CHANGED, defaultEventListener);
        
        addCustomListeners(con);
      }
      
      switch (operation.charAt(0))
      {
        case AggreGateCommand.COMMAND_OPERATION_GET_VAR:
          processOperationGetVar(cmd.getId(), con, target, ans);
          break;
        
        case AggreGateCommand.COMMAND_OPERATION_SET_VAR:
          processOperationSetVar(cmd.getId(), con, target, cmd.getEncodedDataTableFromOperationMessage(), ans);
          break;
        
        case AggreGateCommand.COMMAND_OPERATION_CALL_FUNCTION:
          ans = processOperationCallFunction(cmd.getId(), con, target, cmd.getEncodedDataTableFromOperationMessage(), cmd.getFlags(), ans);
          break;
        
        default:
          throw new SyntaxErrorException(Cres.get().getString("clInvalidOpcode") + operation.charAt(0));
      }
    }
    finally
    {
      Thread.currentThread().setName(originalThreadName);
    }
    return ans;
  }
  
  protected void addCustomListeners(Context con)
  {
  }
  
  protected OutgoingAggreGateCommand processMessage(IncomingAggreGateCommand cmd, OutgoingAggreGateCommand ans) throws SyntaxErrorException, ContextException
  {
    StringWrapper messageCode = cmd.getMessageCode();
    
    if (messageCode.length() > 1)
    {
      throw new SyntaxErrorException(Cres.get().getString("clInvalidMsgCode") + messageCode);
    }
    
    char code = messageCode.charAt(0);
    
    if ((code != AggreGateCommand.MESSAGE_CODE_START) && startMessageCount == 0)
    {
      Log.CLIENTS.debug("Can't process message: start message was not received yet");
      ans.constructReply(cmd.getId(), AggreGateCodes.REPLY_CODE_DENIED);
      return ans;
    }
    
    switch (code)
    {
      case AggreGateCommand.MESSAGE_CODE_START:
        ans = processMessageStart(cmd, ans);
        break;
      
      case AggreGateCommand.MESSAGE_CODE_OPERATION:
        ans = processMessageOperation(cmd, ans);
        break;
      
      default:
        throw new SyntaxErrorException(Cres.get().getString("clInvalidMsgCode") + messageCode.charAt(0));
    }
    return ans;
  }
  
  public OutgoingAggreGateCommand processCommand(IncomingAggreGateCommand cmd)
  {
    OutgoingAggreGateCommand ans;
    
    try
    {
      StringWrapper commandCode = cmd.getParameter(AggreGateCommand.INDEX_COMMAND_CODE);
      
      if (commandCode.length() > 1)
      {
        throw new AggreGateException(Cres.get().getString("clInvalidCmdCode") + commandCode);
      }
      
      switch (commandCode.charAt(0))
      {
        case AggreGateCommand.COMMAND_CODE_MESSAGE:
          ans = processMessage(cmd, ProtocolCommandBuilder.createCommand(json));
          break;
        
        default:
          throw new AggreGateException(Cres.get().getString("clInvalidCmdCode") + commandCode.charAt(0));
      }
    }
    catch (Throwable ex)
    {
      OutgoingAggreGateCommand err = ProtocolCommandBuilder.createCommand(json);
      
      if (Log.CLIENTS.isDebugEnabled())
      {
        Log.CLIENTS.debug("Error processing command '" + AggreGateCommand.checkCommandString(cmd.toString())
            + "': ", ex);
      }
      
      String code = searchForCode(ex);
      
      err.constructReply(cmd.getId(), code, ex.getMessage() != null ? ex.getMessage() : ex.toString(), getErrorDetails(ex));
      
      return err;
    }
    
    return ans;
  }
  
  @Override
  public void shutdown()
  {
    try
    {
      Log.CLIENTS.debug("Shutting down client controller");
      
      Future task = pendingEventProcessingTask;
      if (task != null)
      {
        task.cancel(true);
      }
      
      shutDown = true;
      
      if (dataChannel != null)
      {
        dataChannel.close();
      }
      
      logoutUser();
      super.shutdown();
    }
    catch (Exception ex)
    {
      Log.CLIENTS.warn("Error shutting down client controller: ", ex);
    }
  }
  
  protected void logoutUser() throws ContextException
  {
    if (getContextManager().getRoot().getFunctionDefinition(RootContextConstants.F_LOGOUT) != null)
    {
      getContextManager().getRoot().callFunction(RootContextConstants.F_LOGOUT, getCallerController());
    }
  }
  
  @Override
  public boolean run()
  {
    try
    {
      runImpl();
      
      // Unified Operations Console uses start messages as keepalive messages, sending them periodically
      // Thus, if there were more than one start message, we're analyzing time of last data sample
      // and assuming that we're disconnected if no data was received for a while
      // Data we receive can be any message data, including start message or any other message
      if (!keepAliveDisabled && startMessageCount > 1 && lastDataTimestamp != null)
      {
        if (System.currentTimeMillis() - lastDataTimestamp > KEEP_ALIVE_PERIOD * KEEP_ALIVE_MULTIPLIER)
        {
          throw new DisconnectionException("No data or keepalive messages");
        }
      }
      
      dataChannel.flush();
      
      return true;
    }
    catch (IOException ex)
    {
      String msg = "I/O error while communicating with client";
      Log.CLIENTS.debug(msg, ex);
      Log.CLIENTS.info(msg + ": " + ex);
      return false;
    }
    catch (DisconnectionException ex)
    {
      Log.CLIENTS.info("Client disconnected: " + ex.getMessage());
      
      final CallerController currentCallerController = getCallerController();
      if (currentCallerController != null)
        currentCallerController.unlockAllContexts();
      
      return false;
    }
    catch (Exception ex)
    {
      Log.CLIENTS.warn("Error in client controller", ex);
      return false;
    }
  }
  
  public void runImpl() throws IOException, DisconnectionException, SyntaxErrorException
  {
    IncomingAggreGateCommand command = commandParser.readCommand();
    
    if (command != null)
    {
      if (Log.COMMANDS_CLIENT.isDebugEnabled())
      {
        Log.COMMANDS_CLIENT.debug("Received: " + command);
      }
      
      try
      {
        final String queue = command.getQueueName();
        
        if (queue != null && !queue.isEmpty())
        {
          final CommandTask task = new ProcessCommandTask(command);
          queueManager.addCommand(queue, task);
        }
        else
        {
          commandExecutionService.submit(new ProcessCommandTask(command));
        }
      }
      catch (RejectedExecutionException ex)
      {
        OutgoingAggreGateCommand reply = ProtocolCommandBuilder.createCommand(json);
        reply.constructReply(command.getId(), AggreGateCodes.REPLY_CODE_ERROR, Cres.get().getString("devServerOverloaded"));
        sendCommand(reply);
      }
    }
  }
  
  protected String getErrorDetails(Throwable error)
  {
    StringBuilder buf = new StringBuilder();
    
    buf.append(Cres.get().getString("version")).append(": ").append(SoftwareVersion.getCurrentVersion()).append("\n");
    buf.append(Cres.get().getString("clServerTime")).append(": ").append(new Date()).append("\n");
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    error.printStackTrace(new PrintStream(baos));
    buf.append(baos.toString());
    
    return buf.toString();
  }
  
  public void sendCommand(OutgoingAggreGateCommand cmd) throws DisconnectionException, IOException
  {
    commandWriter.write(cmd, dataChannel, true);
    
    if (Log.COMMANDS_CLIENT.isDebugEnabled())
    {
      Log.COMMANDS_CLIENT.debug("Sent: " + cmd);
    }
  }
  
  public ContextEventListener createListener(Integer listenerHashCode, Expression filter, String fingerprint)
  {
    if (listenerHashCode == null)
    {
      return defaultEventListener;
    }
    
    return new ForwardingEventListener(getContextManager(), listenerHashCode, filter, fingerprint);
  }
  
  private void processPendingEvents()
  {
    while (!Thread.currentThread().isInterrupted())
    {
      OutgoingAggreGateCommand current;
      
      synchronized (pendingEventCommandsQueue)
      {
        current = pendingEventCommandsQueue.poll();
        
        if (current == null)
        {
          return;
        }
      }
      
      try
      {
        sendCommand(current);
      }
      catch (DisconnectionException ex)
      {
        Log.CLIENTS.debug("Disconnection detected while forwarding event to client: " + ex.getMessage(), ex);
        return;
      }
      catch (Throwable ex)
      {
        String msg = "Exception while forwarding event to client: ";
        Log.CLIENTS.info(msg + ex.toString());
        Log.CLIENTS.debug(msg + ex.getMessage(), ex);
      }
    }
  }
  
  protected Context getContext(String path)
  {
    return getContextManager().get(path, getCallerController());
  }
  
  public abstract boolean controllerShouldHandle(Event ev, ContextEventListener listener) throws EventHandlingException;
  
  public int getPendingEventsCount()
  {
    synchronized (pendingEventCommandsQueue)
    {
      return pendingEventCommandsQueue.size();
    }
  }
  
  public long getDiscardedEventsCount()
  {
    return discardedEventsCount;
  }
  
  protected void discardEvent()
  {
    discardedEventsCount++;
    
    if (Log.CLIENTS.isDebugEnabled())
    {
      Log.CLIENTS.debug("Event queue overflow for client controller: " + DefaultClientController.this.toString());
    }
  }
  
  public ContextEventListener getDefaultEventListener()
  {
    return defaultEventListener;
  }
  
  protected ClassicEncodingSettings createClassicEncodingSettings(boolean useFormatCache)
  {
    ClassicEncodingSettings settings = new ClassicEncodingSettings(false);
    settings.setProtocolVersion(protocolVersion);
    
    if (useFormatCache)
    {
      settings.setFormatCache(formatCache);
      settings.setKnownFormatCollector(knownFormatCollector);
    }
    return settings;
  }
  
  public OutgoingAggreGateCommand constructEventCommand(Event event, Integer listenerCode)
  {
    OutgoingAggreGateCommand cmd = ProtocolCommandBuilder.createCommand(json);
    
    String data = event.getData().encode(createClassicEncodingSettings(true));
    
    cmd.constructEvent(event.getContext(), event.getName(), event.getLevel(), data, event.getId(), event.getCreationtime(), listenerCode);
    
    return cmd;
  }
  
  public void setFormatCache(FormatCache formatCache)
  {
    this.formatCache = formatCache;
  }
  
  public boolean isConnected()
  {
    return dataChannel.isOpen();
  }
  
  public String getAddress()
  {
    return dataChannel != null ? dataChannel.getChannelAddress() : null;
  }
  
  @Override
  public void newDataReceived()
  {
    lastDataTimestamp = System.currentTimeMillis();
  }
  
  public long getStartMessageCount()
  {
    return startMessageCount;
  }
  
  public Long getLastDataTimestamp()
  {
    return lastDataTimestamp;
  }
  
  public boolean isJson()
  {
    return json;
  };
  
  public class ForwardingEventListener extends DefaultContextEventListener
  {
    public ForwardingEventListener(ContextManager contextManager, Integer listenerCode, Expression filter, String fingerprint)
    {
      super(null, contextManager, listenerCode, filter, fingerprint);
      setAcceptEventsWithoutListenerCode(true);
    }
    
    @Override
    public boolean shouldHandle(Event event) throws EventHandlingException
    {
      try
      {
        if (!super.shouldHandle(event))
        {
          return false;
        }
        
        if (!controllerShouldHandle(event, this))
        {
          return false;
        }
        
        if (event.getName().equals(AbstractContext.E_CHILD_ADDED))
        {
          Context con = getContext(event.getContext());
          if (con == null || con.getChild(event.getData().rec().getString(AbstractContext.EF_CHILD_ADDED_CHILD), getCallerController()) == null)
          {
            return false;
          }
        }
        
        return true;
      }
      catch (Exception ex)
      {
        throw new EventHandlingException(ex.getMessage(), ex);
      }
    }
    
    @Override
    public void handle(Event event) throws EventHandlingException
    {
      if (Log.CLIENTS.isDebugEnabled())
      {
        Log.CLIENTS.debug("Handling event '" + event.getName() + "' from context '" + event.getContext() + "' for listener '" + getListenerCode() + "' (" + toString() + ")");
      }
      
      try
      {
        if (event.getName().equals(AbstractContext.E_INFO_CHANGED))
        {
          Context con = getContext(event.getContext());
          
          if (con != null)
          {
            event.getData().rec().setValue(AbstractContext.VF_INFO_LOCAL_ROOT, con.getLocalRoot(true));
            event.getData().rec().setValue(AbstractContext.VF_INFO_PEER_ROOT, con.getPeerRoot());
            event.getData().rec().setValue(AbstractContext.VF_INFO_REMOTE_ROOT, con.getRemoteRoot());
            event.getData().rec().setValue(AbstractContext.VF_INFO_REMOTE_PATH, con.getPeerPath());
            event.getData().rec().setValue(AbstractContext.VF_INFO_PEER_PRIMARY_ROOT, con.getLocalPrimaryRoot());
          }
        }
        
        OutgoingAggreGateCommand cmd = constructEventCommand(event, getListenerCode());
        
        synchronized (pendingEventCommandsQueue)
        {
          boolean added = pendingEventCommandsQueue.offer(cmd);
          
          if (!added)
          {
            discardEvent();
          }
          
          if (pendingEventProcessingTask == null || pendingEventProcessingTask.isDone())
          {
            try
            {
              pendingEventProcessingTask = commandExecutionService.submit(new PendingEventProcessingTask());
            }
            catch (RejectedExecutionException ex)
            {
              Log.CLIENTS.warn("Cannot schedule new event delivery since command processing pool is overloaded");
            }
          }
        }
      }
      catch (Exception ex)
      {
        throw new EventHandlingException(MessageFormat.format(Cres.get().getString("clErrorHandlingEvent"), event.getName()) + ex.getMessage(), ex);
      }
    }
    
    @Override
    public String toString()
    {
      return "ForwardingEventListener: " + getListenerCode();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOwningController().hashCode();
      result = prime * result + ((getListenerCode() == null) ? 0 : getListenerCode().hashCode());
      result = prime * result + ((getFilter() == null) ? 0 : getFilter().hashCode());
      result = prime * result + ((getFingerprint() == null) ? 0 : getFingerprint().hashCode());
      return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      ForwardingEventListener other = (ForwardingEventListener) obj;
      if (getOwningController() != other.getOwningController())
      {
        return false;
      }
      
      if (getListenerCode() == null)
      {
        if (other.getListenerCode() != null)
        {
          return false;
        }
      }
      else if (other.getListenerCode() == null)
      {
        return false;
      }
      if (getListenerCode().intValue() != other.getListenerCode().intValue())
      {
        return false;
      }
      
      if (getFilter() == null)
      {
        if (other.getFilter() != null)
        {
          return false;
        }
      }
      else if (!getFilter().equals(other.getFilter()))
      {
        return false;
      }
      
      if (getFingerprint() == null)
      {
        if (other.getFingerprint() != null)
        {
          return false;
        }
      }
      else if (!getFingerprint().equals(other.getFingerprint()))
      {
        return false;
      }
      
      return true;
    }
    
    private DefaultClientController getOwningController()
    {
      return DefaultClientController.this;
    }
    
    @Override
    public CallerController getCallerController()
    {
      return DefaultClientController.this.getCallerController();
    }
  }
  
  private class ProcessCommandTask implements CommandTask
  {
    private final IncomingAggreGateCommand cmd;
    
    public ProcessCommandTask(IncomingAggreGateCommand cmd)
    {
      this.cmd = cmd;
    }
    
    @Override
    public Object call() throws Exception
    {
      try
      {
        if (!shutDown)
        {
          final OutgoingAggreGateCommand reply = processCommand(cmd);
          
          processPendingEvents(); // If some events were generated by the processed commands, deliver them ASAP
          
          if (reply != null)
          {
            sendCommand(reply);
          }
          
          if (cmd.getMessageCode().charAt(0) == AggreGateCommand.MESSAGE_CODE_START && useCompression != dataChannel.isUsesCompression())
          {
            dataChannel.setUsesCompression(useCompression); // after sending the first message reply switch the compression on if needed
          }
        }
      }
      catch (DisconnectionException ex)
      {
        Log.CLIENTS.debug("Disconnection exception while processing command: " + ex.getMessage(), ex);
      }
      catch (SocketException ex)
      {
        Log.CLIENTS.info("Socket exception while processing command: " + ex.getMessage());
        Log.CLIENTS.debug("Socket exception while processing command: " + ex.getMessage(), ex);
      }
      catch (Exception ex)
      {
        Log.CLIENTS.warn("Error processing command: " + ex.getMessage(), ex);
      }
      return null;
    }
  }
  
  private class PendingEventProcessingTask implements Runnable
  {
    @Override
    public void run()
    {
      processPendingEvents();
    }
  }
}
