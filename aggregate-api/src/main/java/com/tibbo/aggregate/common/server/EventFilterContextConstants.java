package com.tibbo.aggregate.common.server;

public interface EventFilterContextConstants
{
  public static final String V_RULES = "rules";
  public static final String V_PRIMARY_FIELDS = "shownFields";
  public static final String V_ADDITIONAL_FIELDS = "additionalFields";
  public static final String V_CUSTOM_HIGHLIGHTING = "customHighlighting";
  public static final String V_HISTORY_SETTINGS = "historySettings";
  
  public static final String F_GET_PARAMETERS = "getParameters";
  public static final String F_ACTIVATE = "activate";
  public static final String F_ADDITIONAL_REFERENCES = "additionalReferences";
  
  public static final String A_CONFIGURE_FROM_LOG = "configureFromLog";
  public static final String A_PARAMETERIZE = "parameterize";
  
  public static final String VF_ADDITIONAL_FIELDS_NAME = "name";
  public static final String VF_ADDITIONAL_FIELDS_DESCRIPTION = "description";
  public static final String VF_ADDITIONAL_FIELDS_EDESCS = "edescs";
  public static final String VF_ADDITIONAL_FIELDS_SHOWN = "shown";
  public static final String VF_CUSTOM_HIGHLIGHTING_MASK = "mask";
  public static final String VF_CUSTOM_HIGHLIGHTING_EVENT = "event";
  public static final String VF_CUSTOM_HIGHLIGHTING_EXPRESSION = "expression";
  public static final String VF_CUSTOM_HIGHLIGHTING_LEVEL = "level";
  public static final String VF_CUSTOM_HIGHLIGHTING_COLOR = "color";
  
  public static final String FIF_GET_PARAMETERS_REALTIME = "realtime";
  public static final String FIF_GET_PARAMETERS_EDITABLE_BINDINGS = "editableBindings";
  
  public static final String FIF_ACTIVATE_REALTIME = "realtime";
  public static final String FIF_ACTIVATE_LISTENER = "listener";
  public static final String FIF_ACTIVATE_PARAMETERS = "parameters";
  public static final String FIF_ACTIVATE_DEFAULT_CONTEXT = "defaultContext";
  
  public static final int STATUS_NORMAL = 0;
  public static final int STATUS_DEFAULT = 1;
  
}
