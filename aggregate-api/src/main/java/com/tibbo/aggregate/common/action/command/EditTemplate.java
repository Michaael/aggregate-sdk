package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;

public class EditTemplate extends GenericActionCommand
{
  public static final int EDIT_WIDGET = 0;
  public static final int EDIT_ST = 1;
  public static final int EDIT_SFC = 2;
  public static final int EDIT_FBD = 3;
  public static final int EDIT_LD = 4;
  public static final int EDIT_WORKFLOW = 5;
  
  public static final String CF_DEFAULT_CONTEXT = "defaultContext";
  public static final String CF_WIDGET_CONTEXT = "widgetContext";
  public static final String CF_WIDGET = "widget";
  public static final String CF_EDIT_MODE = "editMode";
  
  public static final String RF_WIDGET = "widget";
  public static final String RF_RESULT = "result";
  
  public static final TableFormat CFT_EDIT_TEMPLATE = new TableFormat(1, 1);
  
  static
  {
    CFT_EDIT_TEMPLATE.addField("<" + CF_DEFAULT_CONTEXT + "><S><F=N>");
    CFT_EDIT_TEMPLATE.addField("<" + CF_WIDGET_CONTEXT + "><S>");
    CFT_EDIT_TEMPLATE.addField("<" + CF_WIDGET + "><S><F=N>");
    CFT_EDIT_TEMPLATE.addField("<" + CF_EDIT_MODE + "><I>");
  }
  
  public static final TableFormat RFT_EDIT_WIDGET = new TableFormat(1, 1);
  
  static
  {
    RFT_EDIT_WIDGET.addField("<" + RF_RESULT + "><S>");
    RFT_EDIT_WIDGET.addField("<" + RF_WIDGET + "><S><F=N>");
  }
  
  private String defaultContext;
  private String widgetContext;
  private String widget;
  private int editMode;
  
  public EditTemplate(String type)
  {
    super(type, CFT_EDIT_TEMPLATE, RFT_EDIT_WIDGET);
  }
  
  public EditTemplate(String type, String title, String defaultContext, String widgetContext, String widget, int editMode)
  {
    super(type, title);
    this.defaultContext = defaultContext;
    this.widgetContext = widgetContext;
    this.widget = widget;
    this.editMode = editMode;
  }
  
  public EditTemplate(String type, String title, DataTable parameters)
  {
    super(type, title, parameters, CFT_EDIT_TEMPLATE);
  }

  public EditTemplate( String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_WIDGET, title, parameters, CFT_EDIT_TEMPLATE);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_EDIT_TEMPLATE, defaultContext, widgetContext, widget, editMode);
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
  
  public String getWidget()
  {
    return widget;
  }
  
  public void setWidget(String widget)
  {
    this.widget = widget;
  }
  
  public int getEditMode()
  {
    return editMode;
  }
  
  public void setEditMode(int editMode)
  {
    this.editMode = editMode;
  }
  
}
