package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.context.EntityList;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.EventFilterContextConstants;
import com.tibbo.aggregate.common.util.ComponentLocation;
import com.tibbo.aggregate.common.util.DashboardProperties;
import com.tibbo.aggregate.common.util.DashboardsHierarchyInfo;
import com.tibbo.aggregate.common.util.WindowLocation;

public class ShowEventLog extends GenericActionCommand
{
  public static final String CF_EVENT_FILTER = "eventFilter";
  public static final String CF_EVENT_LIST = "eventList";
  public static final String CF_SHOW_REALTIME = "showRealtime";
  public static final String CF_SHOW_HISTORY = "showHistory";
  public static final String CF_PRELOAD_HISTORY = "preloadHistory";
  public static final String CF_SHOW_CONTEXTS = "showContexts";
  public static final String CF_SHOW_NAMES = "showNames";
  public static final String CF_SHOW_LEVELS = "showLevels";
  public static final String CF_SHOW_DATA = "showData";
  public static final String CF_SHOW_ACKNOWLEDGEMENTS = "showAcknowledgements";
  public static final String CF_SHOW_ENRICHMENTS = "showEnrichments";
  public static final String CF_FILTER_PARAMETERS = "filterParameters";
  public static final String CF_CUSTOM_LISTENER_CODE = "customListenerCode";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_CLASS_NAME = "className";
  public static final String CF_INSTANCE_ID = "instanceId";
  public static final String CF_DEFAULT_EVENT = "defaultEvent";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";

  public static final String RF_LISTENER_CODE = "listenerCode";
  
  public static final TableFormat CFT_SHOW_EVENT_LOG = new TableFormat(1, 1);
  static
  {
    CFT_SHOW_EVENT_LOG.addField("<" + CF_EVENT_FILTER + "><S><F=N><D=" + Cres.get().getString("efEventFilter") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
    
    FieldFormat ff = FieldFormat.create("<" + CF_EVENT_LIST + "><T><D=" + Cres.get().getString("events") + ">");
    ff.setDefault(new SimpleDataTable(EntityList.FORMAT, true));
    CFT_SHOW_EVENT_LOG.addField(ff);
    
    CFT_SHOW_EVENT_LOG.addField(FieldFormat.create(CF_DEFAULT_EVENT, FieldFormat.STRING_FIELD, Cres.get().getString("elDefaultEvent")).setNullable(true));
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_REALTIME + "><B><A=1><D=" + Cres.get().getString("wCurrentEvents") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_HISTORY + "><B><A=1><D=" + Cres.get().getString("wEventHistory") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_PRELOAD_HISTORY + "><B><A=1><D=" + Cres.get().getString("wPreloadHistory") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_CONTEXTS + "><B><D=" + Cres.get().getString("wShowContextNames") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_NAMES + "><B><D=" + Cres.get().getString("wShowEventNames") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_LEVELS + "><B><D=" + Cres.get().getString("wShowEventLevels") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_DATA + "><B><A=1><D=" + Cres.get().getString("wShowEventData") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_ACKNOWLEDGEMENTS + "><B><D=" + Cres.get().getString("wShowEventAck") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_SHOW_ENRICHMENTS + "><B><A=0><D=" + Cres.get().getString("wShowEventEnrichments") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_FILTER_PARAMETERS + "><T><F=N><D=" + Cres.get().getString("parameters") + ">");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_CUSTOM_LISTENER_CODE + "><I><F=NH>");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_LOCATION + "><T><F=NH>");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_DASHBOARD + "><T><F=NH>");
    
    CFT_SHOW_EVENT_LOG.addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
    
    CFT_SHOW_EVENT_LOG.addField(FieldFormat.create(CF_DEFAULT_CONTEXT, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    CFT_SHOW_EVENT_LOG.addField(FieldFormat.create(CF_CLASS_NAME, FieldFormat.STRING_FIELD).setNullable(true).setHidden(true));
    CFT_SHOW_EVENT_LOG.addField(FieldFormat.create(CF_INSTANCE_ID, FieldFormat.LONG_FIELD).setNullable(true).setHidden(true));
    
    CFT_SHOW_EVENT_LOG.addField("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
    CFT_SHOW_EVENT_LOG.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
    
    String ref = CF_EVENT_LIST + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + CF_EVENT_FILTER + "} == null";
    CFT_SHOW_EVENT_LOG.addBinding(ref, exp);
    
    ref = CF_FILTER_PARAMETERS;
    exp = "{" + CF_EVENT_FILTER + "} != null ? " + DefaultFunctions.CALL_FUNCTION + "({" + CF_EVENT_FILTER + "}, '" + EventFilterContextConstants.F_GET_PARAMETERS + "', true, true) : null";
    CFT_SHOW_EVENT_LOG.addBinding(ref, exp);
  }
  
  public static final TableFormat RFT_SHOW_EVENT_LOG = new TableFormat(1, 1, "<" + ShowEventLog.RF_LISTENER_CODE + "><I><F=N>");
  
  private String eventFilter;
  private EntityList events = new EntityList();
  private boolean showRealtime;
  private boolean showHistory;
  private boolean preloadHistory;
  private boolean showContexts;
  private boolean showNames;
  private boolean showLevels;
  private boolean showData;
  private boolean showAcknowledgements;
  private boolean showEnrichments;
  private DataTable filterParameters;
  private Integer customListenerCode;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private DashboardsHierarchyInfo dhInfo;
  
  private String defaultContext;
  private String className;
  private Long instanceId;
  private String defaultEvent;
  
  public ShowEventLog()
  {
    super(ActionUtils.CMD_SHOW_EVENT_LOG, CFT_SHOW_EVENT_LOG, RFT_SHOW_EVENT_LOG);
  }
  
  public ShowEventLog(String title, EntityList eventList, boolean showRealtime, boolean showHistory, boolean preloadHistory, boolean showContexts, boolean showNames, boolean showLevels,
      boolean showAcknowledgements, boolean showEnrichments, Integer customListenerCode, WindowLocation location, DashboardProperties dashboard, ComponentLocation componentLocation)
  {
    super(ActionUtils.CMD_SHOW_EVENT_LOG, title);
    this.events = eventList;
    this.showRealtime = showRealtime;
    this.showHistory = showHistory;
    this.preloadHistory = preloadHistory;
    this.showContexts = showContexts;
    this.showNames = showNames;
    this.showLevels = showLevels;
    this.showData = true;
    this.showAcknowledgements = showAcknowledgements;
    this.showEnrichments = showEnrichments;
    this.customListenerCode = customListenerCode;
    this.location = location;
    this.dashboard = dashboard;
    setComponentLocation(componentLocation);
  }
  
  public ShowEventLog(String title, String eventFilter, boolean showRealtime, boolean showHistory, boolean preloadHistory)
  {
    super(ActionUtils.CMD_SHOW_EVENT_LOG, title);
    this.eventFilter = eventFilter;
    this.showRealtime = showRealtime;
    this.showHistory = showHistory;
    this.preloadHistory = preloadHistory;
  }
  
  public ShowEventLog(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_EVENT_LOG, title, parameters, CFT_SHOW_EVENT_LOG);
    events = new EntityList(parameters.rec().getDataTable(CF_EVENT_LIST));
    
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(
        ShowEventLog.CFT_SHOW_EVENT_LOG,
        eventFilter,
        events.toDataTable(),
        defaultEvent,
        showRealtime,
        showHistory,
        preloadHistory,
        showContexts,
        showNames,
        showLevels,
        showData,
        showAcknowledgements,
        showEnrichments,
        filterParameters,
        customListenerCode,
        location != null ? location.toDataTable() : null,
        dashboard != null ? dashboard.toDataTable() : null,
        key,
        defaultContext,
        className,
        instanceId,
        dhInfo != null ? dhInfo.toDataTable() : null,
            getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
  }
  
  public String getEventFilter()
  {
    return eventFilter;
  }
  
  public void setEventFilter(String eventFilter)
  {
    this.eventFilter = eventFilter;
  }
  
  public EntityList getEvents()
  {
    return events;
  }
  
  public void setEvents(EntityList eventList)
  {
    this.events = eventList;
  }
  
  public boolean isShowRealtime()
  {
    return showRealtime;
  }
  
  public void setShowRealtime(boolean showRealtime)
  {
    this.showRealtime = showRealtime;
  }
  
  public boolean isShowHistory()
  {
    return showHistory;
  }
  
  public void setShowHistory(boolean showHistory)
  {
    this.showHistory = showHistory;
  }
  
  public boolean isPreloadHistory()
  {
    return preloadHistory;
  }
  
  public void setPreloadHistory(boolean preloadHistory)
  {
    this.preloadHistory = preloadHistory;
  }
  
  public boolean isShowContexts()
  {
    return showContexts;
  }
  
  public void setShowContexts(boolean showContexts)
  {
    this.showContexts = showContexts;
  }
  
  public boolean isShowNames()
  {
    return showNames;
  }
  
  public void setShowNames(boolean showNames)
  {
    this.showNames = showNames;
  }
  
  public boolean isShowLevels()
  {
    return showLevels;
  }
  
  public void setShowLevels(boolean showLevels)
  {
    this.showLevels = showLevels;
  }
  
  public boolean isShowData()
  {
    return showData;
  }
  
  public void setShowData(boolean showData)
  {
    this.showData = showData;
  }
  
  public boolean isShowAcknowledgements()
  {
    return showAcknowledgements;
  }
  
  public void setShowAcknowledgements(boolean showAcknowledgements)
  {
    this.showAcknowledgements = showAcknowledgements;
  }
  
  public boolean isShowEnrichments()
  {
    return showEnrichments;
  }
  
  public void setShowEnrichments(boolean showEnrichments)
  {
    this.showEnrichments = showEnrichments;
  }
  
  public DataTable getFilterParameters()
  {
    return filterParameters;
  }
  
  public void setFilterParameters(DataTable filterParameters)
  {
    this.filterParameters = filterParameters;
  }
  
  public Integer getCustomListenerCode()
  {
    return customListenerCode;
  }
  
  public void setCustomListenerCode(Integer customListenerCode)
  {
    this.customListenerCode = customListenerCode;
  }
  
  public WindowLocation getLocation()
  {
    return location;
  }
  
  public void setLocation(WindowLocation location)
  {
    this.location = location;
  }
  
  public DashboardProperties getDashboard()
  {
    return dashboard;
  }
  
  public void setDashboard(DashboardProperties dashboard)
  {
    this.dashboard = dashboard;
  }
  
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public void setClassName(String className)
  {
    this.className = className;
  }
  
  public Long getInstanceId()
  {
    return instanceId;
  }
  
  public void setInstanceId(Long instanceId)
  {
    this.instanceId = instanceId;
  }
  
  public String getDefaultEvent()
  {
    return defaultEvent;
  }
  
  public void setDefaultEvent(String event)
  {
    this.defaultEvent = event;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
  
  public DashboardsHierarchyInfo getDashboardsHierarchyInfo()
  {
    return dhInfo;
  }
  
  public void setDashboardsHierarchyInfo(DashboardsHierarchyInfo dhInfo)
  {
    this.dhInfo = dhInfo;
  }

}
