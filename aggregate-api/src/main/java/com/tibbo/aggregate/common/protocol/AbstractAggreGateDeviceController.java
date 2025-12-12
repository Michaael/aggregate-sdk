package com.tibbo.aggregate.common.protocol;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.ProtocolHandler;
import com.tibbo.aggregate.common.communication.AbstractDeviceController;
import com.tibbo.aggregate.common.communication.CommandParser;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextSecurityException;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.AbstractDataTable;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.ProxyDataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.encoding.RemoteFormatCache;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.device.RemoteDeviceErrorException;
import com.tibbo.aggregate.common.event.FireEventRequestController;
import com.tibbo.aggregate.common.server.ServerContextConstants;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.ElementList;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.UserSettings;

public abstract class AbstractAggreGateDeviceController<D extends AggreGateDevice, C extends RemoteContextManager> extends AbstractDeviceController<IncomingAggreGateCommand, OutgoingAggreGateCommand>
    implements AggreGateDeviceController<IncomingAggreGateCommand, OutgoingAggreGateCommand>
{
  public static final String FLAG_NO_REPLY = "N";
  
  private D device;
  
  private C contextManager;
  
  private CallerController callerController;
  
  private final UserSettings userSettings = new UserSettings();
  
  private boolean avoidSendingFormats;
  
  private FormatCache formatCache;
  
  private ThreadPoolExecutor eventPreprocessor;
  
  private final int maxEventQueueLength;
  
  private boolean usesCompression;
  
  protected final CompressedCommandWriter<OutgoingAggreGateCommand> commandWriter = new CompressedCommandWriter<>();
  
  private ProtocolVersion protocolVersion;
  
  private int rejectedEvents = 0;
  
  private final ProtocolCommandBuilder commandBuilder;
  
  private final List<AbstractAggreGateDeviceControllerDisconnectListener> disconnectListeners = new LinkedList<>();

  public AbstractAggreGateDeviceController(D device, Logger logger, int maxEventQueueLength)
  {
    this(device, logger, maxEventQueueLength, false);
  }
  
  public AbstractAggreGateDeviceController(D device, Logger logger, int maxEventQueueLength, boolean json)
  {
    super(device.getCommandTimeout(), logger);
    
    formatCache = new RemoteFormatCache(this, device.toString());
    this.commandBuilder = new ProtocolCommandBuilder(json);
    
    this.device = device;
    this.maxEventQueueLength = maxEventQueueLength;
  }
  
  @Override
  public C getContextManager()
  {
    return contextManager;
  }
  
  public void setContextManager(C contextManager)
  {
    this.contextManager = contextManager;
  }
  
  public void setDevice(D device)
  {
    this.device = device;
  }
  
  @Override
  public D getDevice()
  {
    return device;
  }
  
  @Override
  public CallerController getCallerController()
  {
    return callerController;
  }
  
  protected void setCallerController(CallerController callerController)
  {
    this.callerController = callerController;
  }
  
  public FormatCache getFormatCache()
  {
    return formatCache;
  }
  
  public void setFormatCache(FormatCache cache)
  {
    this.formatCache = cache;
  }

  @Override
  public UserSettings getSettings()
  {
    return userSettings;
  }
  
  public ClassicEncodingSettings createClassicEncodingSettings(boolean forSending)
  {
    ClassicEncodingSettings es = new ClassicEncodingSettings(false);
    es.setProtocolVersion(protocolVersion);
    
    if (!forSending)
    {
      es.setFormatCache(formatCache);
    }
    es.setEncodeFormat(!avoidSendingFormats);
    return es;
  }
  
  protected void setAvoidSendingFormats(boolean avoidSendingFormats)
  {
    this.avoidSendingFormats = avoidSendingFormats;
  }
  
  public boolean isAvoidSendingFormats()
  {
    return avoidSendingFormats;
  }
  
  @Override
  protected boolean connectImpl() throws DisconnectionException, IOException, InterruptedException, RemoteDeviceErrorException, ContextException
  {
    // Set compatibility mode for the first messages
    commandWriter.setVersion(ProtocolVersion.V2);
    
    AggreGateCommandParser aggreGateCommandParser = (AggreGateCommandParser) getCommandParser();
    
    aggreGateCommandParser.setVersion(ProtocolVersion.V2);
    
    boolean lastReplyIsOk = false;
    
    ProtocolVersion[] versions = ProtocolVersion.values();
    IncomingAggreGateCommand ans = null;
    
    // Starting from higher version to lower
    for (int i = versions.length - 1; i >= 0; i--)
    {
      ProtocolVersion version = versions[i];
      ans = sendCommand(commandBuilder.startMessage(version.notation()));
      lastReplyIsOk = ans.getReplyCode().equals(AggreGateCodes.REPLY_CODE_OK);
      
      if (lastReplyIsOk)
        break;
    }
    
    if (!lastReplyIsOk)
      throw new RemoteDeviceErrorException(Cres.get().getString("devUncompatibleVersion"));
    
    if (ans.hasParameter(AggreGateCommand.INDEX_START_PROTOCOL_VERSION))
    {
      String replyVersion = ans.getParameter(AggreGateCommand.INDEX_START_PROTOCOL_VERSION).getString();
      protocolVersion = ProtocolVersion.byNotation(replyVersion);
      
      commandWriter.setVersion(protocolVersion);
      aggreGateCommandParser.setVersion(protocolVersion);
      
      if (ans.hasParameter(AggreGateCommand.INDEX_START_COMPRESSION) && ans.getParameter(AggreGateCommand.INDEX_START_COMPRESSION).charAt(0) == AggreGateCommand.MESSAGE_CODE_COMPRESSION)
      {
        setUsesCompression(true);
      }
    }
    
    formatCache.clear();
    
    return true;
  }
  
  @Override
  public abstract void start() throws IOException, InterruptedException, ContextException, RemoteDeviceErrorException;
  
  public void destroy()
  {
    
  }
  
  public void addDisconnectListener(AbstractAggreGateDeviceControllerDisconnectListener disconnectListener)
  {
    if (disconnectListener != null)
      disconnectListeners.add(disconnectListener);
  }

  @Override
  protected void disconnectImpl()
  {
    if (contextManager != null)
    {
      contextManager.stop();
    }
    
    if (eventPreprocessor != null)
    {
      eventPreprocessor.shutdown();
    }

    formatCache.clear();

    disconnectListeners.forEach(AbstractAggreGateDeviceControllerDisconnectListener::disconnected);
    disconnectListeners.clear();
  }
  
  protected List<ProxyContext> getProxyContexts(String path)
  {
    // Distributed: internal distributed architecture call
    ProxyContext con = getContextManager().get(path);
    return con != null ? Collections.singletonList(con) : Collections.emptyList();
  }

  protected ExecutorService getEventPreprocessor()
  {
    return eventPreprocessor;
  }

  public IncomingAggreGateCommand sendCommandAndCheckReplyCode(OutgoingAggreGateCommand cmd) throws DisconnectionException, ContextException, IOException, InterruptedException,
      RemoteDeviceErrorException
  {
    IncomingAggreGateCommand ans = sendCommand(cmd);
    
    if (ans == null)
      return null;
    
    if (ans.getReplyCode().equals(AggreGateCodes.REPLY_CODE_DENIED))
    {
      String message = ans.getNumberOfParameters() > AggreGateCommand.INDEX_REPLY_MESSAGE ? ": " + TransferEncodingHelper.decode(ans.getParameter(AggreGateCommand.INDEX_REPLY_MESSAGE)) : "";
      throw new ContextSecurityException(Cres.get().getString("devAccessDeniedReply") + message);
    }
    
    if (ans.getReplyCode().equals(AggreGateCodes.REPLY_CODE_PASSWORD_EXPIRED))
    {
      String message = ans.getNumberOfParameters() > AggreGateCommand.INDEX_REPLY_MESSAGE ? ": " + TransferEncodingHelper.decode(ans.getParameter(AggreGateCommand.INDEX_REPLY_MESSAGE)) : "";
      String details = ans.getNumberOfParameters() > AggreGateCommand.INDEX_REPLY_DETAILS ? TransferEncodingHelper.decode(ans.getParameter(AggreGateCommand.INDEX_REPLY_DETAILS)) : null;
      RemoteDeviceErrorException rdex = new RemoteDeviceErrorException(Cres.get().getString("devServerReturnedError") + message, details);
      rdex.setCode(AggreGateCodes.REPLY_CODE_PASSWORD_EXPIRED);
      throw rdex;
    }
    
    if (!ans.getReplyCode().equals(AggreGateCodes.REPLY_CODE_OK))
    {
      String message = ans.getNumberOfParameters() > AggreGateCommand.INDEX_REPLY_MESSAGE ? ": " + TransferEncodingHelper.decode(ans.getParameter(AggreGateCommand.INDEX_REPLY_MESSAGE)) : "";
      String details = ans.getNumberOfParameters() > AggreGateCommand.INDEX_REPLY_DETAILS ? TransferEncodingHelper.decode(ans.getParameter(AggreGateCommand.INDEX_REPLY_DETAILS)) : null;
      RemoteDeviceErrorException ex = new RemoteDeviceErrorException(Cres.get().getString("devServerReturnedError") + message + " (error code: '" + ans.getReplyCode() + "')", details);
      ex.setCode(ans.getReplyCode());
      throw ex;
    }
    
    return ans;
  }
  
  @Override
  protected void processAsyncCommand(final IncomingAggreGateCommand cmd)
  {
    if (Log.COMMANDS.isDebugEnabled())
    {
      Log.COMMANDS.debug("Async command received from server: " + cmd);
    }
    
    if (cmd.getMessageCode().charAt(0) == AggreGateCommand.MESSAGE_CODE_EVENT)
    {
      processEvent(cmd);
    }
  }
  
  private void processEvent(final IncomingAggreGateCommand cmd)
  {
    if (eventPreprocessor == null || eventPreprocessor.isShutdown())
    {
      return;
    }
    
    Runnable task = new Runnable()
    {
      @Override
      public void run()
      {
        if (!isConnected())
        {
          return;
        }
        
        try
        {
          String contextPath = cmd.getParameter(AggreGateCommand.INDEX_EVENT_CONTEXT).getString();
          String eventName = cmd.getParameter(AggreGateCommand.INDEX_EVENT_NAME).getString();
          
          int level = Integer.valueOf(cmd.getParameter(AggreGateCommand.INDEX_EVENT_LEVEL).getString());
          
          String idstr = cmd.getParameter(AggreGateCommand.INDEX_EVENT_ID).getString();
          Long id = idstr.length() > 0 ? Long.valueOf(idstr) : null;
          
          String listenerstr = cmd.getParameter(AggreGateCommand.INDEX_EVENT_LISTENER).getString();
          Integer listener = listenerstr.length() > 0 ? Integer.valueOf(listenerstr) : null;
          
          List<ProxyContext> contexts = getProxyContexts(contextPath);
          
          if (contexts.size() == 0)
          {
            Log.CONTEXT_EVENTS.info("Error firing event '" + eventName + "': context '" + contextPath + "' not found");
            return;
          }
          
          for (ProxyContext con : contexts)
          {
            EventDefinition ed = con.getEventDefinition(eventName);
            
            if (ed == null)
            {
              Log.CONTEXT_EVENTS.warn("Error firing event: event '" + eventName + "' not available in context '" + contextPath + "'");
              continue;
            }
            
            DataTable data = decodeRemoteDataTable(ed.getFormat(), cmd.getEncodedDataTableFromEventMessage());
            
            Date timestamp = null;
            if (cmd.hasParameter(AggreGateCommand.INDEX_EVENT_TIMESTAMP))
            {
              String timestampstr = cmd.getParameter(AggreGateCommand.INDEX_EVENT_TIMESTAMP).getString();
              timestamp = timestampstr.length() > 0 ? new Date(Long.valueOf(timestampstr)) : null;
            }
            
            FireEventRequestController requestController = new FireEventRequestController(false);
            if (cmd.hasParameter(AggreGateCommand.INDEX_EVENT_SERVER_ID))
            {
              requestController.setServerId(cmd.getParameter(AggreGateCommand.INDEX_EVENT_SERVER_ID).getString());
            }
            Event event = con.fireEvent(ed.getName(), data, level, id, timestamp, listener, null, requestController);
            
            confirmEvent(con, ed, event);
          }
        }
        catch (Exception ex)
        {
          if (getLogger() != null)
          {
            String msg = "Error processing async command '" + cmd + "'";
            if (ExceptionUtils.getRootCause(ex) instanceof DisconnectionException)
            {
              getLogger().debug(msg, ex);
            }
            else
            {
              getLogger().error(msg, ex);
            }
          }
        }
      }
    };
    
    /* Using async preprocessor is necessary, since event's data table may be based on a table format that may be received in one of the later commands */
    try
    {
      eventPreprocessor.execute(task);
    }
    catch (RejectedExecutionException ex)
    {
      rejectedEvents++;
      getLogger().warn("Error processing asynchronous incoming command since the queue is full. Corresponding event rejected. Total rejected events: " + rejectedEvents + ". Command: " + cmd);
    }
  }
  
  protected void confirmEvent(Context con, EventDefinition def, Event event) throws ContextException
  {
  }
  
  @Override
  protected void setCommandParser(CommandParser commandBuffer)
  {
    super.setCommandParser(commandBuffer);
    
    eventPreprocessor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(
        maxEventQueueLength), new ThreadFactory()
    {
      @Override
      public Thread newThread(Runnable r)
      {
        return new Thread(r, "EventPreprocessor/" + getDevice());
      }
    }, new ThreadPoolExecutor.AbortPolicy());
    eventPreprocessor.allowCoreThreadTimeOut(true);
  }
  
  @Override
  public String toString()
  {
    return getDevice().toString();
  }
  
  public DataTable callRemoteFunction(String context, String name, TableFormat outputFormat, DataTable parameters, String queueName) throws ContextException
  {
    return callRemoteFunction(context, name, outputFormat, parameters, queueName, true);
  }
  
  public DataTable callRemoteFunction(String context, String name, TableFormat outputFormat, DataTable parameters, String queueName, boolean isReplyRequired) throws ContextException
  {
    try
    {
      final StringBuilder encodedParameters = parameters.encode(new StringBuilder(), createClassicEncodingSettings(true), false, 0);
      Boolean isShallowDataReleased = releaseShallowData(parameters);
      String flags = !isReplyRequired ? FLAG_NO_REPLY : null;
      OutgoingAggreGateCommand cmd = commandBuilder.callFunctionOperation(context, name, encodedParameters.toString(), queueName, flags);
      
      cmd.setAsync(!isReplyRequired);
      
      if (isShallowDataReleased)
        cmd.setTimeout(ProxyContext.DURABLE_OPERATIONS_TIMEOUT);
      IncomingAggreGateCommand ans = sendCommandAndCheckReplyCode(cmd);
      return ans != null ? decodeRemoteDataTable(outputFormat, ans.getEncodedDataTableFromReply()) : new SimpleDataTable(outputFormat);
    }
    catch (ContextException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      ContextException ce = new ContextException(ex.getMessage(), ex);
      if (ex instanceof AggreGateException)
      {
        ce.setCode(((AggreGateException) ex).getCode());
      }
      throw ce;
    }
  }
  
  private Boolean releaseShallowData(DataTable parameters)
  {
    try
    {
      if (parameters.getRecordCount() == 0)
        return false;
      
      if (!parameters.rec().hasField(ServerContextConstants.FIF_STEP_ACTION_ACTION_RESPONSE))
        return false;
      
      final DataTable actionResponse = parameters.rec().getDataTable(ServerContextConstants.FIF_STEP_ACTION_ACTION_RESPONSE);
      
      if (actionResponse == null || actionResponse.getRecordCount() < 1)
        return false;
      
      final DataTable actionParameters = actionResponse.rec().getDataTable(ProtocolHandler.FIELD_ACTION_RESPONSE_PARAMETERS);
      
      if (actionParameters == null || actionParameters.getRecordCount() < 1)
        return false;
      
      final DataRecord params = actionParameters.rec();
      
      Boolean hasShallow = false;
      for (int i = 0; i < params.getFieldCount(); i++)
      {
        if (params.getFormat().getField(i).isShallow())
        {
          if (params.getFormat().getField(i).getType() == FieldFormat.DATA_FIELD)
          {
            params.getData(i).releaseData();
            hasShallow = true;
          }
        }
      }
      return hasShallow;
    }
    catch (Exception ex)
    {
      Log.COMMANDS.debug(ex.getMessage(), ex);
    }
    return false;
  }
  
  protected DataTable decodeRemoteDataTable(TableFormat format, String encodedReply) throws DataTableException
  {
    if (isAvoidSendingFormats())
    {
      final ClassicEncodingSettings settings = new ClassicEncodingSettings(false, format);
      settings.setProtocolVersion(protocolVersion);
      settings.setFormatCache(formatCache);
      
      return choseAppropriateDataTable(encodedReply, settings, true);
    }
    
    try
    {
      return choseAppropriateDataTable(encodedReply, createClassicEncodingSettings(false), false);
    }
    catch (Exception ex)
    {
      throw new DataTableException("Error parsing encoded data table '" + encodedReply + "': " + ex.getMessage(), ex);
    }
  }
  
  private DataTable choseAppropriateDataTable(String encodedReply, ClassicEncodingSettings settings, boolean validate) throws DataTableException
  {
    final ElementList elements = StringUtils.elements(encodedReply, settings.isUseVisibleSeparators());
    final boolean containsID = elements.stream().map(Element::getName).anyMatch(AbstractDataTable.ELEMENT_ID::equals);
    return containsID ? new ProxyDataTable(elements, settings, validate, this) : new SimpleDataTable(elements, settings, validate);
  }
  
  public boolean isUsesCompression()
  {
    return usesCompression;
  }
  
  public void setUsesCompression(boolean usesCompression)
  {
    this.usesCompression = usesCompression;
  }
  
  public ProtocolVersion getProtocolVersion()
  {
    return protocolVersion;
  }
  
  public int getEventQueueLength()
  {
    return eventPreprocessor != null ? eventPreprocessor.getQueue().size() : 0;
  }
  
  public int getRejectedEvents()
  {
    return rejectedEvents;
  }
  
  public ProtocolCommandBuilder getCommandBuilder()
  {
    return commandBuilder;
  }

  public boolean isPropagateUnifiedSearchRequests()
  {
    return false;
  }
}
