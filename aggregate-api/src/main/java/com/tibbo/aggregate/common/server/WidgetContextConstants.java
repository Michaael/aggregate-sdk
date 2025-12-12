package com.tibbo.aggregate.common.server;

public interface WidgetContextConstants
{
  String V_RUNTIME_DATA = "runtimeData";
  String V_TEMPLATE = "template";
  
  String F_VALUE = "value";
  String F_PROPERTY = "property";
  
  String A_LAUNCH = "launch";
  String A_EDIT_TEMPLATE = "editTemplate";
  
  String FIELD_WIDGET_NAME = "name";
  String FIELD_WIDGET_DESCRIPTION = "description";
  String FIELD_WIDGET_TEMPLATE = "template";
  String FIELD_WIDGET_TYPE = "type";
  String FIELD_WIDGET_VALIDITY_EXPRESSION = "validityExpression";
  String FIELD_WIDGET_VALIDITY_LISTENERS = "validityListeners";
  String FIELD_GENERATE_ATTACHED_EVENTS = "generateAttachedEvents";
  String FIELD_WIDGET_ALLOW_VALIDITY_FOR_REMOTE_CONTEXTS = "allowValidityForRemoteContexts";
  String FIELD_WIDGET_DEFAULT_CONTEXT = "defaultContext";
  String FIELD_WIDGET_DEFAULT_LOCATION = "defaultLocation";
  String FIELD_WIDGET_DEFAULT_DASHBOARD = "defaultDashboard";
  String FIELD_WIDGET_RESOURCE_BUNDLE = "resourceBundle";
  String WIDGETS_PLUGIN_V_WIDGET_SETTINGS = "widgetSettings";
  String WIDGETS_PLUGIN_ID = "com.tibbo.linkserver.plugin.context.widgets";
  String WIDGETS_PLUGIN_WEBAPPS_WEB_WIDGETS_MODE = "webAppsWidgetsMode";
  String WIDGETS_PLUGIN_PROHIBITS_SCRIPT_EXECUTION = "prohibitsScriptExecution";
  String WIDGETS_PLUGIN_ENCODING = "encoding";
  String WIDGETS_PLUGIN_REFRESH_RATE = "refreshRate";
  
  int WEB_WIDGETS_MODE_MOBILE = 0;
  int WEB_WIDGETS_MODE_APPLET = 1;
  
  int EncodingRaw = 0;
  int EncodingCopyRect = 1;
  int EncodingRRE = 2;
  int EncodingCoRRE = 4;
  int EncodingHextile = 5;
  
  String AG_BUILDER_PROTOCOL = "agbuilder://";
  String AG_BUILDER_USERNAME = "username";
  String AG_BUILDER_PASSWORD = "password";
  String AG_BUILDER_ADDRESS = "address";
  String AG_BUILDER_PORT = "port";
  String AG_BUILDER_CONTEXT_TEMPLATE = "contextTemplate";
  String AG_BUILDER_DEFAULT_CONTEXT = "defaultContext";
  String COMPONENT_CONNECTOR = "connector";
  String COMPONENT_ROOT_PANEL = "root";
}
