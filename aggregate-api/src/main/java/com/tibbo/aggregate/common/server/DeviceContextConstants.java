package com.tibbo.aggregate.common.server;

public interface DeviceContextConstants
{
  String V_GENERIC_PROPERTIES = "genericProperties";
  String V_CONNECTION_PROPERTIES = "connectionProperties";
  String V_STATUS_EXPRESSIONS = "statusExpressions";
  String V_STATUS = "status";
  String V_STATUS_SYNC_RECENT_LOG = "statusSyncRecentLog";

  String V_SETTING_STATUSES = "settingStatuses";
  String V_SETTING_SYNC_OPTIONS = "settingSyncOptions";
  String V_ASSETS = "assets";
  String V_MANAGED_VARIABLES = "managedVariables";
  String V_MANAGED_FUNCTIONS = "managedFunctions";
  String V_MANAGED_EVENTS = "managedEvents";
  
  String V_CACHE = "cache";
  
  String V_SETTINGS_STATUS = "settingsStatus";
  String V_VARIABLES_CACHE = "variablesCache";
  String V_FUNCTIONS_CACHE = "functionsCache";
  String V_EVENTS_CACHE = "eventsCache";

  String F_RESET = "reset";
  String F_SYNCHRONIZE = "synchronize";
  
  String E_SYNCHRONIZED = "synchronized";
  String E_FUNCTION_CALLED = "functionCalled";
  
  String A_MANAGE = "manage";
  String A_RESET = "reset";
  String A_SYNCHRONIZE = "synchronize";
  String A_SETUP = "setup";
  String A_EDIT_VARIABLE_SYNC_OPTIONS = "editVariableSyncOptions";
  
  String VF_STATUS_EXPRESSIONS_EXPRESSION = "expression";
  String VF_GENERIC_PROPERTIES_TYPE = "type";
  String VF_STATUS_STATUS = "status";
  String VF_STATUS_COLOR = "color";
  String VF_STATUS_DRIVER = "driver";
  String VF_STATUS_DRIVER_STATUS = "driverStatus";
  String VF_STATUS_CONNECTION_STATUS = "connectionStatus";
  String VF_STATUS_SYNC_STATUS = "syncStatus";
  String VF_STATUS_SYNC_END_TIME = "syncEndTime";
  String VF_STATUS_SYNC_DETAILS = "syncDetails";
  String VF_STATUS_SYNC_FILL_RATE = "syncFillRate";
  String VF_STATUS_SYNC_LOG_START_TIME = "syncLogStartTime";
  String VF_STATUS_SYNC_LOG_DURATION = "syncLogDuration";
  String VF_STATUS_SYNC_LOG_CONNECT_ONLY = "syncLogConnectOnly";
  String VF_STATUS_SYNC_LOG_READ_METADATA = "syncLogReadMetadata";
  String VF_STATUS_SYNC_LOG_STATUS = "syncLogStatus";
  String VF_STATUS_SYNC_LOG_VARIABLES = "syncLogVariables";
  String VF_STATUS_SYNC_LOG_VARIABLE_NAME = "syncLogVariableName";
  String VF_STATUS_STAT_VARIABLE_READS = "statVariableReads";
  String VF_STATUS_STAT_VARIABLE_WRITES = "statVariableWrites";
  String VF_STATUS_STAT_FUNCTION_CALLS = "statFunctionCalls";
  String VF_STATUS_STAT_EVENTS = "statEvents";

  String VF_CACHE_TYPE = "type";
  String VF_CACHE_SYNC_TIME = "syncTime";

  String FIF_SYNCHRONIZE_VARIABLE = "variable";

  String EF_FUNCTION_CALLED_FUNCTION = "function";
  String EF_FUNCTION_CALLED_INPUT = "input";
  String EF_FUNCTION_CALLED_OUTPUT = "output";
  String EF_FUNCTION_CALLED_USER = "user";

}
