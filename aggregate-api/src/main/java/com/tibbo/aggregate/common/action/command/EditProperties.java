package com.tibbo.aggregate.common.action.command;

import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class EditProperties extends GenericActionCommand
{
  public static final String CF_READ_ONLY = "readOnly";
  public static final String CF_DYNAMIC = "dynamic";
  public static final String CF_ASYNC = "async";
  public static final String CF_USE_DOCKABLE_FRAME = "useDockableFrame";
  public static final String CF_SINGLE_WINDOW_MODE = "singleWindowMode";
  public static final String CF_CONTEXT = "context";
  public static final String CF_SLAVES = "slaves";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_DEFAULT_GROUP = "defaultGroup";
  
  public static final String CF_PROPERTIES = "properties";
  public static final String CF_PROPERTIES_GROUP = "propertiesGroup";
  
  public static final String RF_EDIT_PROPERTIES_RESULT = "result";
  public static final String RF_EDIT_PROPERTIES_CHANGED_PROPERTIES = "changedProperties";
  
  public static final String FIELD_PROPERTIES_PROPERTY = "property";
  
  public static final String CF_SLAVES_CONTEXT = "context";
  
  public static final TableFormat CFT_SLAVES = FieldFormat.create("<" + CF_SLAVES_CONTEXT + "><S>").wrap();
  
  public static final TableFormat FT_PROPERTIES = FieldFormat.create("<" + FIELD_PROPERTIES_PROPERTY + "><S><D=" + Cres.get().getString("property") + ">").wrap();
  public static final String CF_COMPONENT_LOCATION = "componentLocation";

  public static final TableFormat CFT_EDIT_PROPERTIES = new TableFormat(1, 1)
  {
    {
      addField("<" + CF_CONTEXT + "><S><D=" + Cres.get().getString("context") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
      
      addField("<" + CF_PROPERTIES_GROUP + "><S><F=N><D=" + Cres.get().getString("group") + ">");
      
      FieldFormat properties = FieldFormat.create("<" + CF_PROPERTIES + "><T><D=" + Cres.get().getString("properties") + ">");
      properties.setDefault(new SimpleDataTable(FT_PROPERTIES, true));
      addField(properties);
      
      addField("<" + CF_SINGLE_WINDOW_MODE + "><B><D=" + Cres.get().getString("acSingleWindowMode") + ">");
      
      addField("<" + CF_USE_DOCKABLE_FRAME + "><B><D=" + Cres.get().getString("acUseDockableFrame") + ">");
      addField("<" + CF_READ_ONLY + "><B><D=" + Cres.get().getString("acInitiallyReadOnly") + ">");
      addField("<" + CF_DYNAMIC + "><B><D=" + Cres.get().getString("dynamic") + ">");
      addField("<" + CF_ASYNC + "><B><D=" + Cres.get().getString("acDoNotWaitClosure") + ">");
      addField("<" + CF_SLAVES + "><T><F=NH>");
      addField("<" + CF_LOCATION + "><T><F=NH>");
      addField("<" + CF_DASHBOARD + "><T><F=NH>");
      
      addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
      addField("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
      addField(FieldFormat.STRING_FIELD, CF_DEFAULT_GROUP , Cres.get().getString("defaultGroup"), null, true);
      addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
      
      String ref = CF_PROPERTIES + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
      String exp = "{" + CF_PROPERTIES_GROUP + "} == null";
      addBinding(ref, exp);
    }
  };
  
  public static final TableFormat RFT_EDIT_PROPERTIES = new TableFormat(1, 1)
  {
    {
      addField("<" + RF_EDIT_PROPERTIES_RESULT + "><S>");
      
      FieldFormat changedProperties = FieldFormat.create("<" + RF_EDIT_PROPERTIES_CHANGED_PROPERTIES + "><T>");
      changedProperties.setDefault(new SimpleDataTable(FT_PROPERTIES, true));
      addField(changedProperties);
    }
  };
  
  private String context;
  private String propertiesGroup;
  private List<String> properties;
  private List<String> slaves;
  private boolean readOnly;
  private boolean dynamic;
  private boolean useDockableFrame;
  private boolean singleWindowMode;
  private boolean async;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private DashboardsHierarchyInfo dhInfo;
  private String key;
  private String defaultGroup;
  
  public EditProperties()
  {
    super(ActionUtils.CMD_EDIT_PROPERTIES, CFT_EDIT_PROPERTIES, RFT_EDIT_PROPERTIES);
  }
  
  public EditProperties(String title, String contextName, String propertiesGroup)
  {
    super(ActionUtils.CMD_EDIT_PROPERTIES, title);
    this.context = contextName;
    this.propertiesGroup = propertiesGroup;
  }
  
  public EditProperties(String title, String contextName, List<String> properties)
  {
    super(ActionUtils.CMD_EDIT_PROPERTIES, title);
    this.context = contextName;
    this.properties = properties;
  }
  
  public EditProperties(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_PROPERTIES, title, parameters, CFT_EDIT_PROPERTIES);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataTable slavesTable = createSlavesTable(slaves);
    DataTable table = new SimpleDataTable(CFT_EDIT_PROPERTIES);
    DataRecord rec = table.addRecord();
    
    rec.addString(context);
    rec.addString(propertiesGroup);
    
    DataTable propertiesTable = new SimpleDataTable(FT_PROPERTIES);
    if (properties != null)
    {
      for (String property : properties)
      {
        propertiesTable.addRecord().addString(property);
      }
    }
    rec.addDataTable(propertiesTable);
    
    rec.addBoolean(singleWindowMode);
    rec.addBoolean(useDockableFrame);
    rec.addBoolean(readOnly);
    rec.addBoolean(dynamic);
    rec.addBoolean(async);
    rec.addDataTable(slavesTable);
    rec.addDataTable(location != null ? location.toDataTable() : null);
    rec.addDataTable(dashboard != null ? dashboard.toDataTable() : null);
    rec.addString(key);
    rec.addDataTable(dhInfo != null ? dhInfo.toDataTable() : null);
    rec.addString(defaultGroup);
    rec.addDataTable(getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
    
    return rec.wrap();
  }
  
  private DataTable createSlavesTable(List<String> slaves)
  {
    if (slaves == null)
    {
      return null;
    }
    
    DataTable slavesTable = new SimpleDataTable(CFT_SLAVES);
    for (String slave : slaves)
    {
      slavesTable.addRecord().addString(slave);
    }
    return slavesTable;
  }
  
  public static List<Context> getSlaves(GenericActionCommand cmd, Context base, CallerController caller)
  {
    DataTable slavesTable = cmd.getParameters().rec().getDataTable(CF_SLAVES);
    
    if (slavesTable == null)
    {
      return null;
    }
    
    List<Context> slaves = new LinkedList<Context>();
    
    for (DataRecord rec : slavesTable)
    {
      String context = rec.getString(CF_SLAVES_CONTEXT);
      Context slave = base.get(context, caller);
      
      if (slave != null)
      {
        slaves.add(slave);
      }
    }
    
    return slaves;
  }
  
  public String getContext()
  {
    return context;
  }
  
  public void setContext(String contextName)
  {
    this.context = contextName;
  }
  
  public String getPropertiesGroup()
  {
    return propertiesGroup;
  }
  
  public void setPropertiesGroup(String propertiesGroup)
  {
    this.propertiesGroup = propertiesGroup;
  }
  
  public List<String> getProperties()
  {
    return properties;
  }
  
  public void setProperties(List<String> properties)
  {
    this.properties = properties;
  }
  
  public List<String> getSlaves()
  {
    return slaves;
  }
  
  public void setSlaves(List<String> slaves)
  {
    this.slaves = slaves;
  }
  
  public boolean isReadOnly()
  {
    return readOnly;
  }
  
  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }
  
  public boolean isDynamic()
  {
    return dynamic;
  }
  
  public void setDynamic(boolean dynamic)
  {
    this.dynamic = dynamic;
  }
  
  public boolean isUseDockableFrame()
  {
    return useDockableFrame;
  }
  
  public void setUseDockableFrame(boolean useDockableFrame)
  {
    this.useDockableFrame = useDockableFrame;
  }
  
  public boolean isSingleWindowMode()
  {
    return singleWindowMode;
  }
  
  public void setSingleWindowMode(boolean singleWindowMode)
  {
    this.singleWindowMode = singleWindowMode;
  }
  
  public boolean isAsync()
  {
    return async;
  }
  
  public void setAsync(boolean async)
  {
    this.async = async;
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
  
  public String getDefaultGroup() 
  { 
    return defaultGroup; 
  }
  
  public void setDefaultGroup(String group) 
  { 
    this.defaultGroup = group;
  }

}
