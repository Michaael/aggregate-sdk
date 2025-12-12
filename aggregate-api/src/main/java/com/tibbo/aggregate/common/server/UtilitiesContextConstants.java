package com.tibbo.aggregate.common.server;

public interface UtilitiesContextConstants
{
  int THREAD_INFO_MAX_DEPTH = 100;
  
  String V_GENERATE_THREAD_DUMP_RESULT = "threadDumpResult";
  String V_GENERATE_HEAP_DUMP_RESULT_PATH = "heapDumpResultPath";
  String V_THREAD_DUMP_PREFIX = "thread_dump_";
  String V_HEAP_DUMP_PREFIX = "heap_dump_";
  String V_HEAP_DUMP_EXTENSION = "hprof";
  String V_HEAP_DUMP_DIR = "dumps";
  String V_AGG_DUMP_EXTENSION_TXT = "txt";
  String V_GENERATE_THREAD_DUMP_DAEMON = "daemon";
  String V_GENERATE_THREAD_DUMP_PRIORITY = "prio";
  String V_GENERATE_THREAD_DUMP_THREAD_ID = "tid";
  String V_GENERATE_THREAD_DUMP_THREAD_STATE = "java.lang.Thread.State";
  String V_GENERATE_THREAD_DUMP_THREAD_BLOCKED_ON = "blocked on";
  String V_GENERATE_THREAD_DUMP_THREAD_WAITING_ON = "waiting on";
  String V_GENERATE_THREAD_DUMP_THREAD_HEX_PREFIX = "0x";
  String V_GENERATE_THREAD_DUMP_LOCKED_MONITOR = "locked";
  String V_GET_MODEL_POOLS_CONTEXT_NAME = "contextName";
  String V_GET_MODEL_POOLS_PROPERTY_ENABLED = "enabled";
  
  String F_DEBUG = "debug";
  String F_VARIABLE_ACTIONS = "variableActions";
  String F_EVENT_ACTIONS = "eventActions";
  String F_INIT_ACTIONS = "initActions";
  String F_GET_DATA = "getData";
  String F_CHART_DATA = "chartData";
  String F_ACTIONS_BY_MASK = "actionsByMask";
  String F_EVENTS_BY_MASK = "eventsByMask";
  String F_VARIABLES_BY_MASK = "variablesByMask";
  String F_FUNCTIONS_BY_MASK = "functionsByMask";
  String F_ENTITIES_BY_MASK = "entitiesByMask";
  String F_EVENT_FIELDS = "eventFields";
  String F_VARIABLE_FIELDS = "variableFields";
  String F_VARIABLE_HISTORY = "variableHistory";
  String F_STATISTICS = "statistics";
  String F_CONTEXT_ENTITIES_STATISTICS = "contextEntitiesStatistics";
  String F_RAW_STATISTICS = "rawStatistics";
  String F_TOPOLOGY = "topology";
  String F_DELETE_STATISTICS = "deleteStatistics";
  String F_FILL_STATISTICS_FROM_HISTORY = "fillStatisticsFromHistory";
  String F_ACTION_EXECUTION_PARAMETERS = "actionExecutionParameters";
  String F_FUNCTION_EXECUTION_PARAMETERS = "functionExecutionParameters";
  String F_ACTION_EXECUTION_PARAMETERS_REFERENCE = "actionExecutionParametersReference";
  String F_FUNCTION_ACTION_EXECUTION_PARAMETERS = "functionActionExecutionParameters";
  String F_USERS = "users";
  String F_VARIABLE_INFO = "variableInfo";
  String F_EVENT_INFO = "eventInfo";
  String F_EXECUTE = "execute";
  String F_LIST_VARIABLES = "listVariables";
  String F_SELECTION_VALUES = "selectionValues";
  String F_SUMMARY = "summary";
  String F_FIRE_BACKDATED_EVENT = "fireBackdatedEvent";
  String F_EDITORS = "editors";
  String F_EDITOR_OPTIONS = "editorOptions";
  String F_EXPRESSION = "expression";
  String F_RUN_GARBAGE_COLLECTION = "runGarbageCollection";
  String F_GENERATE_THREAD_DUMP = "generateThreadDump";
  String F_GENERATE_HEAP_DUMP = "generateHeapDump";
  String F_GET_MODEL_POOLS = "getAllModelPools";
  String F_EXECUTE_BINDINGS = "executeBindings";
  String F_CONTEXT_AVAILABLE = "contextAvailable";
  String F_LAYOUTS_BY_DESTINATION = "layoutsByDestination";
  String F_AGENT_AUTHENTICATION_PLUGINS = "agentAuthenticationPlugins";
  String F_COMPONENT_LOCATION_FOR_GRID = "componentLocationForGrid";
  String F_COMPONENT_LOCATION_FOR_ABSOLUTE = "componentLocationForAbsolute";
  String F_COMPONENT_LOCATION_FOR_DOCKABLE = "componentLocationForDockable";
  String F_MIGRATE_RESOURCES = "migrateResources";
  String F_GET_SOLUTIONS = "getSolutions";
  String F_GET_MODULES = "getModules";
  String F_INSTALL_MODULES = "installModules";
  String F_UNINSTALL_MODULES = "uninstallModules";
  
  String F_REDUCE = "reduce";
  String F_EVALUATE_EXPRESSION = "evaluateExpression";
  String E_ACTION_FINISHED = "actionFinished";
  String EF_ACTION_FINISHED_ACTION_ID = "actionId";
  String EF_ACTION_FINISHED_ACTION_RESULT = "actionResult";
  
  String A_VARIABLE_HISTORY = "variableHistory";
  String A_VARIABLE_INFO = "variableInfo";
  String A_CONFIGURE_VARIABLE_AGGREGATION = "configureAggregation";
  String A_EVENT_INFO = "eventInfo";
  String A_SHOW_DATA = "showData";
  String A_SHOW_REPORT = "showReport";
  
  String FIF_INIT_ACTIONS_CONTEXT = "context";
  String FIF_INIT_ACTIONS_ACTION_NAME = ServerContextConstants.FIF_INIT_ACTION_ACTION_NAME;
  String FIF_INIT_ACTIONS_PARAMETERS = ServerContextConstants.FIF_INIT_ACTION_INITIAL_PARAMETERS;
  String FIF_GET_DATA_ID = "id";
  String FIF_TOPOLOGY_PROVIDER = "provider";
  String FIF_TOPOLOGY_CONTEXT_MODE = "contextMode";
  String FIF_TOPOLOGY_NODE_MASK = "nodeMask";
  String FIF_TOPOLOGY_TOPOLOGY_EXPRESSION = "topologyExpression";
  String FIF_TOPOLOGY_NODE_EXPRESSION = "nodeExpression";
  String FIF_TOPOLOGY_LINK_EXPRESSION = "linkExpression";
  String FIF_TOPOLOGY_LINK_ID_EXPRESSION = "linkIdExpression";
  String FIF_TOPOLOGY_NODE_ID_EXPRESSION = "nodeIdExpression";
  String FIF_TOPOLOGY_SOURCE_EXPRESSION = "sourceExpression";
  String FIF_TOPOLOGY_TARGET_EXPRESSION = "targetExpression";
  String FIF_TOPOLOGY_COLOR_EXPRESSION = "colorExpression";
  String FIF_TOPOLOGY_SELECTION_COLOR_EXPRESSION = "selectionColorExpression";
  String FIF_TOPOLOGY_TYPE_EXPRESSION = "typeExpression";
  String FIF_TOPOLOGY_INTERFACE_EXPRESSION = "interfaceExpression";
  String FIF_TOPOLOGY_DIRECTED_EXPRESSION = "directedExpression";
  String FIF_TOPOLOGY_WIDTH_EXPRESSION = "widthExpression";
  String FIF_TOPOLOGY_LINK_DESCRIPTION_EXPRESSION = "linkDescriptionExpression";
  String FIF_TOPOLOGY_LINK_COLOR_EXPRESSION = "linkColorExpression";
  String FIF_TOPOLOGY_LATITUDE_EXPRESSION = "latitudeExpression";
  String FIF_TOPOLOGY_LONGITUDE_EXPRESSION = "longitudeExpression";
  String FIF_TOPOLOGY_LABELS = "labels";
  String FIF_TOPOLOGY_IMAGE_EXPRESSION = "imageExpression";
  String FIF_TOPOLOGY_IMAGE_CUSTOM_PROPERTIES = "imageCustomProperties";
  String FIF_TOPOLOGY_CSS_CUSTOM_PROPERTY_EXPRESSION = "cssCustomPropertyExpression";
  String FIF_TOPOLOGY_TOOLTIP_EXPRESSION = "tooltipExpression";
  String FIF_TOPOLOGY_AZIMUTH_EXPRESSION = "azimuthExpression";
  String FIF_TOPOLOGY_RATIO_EXPRESSION = "ratioExpression";
  String FIF_TOPOLOGY_CUSTOM_LAYOUT_NODE_X_EXPRESSION = "customLayoutNodeXExpression";
  String FIF_TOPOLOGY_CUSTOM_LAYOUT_NODE_Y_EXPRESSION = "customLayoutNodeYExpression";
  String FIF_TOPOLOGY_PREFERRED_ACTION_NAME_EXPRESSION = "preferredActionNameExpression";
  String FIF_TOPOLOGY_SHOW_NEIGHBOURING_NODES = "showNeighbouringNodes";
  String FIF_DEFAUTLT_CONTEXT = "defaultContext";
  String FIF_GRAPH_DB_CONTEXT = "graphDbContext";
  String FIF_GRAPH_DB_QUERY = "graphDbQuery";
  String FIF_GRAPH_DB_DEPTH = "graphDbDepth";
  String FIF_GRAPH_DB_NODE_ID = "graphDbNodeID";
  String FIF_GRAPH_DB_SESSION_ID = "graphDbSessionID";
  
  String FIF_VARIABLES_BY_MASK_MASK = "mask";
  String FIF_VARIABLES_BY_MASK_GROUP = "group";
  String FIF_EXECUTE_COMMAND = "command";
  String FIF_EXECUTE_DIRECTORY = "directory";
  String FIF_EXECUTE_CHARSET = "charset";
  String FIF_EVENTS_BY_MASK_MASK = "mask";
  String FIF_EVENTS_BY_MASK_ALLOW_ALL = "allowAll";
  String FIF_ACTIONS_BY_MASK_MASK = "mask";
  String FIF_ACTIONS_BY_MASK_INCLUDE_NON_HEADLESS = "includeNonHeadless";
  String FIF_FUNCTIONS_BY_MASK_MASK = "mask";
  String FIF_ENTITIES_BY_MASK_MASK = "mask";
  String FIF_ENTITIES_BY_MASK_TYPE = "type";
  String FIF_ACTION_EXECUTION_PARAMETERS_MASK = "mask";
  String FIF_ACTION_EXECUTION_PARAMETERS_ACTION = "action";
  String FIF_ACTION_EXECUTION_PARAMETERS_ORIGINAL = "original";
  String FIF_ACTION_EXECUTION_PARAMETERS_INTERACTIVE = "interactive";
  String FIF_FUNCTION_EXECUTION_PARAMETERS_MASK = "mask";
  String FIF_FUNCTION_EXECUTION_PARAMETERS_FUNCTION = "function";
  String FIF_FUNCTION_EXECUTION_PARAMETERS_ORIGINAL = "original";
  String FIF_EVENT_FIELDS_MASK = "mask";
  String FIF_EVENT_FIELDS_EVENT = "event";
  String FIF_VARIABLE_FIELDS_MASK = "mask";
  String FIF_VARIABLE_FIELDS_VARIABLE = "variable";
  String FIF_VARIABLE_HISTORY_CONTEXT = "context";
  String FIF_VARIABLE_HISTORY_VARIABLE = "variable";
  String FIF_VARIABLE_HISTORY_FROM_DATE = "fromDate";
  String FIF_VARIABLE_HISTORY_TO_DATE = "toDate";
  String FIF_VARIABLE_HISTORY_DATA_AS_TABLE = "dataAsTable";
  String FIF_VARIABLE_HISTORY_LIMIT = "limit";
  String FIF_VARIABLE_HISTORY_SORT_ASCENDING = "sortAscending";
  String FIF_VARIABLE_INFO_CONTEXT = "context";
  String FIF_VARIABLE_INFO_VARIABLE = "variable";
  String FIF_RAW_STATISTICS_CONTEXT = "context";
  String FIF_RAW_STATISTICS_NAME = "name";
  String FIF_EVENT_INFO_CONTEXT = "context";
  String FIF_EVENT_INFO_EVENT = "event";
  String FIF_LIST_VARIABLES_GROUP = "group";
  String FIF_LIST_VARIABLES_MASK = "mask";
  String FOF_LIST_VARIABLES_CONTEXT = "context";
  String FOF_LIST_VARIABLES_VARIABLE = "variable";
  String FOF_LIST_VARIABLES_VALUE = "value";
  String FIF_SELECTION_VALUES_TABLE_EXPRESSION = "tableExpression";
  String FIF_SELECTION_VALUES_VALUE_EXPRESSION = "valueExpression";
  String FIF_SELECTION_VALUES_DESCRIPTION_EXPRESSSION = "descriptionExpresssion";
  String FIF_STATISTICS_MASK = "mask";
  String FIF_STATISTICS_CHANNEL = "channel";
  String FIF_STATISTICS_KEY = "key";
  String FIF_STATISTICS_PERIOD = "period";
  String FIF_STATISTICS_FULL = "full";
  String FIF_STATISTICS_AVERAGE = "average";
  String FIF_STATISTICS_MINIMUM = "minimum";
  String FIF_STATISTICS_MAXIMUM = "maximum";
  String FIF_STATISTICS_SUM = "sum";
  String FIF_STATISTICS_FIRST = "first";
  String FIF_STATISTICS_LAST = "last";
  String FIF_EXECUTE_BINDINGS_DATATABLE = "datatable";
  String FIF_CONTEXT_AVAILABLE_PATH = "path";
  
  String FOF_STATISTICS_CONTEXT = "context";
  String FOF_STATISTICS_START = "start";
  String FOF_STATISTICS_END = "end";
  String FOF_STATISTICS_KEY = "key";
  String FOF_STATISTICS_AVERAGE = "average";
  String FOF_STATISTICS_MINIMUM = "minimum";
  String FOF_STATISTICS_MAXIMUM = "maximum";
  String FOF_STATISTICS_SUM = "sum";
  String FOF_STATISTICS_FIRST = "first";
  String FOF_STATISTICS_LAST = "last";
  
  String FIF_CONTEXT_ENTITIES_STATISTICS_MASK = "mask";
  String FIF_CONTEXT_ENTITIES_STATISTICS_TYPE = "type";
  String FIF_CONTEXT_ENTITIES_STATISTICS_NAME = "name";
  
  String FOF_CONTEXT_ENTITIES_STATISTICS_CONTEXT = "context";
  String FOF_CONTEXT_ENTITIES_STATISTICS_NAME = "name";
  String FOF_CONTEXT_VARIABLES_STATISTICS_GET_COUNT = "getCount";
  String FOF_CONTEXT_VARIABLES_STATISTICS_SET_COUNT = "setCount";
  String FOF_CONTEXT_FUNCTIONS_STATISTICS_EXEC_COUNT = "execCount";
  String FOF_CONTEXT_EVENTS_STATISTICS_SUBSCRIBE_COUNT = "subscribeCount";
  String FOF_CONTEXT_EVENTS_STATISTICS_UNSUBSCRIBE_COUNT = "unsubscribeCount";
  String FOF_CONTEXT_EVENTS_STATISTICS_LISTENER_COUNT = "listenerCount";
  String FOF_CONTEXT_EVENTS_STATISTICS_INSTANCES_COUNT = "instancesCount";
  
  String FOF_INIT_ACTIONS_ACTION_ID = "actionId";
  String FOF_GET_DATA_DATA = "data";
  String FOF_FIRE_BACKDATED_EVENT_ID = "id";
  
  String FIF_FIRE_BACKDATED_EVENT_CONTEXT = "context";
  String FIF_FIRE_BACKDATED_EVENT_EVENT = "event";
  String FIF_FIRE_BACKDATED_EVENT_LEVEL = "level";
  String FIF_FIRE_BACKDATED_EVENT_CREATION_TIME = "creationTime";
  String FIF_FIRE_BACKDATED_EVENT_DATA = "data";
  
  String FIF_EDITORS_TYPE = "type";
  
  String FIF_EDITOR_OPTIONS_TYPE = "type";
  String FIF_EDITOR_OPTIONS_EDITOR = "editor";
  String FIF_EDITOR_OPTIONS_OPTIONS = "options";
  
  String FOF_TOPOLOGY_LINKS = "links";
  String FOF_TOPOLOGY_NODES = "nodes";
  String FOF_TOPOLOGY_SESSION_ID = "sessionID";
  
  String FOF_EXECUTE_BINDINGS_DATATABLE = "datatable";
  String FOF_EXECUTE_BINDINGS_RECORD_FORMATS = "recordFormats";
  String FOF_EXECUTE_BINDINGS_ERRORS = "errors";
  
  String FOF_CONTEXT_AVAILABLE_AVAILABLE = "available";
  
  String FOFT_AGENT_AUTHENTICATION_PLUGINS_PLUGIN_ID = "pluginId";
  String FOFT_AGENT_AUTHENTICATION_PLUGINS_PLUGIN_DESCRIPTION = "pluginDescription";
  
  String FIF_COMPONENT_LOCATION_PATH = "path";
  
  String FIF_COMPONENT_LOCATION_ROW = "row";
  String FIF_COMPONENT_LOCATION_COLUMN = "column";
  String FIF_COMPONENT_LOCATION_ROWSPAN = "rowSpan";
  String FIF_COMPONENT_LOCATION_COLUMNSPAN = "columnSpan";
  
  String FIF_COMPONENT_LOCATION_X = "x";
  String FIF_COMPONENT_LOCATION_Y = "y";
  String FIF_COMPONENT_LOCATION_WIDTH = "width";
  String FIF_COMPONENT_LOCATION_HEIGHT = "height";
  String FIF_COMPONENT_LOCATION_Z_INDEX = "zIndex";
  
  String FIF_COMPONENT_LOCATION_POSITION = "position";
  String FIF_COMPONENT_LOCATION_POSITION_ATTRIBUTE = "positionAttribute";
  String FIF_COMPONENT_LOCATION_TARGET_ID = "targetId";
  String FIF_COMPONENT_LOCATION_TAB_INDEX = "tabIndex";
  String FIF_COMPONENT_LOCATION_TAB_POSITION = "tabPosition";
  String FIF_COMPONENT_LOCATION_PREFERRED_WIDTH = "preferredWidth";
  String FIF_COMPONENT_LOCATION_PREFERRED_HEIGHT = "preferredHeight";
  String FIF_COMPONENT_LOCATION_MINIMUM_WIDTH = "minimumWidth";
  String FIF_COMPONENT_LOCATION_MINIMUM_HEIGHT = "minimumHeight";
  String FIF_COMPONENT_LOCATION_MOVABLE = "movable";
  String FIF_COMPONENT_LOCATION_CLOSABLE = "closable";
  String FIF_COMPONENT_LOCATION_RESIZABLE = "resizable";
  String FIF_COMPONENT_LOCATION_COLLAPSIBLE = "collapsible";
  String FIF_COMPONENT_LOCATION_MAXIMIZABLE = "maximizable";
  String FIF_COMPONENT_LOCATION_FLOATABLE = "floatable";
  String FIF_COMPONENT_LOCATION_COLLAPSED = "collapsed";
  String FIF_COMPONENT_LOCATION_SHOW_HEADER = "showHeader";
  String FIF_COMPONENT_LOCATION_DESCRIPTION = "description";
  String FIF_COMPONENT_LOCATION_ICON = "icon";
  String FOF_COMPONENT_LOCATION_LAYOUT = "layout";
  
  String FIF_MIGRATE_CONTEXT_PATH = "contextPath";
  
  String FIF_MIGRATE_RESOURCES_SERVER_ID = ConfigContextConstants.VF_GENERAL_SERVER_ID;
  
  String FOF_MIGRATE_STATUS = "status";
  
  String FIF_REDUCE_CONTEXT_PATH = "contextPath";
  String FIF_REDUCE_FUNCTION_NAME = "functionName";
  String FIF_REDUCE_INPUT_PARAMETERS = "inputParameters";
  String FIF_REDUCE_EXPRESSION = "reduceExpression";
  
  String FIF_GET_SOLUTIONS_STORE_ADDRESS = "storeAddress";
  String FIF_GET_MODULES_STORE_ADDRESS = "storeAddress";
  String FOF_GET_MODULES_IS_INSTALLED = "isInstalled";
  String FOF_INSTALL_MODULES_RESULT = "result";
  String FOF_INSTALL_MODULES_STATUS = "status";

  String FIF_EVALUATE_EXPRESSION_EXPRESSION = "expression";
  String FIF_EVALUATE_EXPRESSION_DEFAULT_CONTEXT = "defaultContext";
  String FIF_EVALUATE_EXPRESSION_DEFAULT_TABLE = "defaultTable";
  String FIF_EVALUATE_EXPRESSION_MUTE_ERRORS = "muteErrors";
  String FIF_EVALUATE_EXPRESSION_ORIGIN = "origin";
  String FOF_EVALUATE_EXPRESSION_RESULT = "result";
  String FOF_EVALUATE_EXPRESSION_DATA_TYPE = "resultDataType";
}
