package com.tibbo.aggregate.common.server;

public interface ModelContextConstants
{
  int STATUS_DISABLED = 10;
  
  int STORAGE_DATABASE = 0;
  int STORAGE_MEMORY = 1;
  
  int FUNCTION_TYPE_JAVA = 0;
  int FUNCTION_TYPE_EXPRESSION = 1;
  int FUNCTION_TYPE_QUERY = 2;
  
  String V_MODEL_VARIABLES = "modelVariables";
  String V_MODEL_FUNCTIONS = "modelFunctions";
  String V_MODEL_EVENTS = "modelEvents";
  String V_RULE_SETS = "ruleSets";
  String V_BINDINGS = "bindings";
  String V_THREAD_POOL_STATUS = "threadPoolStatus";
  String V_THREAD_POOL_REJECTED_COUNT = "rejectedCount";
  
  String E_BINDING_EXECUTION = "bindingExecution";
  
  String A_MOVE_TO_CONTAINER = "moveToContainer";
  String F_MOVE_TO_CONTAINER = A_MOVE_TO_CONTAINER;
  
  String FIF_CONTAINER_PATH = "containerPath";
  
  String FIELD_VD_READ_PERMISSIONS = "readPermissions";
  String FIELD_VD_WRITE_PERMISSIONS = "writePermissions";
  String FIELD_VD_STORAGE_MODE = "storageMode";
  String FIELD_VD_HISTORY_RATE = "historyRate";
  String FIELD_VD_UPDATE_HISTORY_STORAGE_TIME = "updateHistoryStorageTime";
  String FIELD_FD_TYPE = "type";
  String FIELD_FD_IMPLEMENTATION = "implementation";
  
  String OLD_FIELD_FD_EXPRESSION = "expresion";
  String FIELD_FD_EXPRESSION = "expression";
  
  String FIELD_FD_QUERY = "query";
  String FIELD_FD_PLUGIN = "plugin";
  String FIELD_ED_PERMISSIONS = "permissions";
  String FIELD_ED_FIRE_PERMISSIONS = "firePermissions";
  String FIELD_ED_HISTORY_STORAGE_TIME = "historyStorageTime";
}
