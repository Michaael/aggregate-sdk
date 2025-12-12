package com.tibbo.aggregate.common.server;

public interface ServerContextConstants
{
  public static final String V_VISIBLE_INFO = "visibleInfo";
  public static final String V_VISIBLE_CHILDREN = "visibleChildren";
  public static final String V_CONTEXT_STATUS = "contextStatus";
  public static final String V_CONTEXT_STATISTICS = "contextStatistics";
  public static final String V_PERMS = "perms";
  public static final String V_ACTIVE_ALERTS = "activeAlerts";
  public static final String V_GROUP_MEMBERSHIP = "groupMembership";
  public static final String V_APPLICATION_MEMBERSHIP = "applicationMembership";

  public static final String F_REORDER = "reorder";
  public static final String F_INIT_ACTION = "initAction";
  public static final String F_STEP_ACTION = "stepAction";
  public static final String F_RAW_STATISTICS_SPECIFIC = "rawStatisticsSpecific";
  public static final String F_FILTERED_VISIBLE_CHILDREN = "filteredVisibleChildren";
  
  public static final String E_VISIBLE_INFO_CHANGED = "visibleInfoChanged";
  public static final String E_VISIBLE_CHILD_REMOVED = "visibleChildRemoved";
  public static final String E_VISIBLE_CHILD_ADDED = "visibleChildAdded";
  public static final String E_CONTEXT_STATUS_CHANGED = "contextStatusChanged";
  public static final String E_MEMBER_COUNT_CHANGED = "memberCountChanged";
  public static final String E_EVALUATION = "evaluation";
  public static final String E_EVALUATION_ERROR = "evaluationError";
  
  public static final String A_PERMS = "perms";
  public static final String A_EDIT_CUSTOM_PROPERTIES = "editCustomProperties";
  public static final String A_REPLICATE = "replicate";
  
  public static final String VF_VISIBLE_INFO_DYNAMIC = "dynamic";
  public static final String VF_VISIBLE_INFO_CHILDREN_REORDERABLE = "childrenReorderable";
  public static final String VF_VISIBLE_INFO_EXPANDED = "expanded";
  public static final String VF_VISIBLE_INFO_OWNER = "owner";
  public static final String VF_VISIBLE_INFO_MEMBER_COUNT = "memberCount";
  public static final String VF_VISIBLE_CHILDREN_PATH = "path";
  public static final String VF_CONTEXT_STATUS_STATUS = "status";
  public static final String VF_CONTEXT_STATUS_COMMENT = "comment";
  public static final String VF_PERMS_TYPE = "type";
  public static final String VF_PERMS_USER = "user";
  public static final String VF_GROUP_MEMBERSHIP_GROUP = "group";
  public static final String VF_GROUP_MEMBERSHIP_LOCATION = "location";
  public static final String VF_APPLICATION = "application";
  public static final String VF_ACTIVE_ALERTS_LOCATION = "location";
  public static final String VF_ACTIVE_ALERTS_EVENT = "event";
  public static final String VF_ACTIVE_ALERTS_ALERT = "alert";
  public static final String VF_ACTIVE_ALERTS_TYPE = "type";
  public static final String VF_ACTIVE_ALERTS_TIME = "time";
  public static final String VF_ACTIVE_ALERTS_LEVEL = "level";
  public static final String VF_ACTIVE_ALERTS_CAUSE = "cause";
  public static final String VF_ACTIVE_ALERTS_MESSAGE = "message";
  public static final String VF_ACTIVE_ALERTS_TRIGGER = "trigger";
  public static final String VF_ACTIVE_ALERTS_DATA = "data";
  public static final String VF_ACTIVE_ALERTS_ACKNOWLEDGEMENTS = "acknowledgements";
  public static final String VF_ACTIVE_ALERTS_ENRICHMENTS = "enrichments";
  
  public static final String FIF_INIT_ACTION_INPUT_DATA = "inputData";
  public static final String FIF_INIT_ACTION_INITIAL_PARAMETERS = "initialParameters";
  public static final String FIF_INIT_ACTION_ACTION_NAME = "actionName";
  public static final String FIF_INIT_ACTION_EXECUTION_MODE = "executionMode";
  public static final String FIF_INIT_ACTION_ACTION_ID = "actionId";
  public static final String FIF_STEP_ACTION_ACTION_RESPONSE = "actionResponse";
  public static final String FIF_STEP_ACTION_ACTION_ID = "actionId";
  public static final String FIF_REORDER_INDEX = "index";
  public static final String FIF_REORDER_CHILD = "child";
  
  public static final String FOF_INIT_ACTION_ACTION_ID = "actionId";
  public static final String FOF_STEP_ACTION_ACTION_COMMAND = "actionCommand";
  
  public static final String FIF_RAW_STATISTICS_SPECIFIC_CHANNEL = "channel";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_KEY = "key";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_FROM_DATE = "fromDate";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_TO_DATE = "toDate";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_GROUPING = "grouping";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_AGGREGATION = "aggregation";
  public static final String FIF_RAW_STATISTICS_SPECIFIC_CHANNEL_TYPE = "channelType";
  
  public static final String FOF_RAW_STATISTICS_SPECIFIC_DATE = "date";
  public static final String FOF_RAW_STATISTICS_SPECIFIC_VALUE = "value";
  
  public static final String EF_CONTEXT_STATUS_CHANGED_STATUS = "status";
  public static final String EF_CONTEXT_STATUS_CHANGED_COMMENT = "comment";
  public static final String EF_CONTEXT_STATUS_CHANGED_OLD_STATUS = "oldStatus";
  public static final String EF_VISIBLE_CHILD_ADDED_PATH = "path";
  public static final String EF_VISIBLE_CHILD_REMOVED_PATH = "path";
  
  public static final String EF_EVALUATION_EXPRESSION = "expression";
  public static final String EF_EVALUATION_HOLDER = "holder";
  public static final String EF_EVALUATION_RESULT = "result";
  public static final String EF_EVALUATION_DETAILS = "details";
  public static final String EF_EVALUATION_DEFAULT_CONTEXT = "defaultContext";
  public static final String EF_EVALUATION_DEFAULT_TABLE = "defaultTable";
  public static final String EF_EVALUATION_DEFAULT_ROW = "defaultRow";
  
  public static final String EF_EVALUATION_ERROR_EXPRESSION = "expression";
  public static final String EF_EVALUATION_ERROR_HOLDER = "holder";
  public static final String EF_EVALUATION_ERROR_MESSAGE = "message";
  public static final String EF_EVALUATION_ERROR_DETAILS = "details";
  public static final String EF_EVALUATION_ERROR_DEFAULT_CONTEXT = "defaultContext";
  public static final String EF_EVALUATION_ERROR_DEFAULT_TABLE = "defaultTable";
  public static final String EF_EVALUATION_ERROR_DEFAULT_ROW = "defaultRow";
  
}
