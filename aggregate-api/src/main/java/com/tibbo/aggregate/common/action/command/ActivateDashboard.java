package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class ActivateDashboard extends GenericActionCommand
{
  public static final String CF_NAME = "name";
  public static final String CF_PATH = "path";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_ACTION_PARAMETERS = "actionParameters";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";
  
  public static final TableFormat CFT_ACTIVATE_DASHBOARD = new TableFormat(1, 1);
  static
  {
    CFT_ACTIVATE_DASHBOARD.addField("<" + CF_NAME + "><S>");
    
    FieldFormat ff = FieldFormat.create(CF_PATH, FieldFormat.STRING_FIELD, Cres.get().getString("dashboard"));
    ff.setNullable(true);
    ff.setEditor(StringFieldFormat.EDITOR_CONTEXT);
    ff.setEditorOptions(StringFieldFormat.encodeMaskEditorOptions(Contexts.TYPE_DASHBOARD, Contexts.CTX_DASHBOARDS));
    CFT_ACTIVATE_DASHBOARD.addField(ff);
    
    ff = FieldFormat.create("<" + CF_LOCATION + "><T><F=N>");
    ff.setDefault(new WindowLocation().toDataTable());
    CFT_ACTIVATE_DASHBOARD.addField(ff);
    
    ff = FieldFormat.create("<" + CF_DASHBOARD + "><T><F=N>");
    ff.setDefault(new DashboardProperties().toDataTable());
    CFT_ACTIVATE_DASHBOARD.addField(ff);
    
    CFT_ACTIVATE_DASHBOARD.addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
    
    ff = FieldFormat.create(CF_ACTION_PARAMETERS, FieldFormat.DATATABLE_FIELD).setNullable(true).setHidden(true);
    CFT_ACTIVATE_DASHBOARD.addField(ff);
    
    ff = FieldFormat.create("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=N>");
    ff.setDefault(new DashboardsHierarchyInfo().toDataTable());
    CFT_ACTIVATE_DASHBOARD.addField(ff);
    
    CFT_ACTIVATE_DASHBOARD.addField("<" + CF_DEFAULT_CONTEXT + "><S><F=N><D=" + ">");
    CFT_ACTIVATE_DASHBOARD.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
  }
  
  private String name;
  private String path;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private DataTable actionParameters;
  private DashboardsHierarchyInfo dhInfo;
  private String defaultContext;
  
  public ActivateDashboard()
  {
    super(ActionUtils.CMD_ACTIVATE_DASHBOARD, CFT_ACTIVATE_DASHBOARD, null);
  }
  
  public ActivateDashboard(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_ACTIVATE_DASHBOARD, title, parameters, CFT_ACTIVATE_DASHBOARD);
  }
  
  public ActivateDashboard(String name)
  {
    super(ActionUtils.CMD_ACTIVATE_DASHBOARD, (String) null);
    this.name = name;
  }
  
  public ActivateDashboard(String nameString, String windowPathString)
  {
    this(nameString);
    this.path = windowPathString;
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_ACTIVATE_DASHBOARD, name, path, location != null ? location.toDataTable() : null, dashboard != null ? dashboard.toDataTable() : null, key, actionParameters,
        dhInfo != null ? dhInfo.toDataTable() : null, defaultContext, getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getPath()
  {
    return path;
  }
  
  public void setPath(String path)
  {
    this.path = path;
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
  
  public DataTable getActionParameters()
  {
    return actionParameters;
  }
  
  public void setActionParameters(DataTable actionParameters)
  {
    this.actionParameters = actionParameters;
  }
  
  public DashboardsHierarchyInfo getDashboardsHierarchyInfo()
  {
    return dhInfo;
  }
  
  public void setDashboardsHierarchyInfo(DashboardsHierarchyInfo dhInfo)
  {
    this.dhInfo = dhInfo;
  }
  
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }

}
