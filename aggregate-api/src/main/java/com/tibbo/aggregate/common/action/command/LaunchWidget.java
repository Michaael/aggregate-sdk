package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.util.*;

public class LaunchWidget extends GenericActionCommand
{
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_WIDGET_CONTEXT = "widgetContext";
  public static final String CF_TEMPLATE = "template";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_INPUT = "input";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_WIDGET_SETTINGS = "widgetSettings";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";
  
  public static final TableFormat CFT_LAUNCH_WIDGET = new TableFormat(1, 1);
  
  static
  {
    CFT_LAUNCH_WIDGET.addField("<" + CF_WIDGET_CONTEXT + "><S><F=N><D=" + Cres.get().getString("widget") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
    CFT_LAUNCH_WIDGET.addField("<" + CF_DEFAULT_CONTEXT + "><S><F=N><D=" + Cres.get().getString("conDefaultContext") + "><E=" + StringFieldFormat.EDITOR_CONTEXT + ">");
    CFT_LAUNCH_WIDGET.addField("<" + CF_TEMPLATE + "><S><F=N><D=" + Cres.get().getString("template") + ">");
    CFT_LAUNCH_WIDGET.addField("<" + CF_LOCATION + "><T><F=NH>");
    CFT_LAUNCH_WIDGET.addField("<" + CF_DASHBOARD + "><T><F=NH>");
    CFT_LAUNCH_WIDGET.addField("<" + CF_KEY + "><S><F=NH>");
    CFT_LAUNCH_WIDGET.addField("<" + CF_INPUT + "><T><F=NH>");
    CFT_LAUNCH_WIDGET.addField("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
    CFT_LAUNCH_WIDGET.addField(FieldFormat.create("<" + CF_WIDGET_SETTINGS + "><T><F=NH>").setDefault(null));
    CFT_LAUNCH_WIDGET.addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
  }
  
  private String widgetContext;
  private String defaultContext;
  private String template;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private DataTable input;
  private DashboardsHierarchyInfo dhInfo;
  private DataTable widgetSettings;
  
  public LaunchWidget()
  {
    super(ActionUtils.CMD_LAUNCH_WIDGET, CFT_LAUNCH_WIDGET, null);
  }
  
  public LaunchWidget(String title, String widgetContext, String defaultContext)
  {
    super(ActionUtils.CMD_LAUNCH_WIDGET, title);
    this.widgetContext = widgetContext;
    this.defaultContext = defaultContext;
  }
  
  public LaunchWidget(String title, String widgetContext, String defaultContext, String template)
  {
    this(title, widgetContext, defaultContext);
    this.template = template;
  }
  
  public LaunchWidget(String keyString, String title, DataTable parameters)
  {
    super(ActionUtils.CMD_LAUNCH_WIDGET, title, parameters, CFT_LAUNCH_WIDGET);
    this.key = keyString;
  }
  
  public LaunchWidget(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_LAUNCH_WIDGET, title, parameters, CFT_LAUNCH_WIDGET);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_LAUNCH_WIDGET, widgetContext, defaultContext, template, location != null ? location.toDataTable() : null, dashboard != null ? dashboard.toDataTable() : null, key,
        input, dhInfo != null ? dhInfo.toDataTable() : null, widgetSettings, getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
  }
  
  public String getDefaultContext()
  {
    return defaultContext;
  }
  
  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }
  
  public String getWidgetContext()
  {
    return widgetContext;
  }
  
  public void setWidgetContext(String widgetContext)
  {
    this.widgetContext = widgetContext;
  }
  
  public String getTemplate()
  {
    return template;
  }
  
  public void setTemplate(String encodedWidgetTemplate)
  {
    this.template = encodedWidgetTemplate;
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
  
  public DataTable getInput()
  {
    return input;
  }
  
  public void setInput(DataTable input)
  {
    this.input = input;
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
  
  public DataTable getWidgetSettings()
  {
    return widgetSettings;
  }
  
  public void setWidgetSettings(DataTable widgetSettings)
  {
    this.widgetSettings = widgetSettings;
  }
}
