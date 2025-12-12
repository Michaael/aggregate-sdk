package com.tibbo.aggregate.common.server;

public interface ClassContextConstants
{
  public static final String MANY_TO_MANY_RELATION_LEFT = "L";
  public static final String MANY_TO_MANY_RELATION_RIGHT = "R";
  
  public static final String V_CLASSES = "classes";
  public static final String V_FIELDS = "fields";
  public static final String V_BINDINGS = "bindings";
  public static final String V_MANY_TO_MANY_RELATIONS = "manyToManyRelations";
  public static final String V_VIEWS = "views";
  public static final String V_PERMISSIONS = "permissions";
  public static final String V_LIFECYCLES = "lifecycles";
  
  public static final String F_LIFE_CYCLES = "lifeCycles";
  public static final String F_TRANSITIONS = "transitions";
  public static final String F_CURRENT_TRANSITIONS = "currentTransitions";
  public static final String F_PROCESS_BINDINGS = "processBindings";
  public static final String F_UPDATE_VIEW_BINDINGS = "updateViewBindings";
  public static final String F_EXECUTE_TRANSACTION = "executeTransaction";
  public static final String F_PERMISSION_FIELDS = "permissionFields";
  public static final String F_PREPARE_FILTER = "prepareFilter";

  public static final String A_STATE_TRANSITION = "stateTransition";
  
  public static final String VF_FIELDS_PRIMARY_KEY = "primaryKey";
  public static final String VF_FIELDS_LENGTH = "length";
  
  public static final String VF_MANY_TO_MANY_RELATIONS_NAME = "name";
  public static final String VF_MANY_TO_MANY_RELATIONS_DESCRIPTION = "description";
  public static final String VF_MANY_TO_MANY_RELATIONS_RELATED_CLASS = "relatedClass";
  public static final String VF_MANY_TO_MANY_RELATIONS_CASCADE_DELETE = "cascadeDelete";
  public static final String VF_MANY_TO_MANY_RELATIONS_ORIGIN = "origin";
  
  public static final String VF_LIFECYCLES_NAME = "name";
  public static final String VF_LIFECYCLES_DESCRIPTION = "description";
  public static final String VF_LIFECYCLES_STATES = "states";
  public static final String VF_LIFECYCLES_STATE_TRANSITIONS = "stateTransitions";
  
  public static final String VF_LIFECYCLES_STATES_NAME = "name";
  public static final String VF_LIFECYCLES_STATES_DESCRIPTION = "description";
  public static final String VF_LIFECYCLES_STATES_ENTRANCE_CONDITION = "entranceCondition";
  public static final String VF_LIFECYCLES_STATES_EXIT_CONDITION = "exitCondition";
  public static final String VF_LIFECYCLES_STATES_ENTRANCE_BINDINGS = "entranceBindings";
  public static final String VF_LIFECYCLES_STATES_EXIT_BINDINGS = "exitBindings";
  
  public static final String VF_LIFECYCLES_STATES_FILL_SETTING_FIELD_NAME = "fieldName";
  public static final String VF_LIFECYCLES_STATES_FILL_SETTING_ON_EXIT = "fillOnExit";
  public static final String VF_LIFECYCLES_STATES_FILL_SETTING_ON_ENTRANCE = "fillOnEntrance";
  
  public static final String VF_LIFECYCLES_STATES_BINDING_TARGET = "target";
  public static final String VF_LIFECYCLES_STATES_BINDING_EXPRESSION = "expression";
  
  public static final String VF_LIFECYCLES_STATES_FILL_SETTINGS = "fillSettings";
  
  public static final String VF_VIEWS_NAME = "name";
  public static final String VF_VIEWS_DESCRIPTION = "description";
  public static final String VF_VIEWS_FIELDS = "fields";
  public static final String VF_VIEWS_RELATIONS = "relations";
  public static final String VF_VIEWS_FILTER = "filter";
  public static final String VF_VIEWS_SORTING = "sorting";
  public static final String VF_VIEWS_BINDINGS = "bindings";
  
  public static final String VF_PERMISSION_USERS = "userContextMask";
  public static final String VF_PERMISSION_FIELDS = "fields";
  public static final String VF_PERMISSION_INSTANCES = "instancesFilter";

  public static final String VF_PERMISSION_WRITE = "write";
  public static final String VF_PERMISSION_READ = "read";

  public static final String VF_FIELD_INHERITED_TABLE_NAME = "inheritedTableName";
  public static final String FIF_LIFE_CYCLES_CLASS_NAME = "className";
  public static final String FIF_LIFE_CYCLES_OLD_VALUE = "oldValue";

  public static final String FIF_TRANSITIONS_STATE = "state";
  public static final String FIF_TRANSITIONS_OLD_VALUE = "oldValue";

  public static final String FIF_CURRENT_TRANSITIONS_CLASS_NAME = "className";
  public static final String FIF_CURRENT_TRANSITIONS_CYCLE_NAME = "cycleName";
  public static final String FIF_CURRENT_TRANSITIONS_DATA = "data";
  public static final String FIF_CURRENT_TRANSITIONS_DASHBOARD_CONTEXT = "dashboardContext";
  public static final String FIF_CURRENT_TRANSITIONS_INSTANCE_ID = "instanceId";

  public static final String FIF_PROCESS_BINGINGS_FILTER = "filter";

  public static final String FIF_UPDATE_VIEW_BINDINGS_BINDINGS = "bindings";
  public static final String FIF_UPDATE_VIEW_BINDINGS_VIEW_COLUMNS = "viewColumns";

  public static final String FIF_EXECUTE_TRANSACTION_TRANSACTION = "transaction";
  public static final String FIF_EXECUTE_TRANSACTION_ON_SUCCESS = "onSuccess";
  public static final String FIF_EXECUTE_TRANSACTION_ON_FAILURE = "onFailure";

  public static final String FOF_CREATE_TABLE_STATE_TRANSITIONS_STATE_NAME = "stateName";

  public static final String FIELD_TRANSACTION_FORMAT_CONTEXT = "context";
  public static final String FIELD_TRANSACTION_FORMAT_FUNCTION = "function";
  public static final String FIELD_TRANSACTION_FORMAT_PARAMETERS = "parameters";

}
