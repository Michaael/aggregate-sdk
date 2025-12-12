package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class CloseDashboard extends GenericActionCommand
{
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_TARGET_ELEMENT = "targetElement";
  public static final String CF_CLOSE_ALL = "closeAll";
  public static final String CF_DEEP_SEARCH = "deepSearch";
  
  public static final TableFormat CFT_CLOSE_DASHBOARD = new TableFormat(1, 1);
  
  static
  {
    CFT_CLOSE_DASHBOARD.addField(FieldFormat.create("<" + CF_DASHBOARD + "><T><F=N>"));
    CFT_CLOSE_DASHBOARD.addField(FieldFormat.create("<" + CF_TARGET_ELEMENT + "><S><F=N>"));
    CFT_CLOSE_DASHBOARD.addField(FieldFormat.create("<" + CF_CLOSE_ALL + "><B>").setDefault(false));
    CFT_CLOSE_DASHBOARD.addField(FieldFormat.create("<" + CF_DEEP_SEARCH + "><B>").setDefault(false));
  }
  
  private DashboardProperties dashboard;
  
  private String targetElement;
  
  private Boolean closeAll;
  
  private Boolean deepSearch;
  
  public CloseDashboard()
  {
    super(ActionUtils.CMD_CLOSE_DASHBOARD, CFT_CLOSE_DASHBOARD, null);
  }
  
  public CloseDashboard(DashboardProperties dashboard)
  {
    super(ActionUtils.CMD_CLOSE_DASHBOARD, CFT_CLOSE_DASHBOARD, null);
    this.dashboard = dashboard;
  }
  
  public CloseDashboard(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_CLOSE_DASHBOARD, title, parameters, CFT_CLOSE_DASHBOARD);
  }
  
  public CloseDashboard(String name)
  {
    super(ActionUtils.CMD_CLOSE_DASHBOARD, (String) null);
  }
  
  public CloseDashboard(String nameString, String windowPathString)
  {
    this(nameString);
  }
  
  public DashboardProperties getDashboard()
  {
    return dashboard;
  }
  
  public void setDashboard(DashboardProperties dashboard)
  {
    this.dashboard = dashboard;
  }
  
  public String getTargetElement()
  {
    return targetElement;
  }
  
  public void setTargetElement(String targetElement)
  {
    this.targetElement = targetElement;
  }
  
  public Boolean getCloseAll()
  {
    return closeAll;
  }
  
  public void setCloseAll(Boolean closeAll)
  {
    this.closeAll = closeAll;
  }
  
  public Boolean getDeepSearch()
  {
    return deepSearch;
  }
  
  public void setDeepSearch(Boolean deepSearch)
  {
    this.deepSearch = deepSearch;
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_CLOSE_DASHBOARD, dashboard != null ? dashboard.toDataTable() : null, targetElement, closeAll, deepSearch);
  }
}
