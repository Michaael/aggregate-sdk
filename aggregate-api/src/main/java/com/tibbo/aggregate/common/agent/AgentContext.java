package com.tibbo.aggregate.common.agent;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.context.FunctionImplementation;
import com.tibbo.aggregate.common.context.RequestController;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.context.VariableGetter;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.datatable.encoding.KnownFormatCollector;
import com.tibbo.aggregate.common.device.DeviceAssetDefinition;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.server.CommonServerFormats;
import com.tibbo.aggregate.common.server.DeviceContextConstants;
import com.tibbo.aggregate.common.server.RootContextConstants;
import com.tibbo.aggregate.common.util.Md5Utils;

public class AgentContext extends AbstractContext
{
  public static final String V_DATE = "date";
  
  public static final String F_LOGIN = "login";
  public static final String F_EXT_LOGIN = "extLogin";
  public static final String F_REGISTER = "register";
  public static final String F_SYNCHRONIZED = "synchronized";
  public static final String F_CONFIRM_EVENT = "confirmEvent";
  public static final String F_ACKNOWLEDGE_EVENT = "acknowledgeEvent";
  public static final String F_GET_HISTORY = "getHistory";
  public static final String F_SEND_BUFFERED_EVENTS = "sendBufferedEvents";
  
  public static final String E_EVENT_CONFIRMED = "eventConfirmed";
  
  public static final String FIF_LOGIN_CHALLENGE = "challenge";
  
  public static final String FIF_CONFIRM_EVENT_ID = "id";
  
  public static final String FIF_ACKNOWLEDGE_EVENT_ID = "id";
  public static final String FIF_ACKNOWLEDGE_EVENT_DATE = "date";
  public static final String FIF_ACKNOWLEDGE_EVENT_AUTHOR = "author";
  public static final String FIF_ACKNOWLEDGE_EVENT_ACKNOWLEDGEMENT = "acknowledgement";
  public static final String FIF_ACKNOWLEDGE_EVENT_EVENT_DATA = "eventData";
  
  public static final String FOF_LOGIN_OWNER = "owner";
  public static final String FOF_LOGIN_NAME = "name";
  public static final String FOF_LOGIN_RESPONSE = "response";
  
  public static final String FOF_PASSWORD = "password";
  public static final String FOF_COMMAND_TIMEOUT = "commandTimeout";
  
  public static final String FOF_GET_HISTORY_VARIABLE = "variable";
  public static final String FOF_GET_HISTORY_TIMESTAMP = "timestamp";
  public static final String FOF_GET_HISTORY_VALUE = "value";
  
  public static final String FOF_BUFFERED_EVENTS_SENT = "bufferedEventsSent";
  
  public static final String EF_EVENT_CONFIRMED_ID = "id";
  
  public static final TableFormat FIFT_LOGIN = new TableFormat(1, 1, "<" + FIF_LOGIN_CHALLENGE + "><S>");
  
  public static final TableFormat FOFT_LOGIN = new TableFormat(1, 1);
  
  static
  {
    FOFT_LOGIN.addField("<" + FOF_LOGIN_OWNER + "><S>");
    FOFT_LOGIN.addField("<" + FOF_LOGIN_NAME + "><S>");
    FOFT_LOGIN.addField("<" + FOF_LOGIN_RESPONSE + "><S>");
  }
  
  public static final TableFormat FOFT_EXT_LOGIN = new TableFormat(1, 1);
  
  static
  {
    FOFT_EXT_LOGIN.addField("<" + FOF_PASSWORD + "><S>");
  }
  
  public static final TableFormat FOFT_REGISTER = new TableFormat(1, 1);
  
  static
  {
    FOFT_REGISTER.addField("<" + FOF_PASSWORD + "><S>");
    FOFT_REGISTER.addField("<" + FOF_COMMAND_TIMEOUT + "><L>");
  }
  
  public static final TableFormat FOFT_GET_HISTORY = new TableFormat();
  
  static
  {
    FOFT_GET_HISTORY.addField("<" + FOF_GET_HISTORY_VARIABLE + "><S>");
    FOFT_GET_HISTORY.addField("<" + FOF_GET_HISTORY_TIMESTAMP + "><D>");
    FOFT_GET_HISTORY.addField("<" + FOF_GET_HISTORY_VALUE + "><T>");
  }
  
  public static final TableFormat FOFT_SEND_BUFFERED_EVENTS = new TableFormat(1, 1, "<" + FOF_BUFFERED_EVENTS_SENT + "><I>");
  
  public static final TableFormat FIFT_CONFIRM_EVENT = new TableFormat(1, 1, "<" + FIF_CONFIRM_EVENT_ID + "><L>");
  
  public static final TableFormat FIFT_ACKNOWLEDGE_EVENT = new TableFormat(1, 1);
  
  static
  {
    FIFT_ACKNOWLEDGE_EVENT.addField("<" + FIF_ACKNOWLEDGE_EVENT_ID + "><L><F=N>");
    FIFT_ACKNOWLEDGE_EVENT.addField("<" + FIF_ACKNOWLEDGE_EVENT_DATE + "><D>");
    FIFT_ACKNOWLEDGE_EVENT.addField("<" + FIF_ACKNOWLEDGE_EVENT_AUTHOR + "><S><F=N>");
    FIFT_ACKNOWLEDGE_EVENT.addField("<" + FIF_ACKNOWLEDGE_EVENT_ACKNOWLEDGEMENT + "><S>");
    FIFT_ACKNOWLEDGE_EVENT.addField("<" + FIF_ACKNOWLEDGE_EVENT_EVENT_DATA + "><T>");
  }
  
  public static final TableFormat EFT_EVENT_CONFIRMED = new TableFormat(1, 1, "<" + EF_EVENT_CONFIRMED_ID + "><L>");
  
  private final RemoteServer server;
  
  private final String name;
  
  private final boolean eventConfirmation;
  
  private boolean isSynchronized;
  
  private List<DeviceAssetDefinition> assets = new LinkedList();
  
  private FormatCache formatCache;
  
  private KnownFormatCollector knownFormatCollector;
  
  public AgentContext(RemoteServer server, String name, boolean eventConfirmation)
  {
    super(Contexts.CTX_ROOT);
    this.server = server;
    this.name = name;
    this.eventConfirmation = eventConfirmation;
  }
  
  @Override
  public void setupMyself() throws ContextException
  {
    super.setupMyself();
    
    VariableDefinition vd = new VariableDefinition(DeviceContextConstants.V_ASSETS, DeviceAssetDefinition.FORMAT, true, false);
    vd.setGetter(assetReader);
    addVariableDefinition(vd);
    
    FunctionDefinition fd = new FunctionDefinition(F_LOGIN, FIFT_LOGIN, FOFT_LOGIN, Cres.get().getString("login"));
    fd.setImplementation(loginImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_EXT_LOGIN, TableFormat.EMPTY_FORMAT, FOFT_EXT_LOGIN, Cres.get().getString("extLogin"));
    fd.setImplementation(externalLoginImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_REGISTER, TableFormat.EMPTY_FORMAT, FOFT_REGISTER, Cres.get().getString("register"));
    fd.setImplementation(registerImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_SYNCHRONIZED, TableFormat.EMPTY_FORMAT, TableFormat.EMPTY_FORMAT);
    fd.setImplementation(synchronizedImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_CONFIRM_EVENT, FIFT_CONFIRM_EVENT, TableFormat.EMPTY_FORMAT);
    fd.setImplementation(confirmEventImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_ACKNOWLEDGE_EVENT, FIFT_ACKNOWLEDGE_EVENT, TableFormat.EMPTY_FORMAT);
    fd.setImplementation(acknowledgeEventImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_GET_HISTORY, TableFormat.EMPTY_FORMAT, FOFT_GET_HISTORY);
    fd.setImplementation(getHistoryImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(F_SEND_BUFFERED_EVENTS, TableFormat.EMPTY_FORMAT, FOFT_SEND_BUFFERED_EVENTS);
    fd.setImplementation(sendBufferedEventsImpl);
    addFunctionDefinition(fd);
    
    fd = new FunctionDefinition(RootContextConstants.F_GET_FORMAT, CommonServerFormats.FIFT_GET_FORMAT, null); // Important: using dynamic output format to avoid format caching
    fd.setImplementation(getFormatImpl);
    fd.setConcurrent(true);
    addFunctionDefinition(fd);
    
    if (eventConfirmation)
    {
      EventDefinition ed = new EventDefinition(E_EVENT_CONFIRMED, EFT_EVENT_CONFIRMED);
      addEventDefinition(ed);
    }
  }
  
  public void addAsset(DeviceAssetDefinition asset)
  {
    assets.add(asset);
  }
  
  public List<DeviceAssetDefinition> getAssets()
  {
    return assets;
  }
  
  public void setAssets(List<DeviceAssetDefinition> assets)
  {
    this.assets = assets;
  }
  
  protected void confirmEvent(Long id)
  {
    if (getEventDefinition(E_EVENT_CONFIRMED) != null)
    {
      fireEvent(E_EVENT_CONFIRMED, id);
    }
  }
  
  protected void acknowledgeEvent(Long id, Date date, String author, String acknowledgement, DataTable data)
  {
    // Do nothing by default
  }
  
  public boolean isSynchronized()
  {
    return isSynchronized;
  }
  
  public void setSynchronized(boolean isSynchronized)
  {
    this.isSynchronized = isSynchronized;
  }
  
  public RemoteServer getServer()
  {
    return server;
  }
  
  public void setFormatCache(FormatCache formatCache)
  {
    this.formatCache = formatCache;
  }
  
  public void setKnownFormatCollector(KnownFormatCollector knownFormatCollector)
  {
    this.knownFormatCollector = knownFormatCollector;
  }
  
  protected List<HistoricalValue> getHistory()
  {
    return Collections.emptyList();
  }
  
  /**
   * Implementations of this method should send previously buffered events to the server by calling fireEvent().
   * 
   * @return Number of events sent
   */
  
  protected int sendBufferedEvents()
  {
    return 0;
  }
  
  VariableGetter assetReader = new VariableGetter()
  {
    
    @Override
    public DataTable get(Context con, VariableDefinition def, CallerController caller, RequestController request) throws ContextException
    {
      return DataTableConversion.beansToTable(assets, DeviceAssetDefinition.FORMAT);
    }
  };
  
  FunctionImplementation loginImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      String challenge = parameters.rec().getString(FIF_LOGIN_CHALLENGE);
      
      String response = Md5Utils.hexHash(challenge + server.getPassword());
      
      return new DataRecord(FOFT_LOGIN).addString(server.getUsername()).addString(name).addString(response).wrap();
    }
  };
  
  FunctionImplementation registerImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      return new DataRecord(FOFT_REGISTER).setValue(FOF_PASSWORD, server.getPassword()).setValue(FOF_COMMAND_TIMEOUT, server.getCommandTimeout()).wrap();
    }
  };
  
  FunctionImplementation externalLoginImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      return new DataRecord(FOFT_EXT_LOGIN).setValue(FOF_PASSWORD, server.getPassword()).wrap();
    }
  };
  
  FunctionImplementation synchronizedImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      setSynchronized(true);
      return null;
    }
  };
  
  FunctionImplementation confirmEventImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      confirmEvent(parameters.rec().getLong(FIF_CONFIRM_EVENT_ID));
      return null;
    }
  };
  
  FunctionImplementation acknowledgeEventImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      acknowledgeEvent(parameters.rec().getLong(FIF_ACKNOWLEDGE_EVENT_ID), parameters.rec().getDate(FIF_ACKNOWLEDGE_EVENT_DATE), parameters.rec().getString(FIF_ACKNOWLEDGE_EVENT_AUTHOR),
          parameters.rec().getString(FIF_ACKNOWLEDGE_EVENT_ACKNOWLEDGEMENT), parameters.rec().getDataTable(FIF_ACKNOWLEDGE_EVENT_EVENT_DATA));
      return null;
    }
  };
  
  FunctionImplementation getHistoryImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      DataTable res = new SimpleDataTable(def.getOutputFormat());
      
      for (HistoricalValue hv : getHistory())
      {
        DataRecord rec = res.addRecord();
        rec.addString(hv.getVariable());
        rec.addDate(hv.getTimestamp());
        rec.addDataTable(hv.getValue());
      }
      
      return res;
    }
  };
  
  FunctionImplementation getFormatImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      int id = parameters.rec().getInt(RootContextConstants.FIF_GET_FORMAT_ID);
      if (knownFormatCollector != null && knownFormatCollector.isKnown(id))
      {
        TableFormat format = formatCache != null ? formatCache.get(id) : null;
        
        if (format == null)
        {
          throw new ContextException("Format with requested ID does not exist: " + id);
        }
        return new SimpleDataTable(CommonServerFormats.FOFT_GET_FORMAT, format.encode(new ClassicEncodingSettings(false)));
      }
      
      throw new ContextException("Format with requested ID does not exist or is not available: " + id);
    }
  };
  
  FunctionImplementation sendBufferedEventsImpl = new FunctionImplementation()
  {
    @Override
    public DataTable execute(Context con, FunctionDefinition def, CallerController caller, RequestController request, DataTable parameters) throws ContextException
    {
      DataRecord rec = new DataRecord(FOFT_SEND_BUFFERED_EVENTS);
      rec.setValue(FOF_BUFFERED_EVENTS_SENT, sendBufferedEvents());
      return rec.wrap();
    }
  };
  
}
