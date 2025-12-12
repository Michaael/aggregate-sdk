package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.action.*;

public interface DashboardContextConstants
{
  public static final String V_ELEMENTS = "elements";
  public static final String V_LAYOUT = "layout";
  
  public static final String ELEMENT_FIELD_ID = "id";
  // public static final String ELEMENT_FIELD_KEY = "key";
  public static final String ELEMENT_FIELD_TITLE = "title";
  public static final String ELEMENT_FIELD_TYPE = "type";
  public static final String ELEMENT_FIELD_LOCATION = "location";
  public static final String ELEMENT_FIELD_PARAMETERS = "parameters";
  public static final String ELEMENT_FIELD_VALIDITY_EXPRESSION = "validityExpression";
  
  public static final String ELEMENT_FIELD_NAME = "name";
  
  public static final String FIELD_LAYOUT = "layout";
  
  public static final String F_DASHBOARD_PROPERTIES = "dashboardProperties";
  public static final String F_ELEMENT_PARAMETERS = "elementParameters";
  public static final String F_DOCK_LAYOUT = "dockLayout";
  
  public static final String A_OPEN = "open";
  public static final String A_EDIT = "edit";
  
  public static final String FIF_PARAMETERS_TYPE = "type";
  public static final String FIF_PARAMETERS_PARAMETERS = "parameters";
  public static final String FIF_PROPERTIES_INSTANCE_ID = "instance_id";
  
  // Dashboard Elements
  public static final String ELEMENT_CLASS_WIDGET = ActionUtils.CMD_LAUNCH_WIDGET;
  public static final String ELEMENT_CLASS_EVENT_LOG = ActionUtils.CMD_SHOW_EVENT_LOG;
  public static final String ELEMENT_CLASS_SYSTEM_TREE = ActionUtils.CMD_SHOW_SYSTEM_TREE;
  public static final String ELEMENT_CLASS_DATA = ActionUtils.CMD_EDIT_DATA;
  public static final String ELEMENT_CLASS_PROPERTIES = ActionUtils.CMD_EDIT_PROPERTIES;
  public static final String ELEMENT_CLASS_REPORT = ActionUtils.CMD_SHOW_REPORT;
  public static final String ELEMENT_CLASS_DASHBOARD = ActionUtils.CMD_ACTIVATE_DASHBOARD;
  public static final String ELEMENT_CLASS_HTML_SNIPPET = ActionUtils.CMD_SHOW_HTML_SNIPPET;
  public static final String ELEMENT_CLASS_INSTANCE_FIELDS = "classInstanceFields";
  public static final String ELEMENT_CLASS_INSTANCE_LIST = "classInstanceList";
  public static final String ELEMENT_CLASS_CLOSE_DASHBOARD = ActionUtils.CMD_CLOSE_DASHBOARD;
}
