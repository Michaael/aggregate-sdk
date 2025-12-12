package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class EditReport extends GenericActionCommand
{
  // danil TODO: CF_... constants are to widely used, they should be moved outside this class
  public static final String CF_TEMPLATE = "template";
  public static final String CF_DATA = "data";
  public static final String CF_SUBREPORTS = "subreports";
  public static final String CF_RESOURCES = "resources";
  
  public static final String RF_TEMPLATE = "template";
  public static final String RF_RESULT = "result";
  
  public static final TableFormat CFT_EDIT_REPORT = new TableFormat(1, 1);
  static
  {
    CFT_EDIT_REPORT.addField("<" + CF_TEMPLATE + "><S>");
    CFT_EDIT_REPORT.addField("<" + CF_DATA + "><T>");
    CFT_EDIT_REPORT.addField("<" + CF_SUBREPORTS + "><T>");
    CFT_EDIT_REPORT.addField("<" + CF_RESOURCES + "><T>");
  }
  
  public static final TableFormat RFT_EDIT_REPORT = new TableFormat(1, 1);
  static
  {
    RFT_EDIT_REPORT.addField("<" + RF_RESULT + "><S>");
    RFT_EDIT_REPORT.addField("<" + RF_TEMPLATE + "><S><F=N>");
  }
  
  public static final String CF_SUBREPORTS_NAME = "name";
  public static final String CF_SUBREPORTS_TEMPLATE = "template";
  public static final String CF_SUBREPORTS_DATA = "data";
  
  public static final TableFormat CFT_SUBREPORTS = new TableFormat();
  static
  {
    CFT_SUBREPORTS.addField("<" + CF_SUBREPORTS_NAME + "><S>");
    CFT_SUBREPORTS.addField("<" + CF_SUBREPORTS_TEMPLATE + "><S>");
    CFT_SUBREPORTS.addField("<" + CF_SUBREPORTS_DATA + "><T>");
  }
  
  public static final String CF_RESOURCES_DATA = "data";

  public static final TableFormat CFT_RESOURCES = new TableFormat();
  static
  {
    CFT_RESOURCES.addField("<" + CF_RESOURCES_DATA + "><A>");
  }
  
  private String template;
  private DataTable data;
  private DataTable subreports;
  private DataTable resources;
  
  public EditReport()
  {
    super(ActionUtils.CMD_EDIT_REPORT, CFT_EDIT_REPORT, RFT_EDIT_REPORT);
  }
  
  public EditReport(String title, String template, DataTable data, DataTable subreports, DataTable resources)
  {
    super(ActionUtils.CMD_EDIT_REPORT, title);
    this.template = template;
    this.data = data;
    this.subreports = subreports;
    this.resources = resources;
  }
  
  public EditReport(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_REPORT, title, parameters, CFT_EDIT_REPORT);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_EDIT_REPORT, template, data, subreports, resources);
  }
  
  public String getTemplate()
  {
    return template;
  }
  
  public void setTemplate(String template)
  {
    this.template = template;
  }
  
  public DataTable getData()
  {
    return data;
  }
  
  public void setData(DataTable data)
  {
    this.data = data;
  }
  
}
