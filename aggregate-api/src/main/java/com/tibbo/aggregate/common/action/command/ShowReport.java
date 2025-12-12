package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.util.*;

public class ShowReport extends GenericActionCommand
{
  public static final String CF_REPORT_DATA = "reportData";
  public static final String CF_LOCATION = "location";
  public static final String CF_DASHBOARD = "dashboard";
  public static final String CF_KEY = "key";
  public static final String CF_DASHBOARDS_HIERARCHY_INFO = "dashboardsHierarchyInfo";
  public static final String CF_REPORT_FORMAT = "reportFormat";
  public static final String CF_FILE_NAME = "fileName";
  public static final String CF_COMPONENT_LOCATION = "componentLocation";

  public static final TableFormat CFT_SHOW_REPORT = new TableFormat(1, 1)
  {
    {
      addField("<" + CF_REPORT_DATA + "><A><F=N>");
      addField("<" + CF_LOCATION + "><T><F=NH>");
      addField("<" + CF_DASHBOARD + "><T><F=NH>");
      addField("<" + CF_KEY + "><S><F=NH><D=" + Cres.get().getString("key") + ">");
      addField("<" + CF_DASHBOARDS_HIERARCHY_INFO + "><T><F=NH>");
      addField("<" + CF_REPORT_FORMAT + "><S><F=NH>");
      addField("<" + CF_FILE_NAME + "><S><F=N>");
      addField("<" + CF_COMPONENT_LOCATION + "><T><F=NH><D=" + Cres.get().getString("componentLocation") + ">");
    }
  };
  
  private byte[] reportData;
  private WindowLocation location;
  private DashboardProperties dashboard;
  private String key;
  private DashboardsHierarchyInfo dhInfo;
  private String reportFormat;
  private String fileName;

  public ShowReport()
  {
    super(ActionUtils.CMD_SHOW_REPORT, CFT_SHOW_REPORT, null);
  }
  
  public ShowReport(String title, byte[] reportData, WindowLocation location, DashboardProperties dashboard)
  {
    this(title, reportData, location, dashboard, null, null);
  }
  
  public ShowReport(String title, byte[] reportData, WindowLocation location, DashboardProperties dashboard,
                    String reportFormat, String fileName)
  {
    super(ActionUtils.CMD_SHOW_REPORT, title);
    this.reportData = reportData;
    this.location = location;
    this.dashboard = dashboard;
    this.reportFormat = reportFormat;
    this.fileName = fileName;
  }
  
  public ShowReport(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_REPORT, title, parameters, CFT_SHOW_REPORT);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    DataTable t = new SimpleDataTable(CFT_SHOW_REPORT);
    DataRecord r = t.addRecord();
    r.addData(new Data(reportData));
    r.addDataTable(location != null ? location.toDataTable() : null);
    r.addDataTable(dashboard != null ? dashboard.toDataTable() : null);
    r.addString(key);
    r.addDataTable(dhInfo != null ? dhInfo.toDataTable() : null);
    r.addString(reportFormat);
    r.addString(fileName);
    r.addDataTable(getComponentLocation() != null ? getComponentLocation().toDataTable() : null);
    return t;
  }
  
  public byte[] getReportData()
  {
    return reportData;
  }
  
  public void setReportData(byte[] reportData)
  {
    this.reportData = reportData;
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
  
  public String getReportFormat()
  {
    return reportFormat;
  }
  
  public void setReportFormat(String reportFormat)
  {
    this.reportFormat = reportFormat;
  }
  
  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

}
