package com.tibbo.aggregate.common.server;

public interface RootContextConstants
{
  int PROLONG_LEASED_LICENSE_STATUS_DECLINED = 0;
  int PROLONG_LEASED_LICENSE_STATUS_NEW_LICENSE_NEEDED = 1;
  int PROLONG_LEASED_LICENSE_STATUS_PROLONGED = 2;
  
  public static final String V_VERSION = "version";
  public static final String V_ENVIRONMENT = "environment";
  public static final String V_SYSINFO = "sysinfo";
  public static final String V_EXPRESSIONS_STATISTIC = "expressionsStatistic";
  public static final String V_CONTEXT_INFO = "contextInfo";
  public static final String V_DEVICE_STATISTICS = "deviceStatistics";
  public static final String V_PLUGINS = "plugins";
  public static final String V_MODULES = "modules";
  public static final String V_LICENSE = "license";
  public static final String V_STATUS = "status";
  
  public static final String V_STORAGE_STATISTICS_CONFIGURATION = "storageStatisticsConfiguration";
  public static final String V_STORAGE_STATISTICS_EVENTS = "storageStatisticsEvents";
  public static final String V_STORAGE_STATISTICS_BINARY_DATA = "storageStatisticsBinaryData";
  public static final String V_RELATIONAL_DATABASE = "relationalDatabase";
  public static final String V_RELATIONAL_DATABASE_TABLES = "relationalDatabaseTables";
  public static final String V_RELATIONAL_DATABASE_CONNECTIONS = "relationalDatabaseConnections";
  public static final String V_RELATIONAL_DATABASE_CLUSTER = "relationalDatabaseCluster";
  public static final String V_CONNECTIONS = "connections";
  public static final String V_THREADS = "threads";
  public static final String V_THREAD_STATISTICS = "threadStatistics";
  public static final String V_POOLS = "pools";
  public static final String V_CLUSTER = "cluster";
  
  public static final String V_EVENT_PROCESSING_DETAILS = "eventProcessingDetails";
  public static final String V_EVENT_RULE_STATISTICS = "eventRuleStatistics";
  public static final String V_EVENT_QUEUE_STATISTICS = "eventQueueStatistics";
  public static final String V_EVENT_STATISTICS = "eventStatistics";
  public static final String V_VARIABLE_STATISTICS = "variableStatistics";
  public static final String V_MEMORY_CLEARING = "memoryClearing";
  public static final String V_SESSION_VARIABLE = "sessionVariable";
  public static final String V_SESSION_VALUE = "sessionValue";
  public static final String V_SESSION_OLD_VALUE = "sessionOldValue";
  public static final String V_SESSION_NEW_VALUE = "sessionNewValue";
  
  public static final String V_SESSION_DEFAULT_VALUE = "value";
  public static final String V_SESSION_USER_NAME = "username";
  public static final String V_SESSION_LOGIN = "login";
  public static final String V_SESSION_TYPE = "type";
  public static final String V_SESSION_PROTOCOL = "protocol";
  public static final String V_SESSION_LOGGER = "sessionLogger";
  public static final String V_SESSION_COOKIES = "cookies";
  public static final String V_SESSION_HEADERS = "headers";
  public static final String V_VARIABLE_VALIDATORS = "variableValidators";
  
  public static final String V_EVENT_DELIVERY_FAILURE_MESSAGE = "message";
  
  public static final String V_LICENSES = "licenses";
  public static final String V_LICENSE_STATUS = "status";
  public static final String V_LICENSE_ACTIVATION_KEY = "activationKey";
  public static final String V_LICENSE_FILE = "licenseFile";
  public static final String V_LICENSE_DETAILS = "licenseDetails";
  
  public static final String V_CURRENT_ENCRYPTION_KEY = "currentEncryptionKey";
  public static final String V_NEW_ENCRYPTION_KEY = "newEncryptionKey";
  public static final String V_ENCRYPTION_KEYS = "encryptionKeys";
  public static final String V_KEY_APPLICATION_STATUS = "keyApplicationStatus";
  public static final String V_KEY_APPLICATION_DATE = "keyApplicationDate";
  public static final String V_KEY_LAST_REQUEST_DATE = "keyLastRequestDate";
  public static final String V_ENCRYPTION_DRY_RUN = "encryptionDryRun";
  
  public static final String V_THREAD_POOLS_POOL_NAME = "poolName";
  public static final String V_THREAD_POOLS_ACTIVE_COUNT = "activeCount";
  public static final String V_THREAD_POOLS_COMPLETED_COUNT = "completedCount";
  public static final String V_THREAD_POOLS_TOTAL_COUNT = "totalCount";
  public static final String V_THREAD_POOLS_CORE_SIZE = "coreSize";
  public static final String V_THREAD_POOLS_LARGEST_SIZE = "largestSize";
  public static final String V_THREAD_POOLS_MAXIMUM_SIZE = "maximumSize";
  public static final String V_THREAD_POOLS_QUEUE_LENGTH = "queueLength";
  
  public static final String V_STATUS_DISK_UTILIZATION_NAME = "diskUtilizationName";
  public static final String V_STATUS_DISK_UTILIZATION_SPACE = "diskUtilizationSpace";
  
  public static final String F_REGISTER = "register";
  public static final String F_LOGIN = "login";
  public static final String F_LOGOUT = "logout";
  public static final String F_STOP = "stop";
  public static final String F_RESTART = "restart";
  public static final String F_UPGRADE = "upgrade";
  public static final String F_START_MAINTENANCE_MODE = "startMaintenanceMode";
  public static final String F_STOP_MAINTENANCE_MODE = "stopMaintenanceMode";
  public static final String F_CHANGE_PASSWORD = "changePassword";
  public static final String F_EXECUTE_QUERY = "executeQuery";
  public static final String F_EXECUTE_NATIVE_QUERY = "executeNativeQuery";
  public static final String F_ADSORB_QUERY_RESULTS = "adsorbQueryResults";
  public static final String F_GET_FORMAT = "getFormat";
  public static final String F_SESSION_GET = "sessionGet";
  public static final String F_SESSION_SET = "sessionSet";
  public static final String F_TERMINATE_CLIENT_CONNECTION = "terminateClientConnection";
  public static final String F_LOAD_ICON = "loadIcon";

  public static final String F_FILTERED_CONTEXT_LIST = "filteredContextList";
  public static final String F_GET_LICENSE = "getLicense";
  public static final String F_PROLONG_LEASED_LICENSE = "prolongLeasedLicense";
  public static final String F_REVOKE_LEASED_LICENSE = "revokeLeasedLicense";

  public static final String F_GET_ENCRYPTION_KEY = "getEncryptionKey";
  public static final String F_SET_ENCRYPTION_STATUS = "setEncryptionStatus";
  
  public static final String E_FEEDBACK = "feedback";
  public static final String E_CONTEXT_ADDED = "contextAdded";
  public static final String E_CONTEXT_REMOVED = "contextRemoved";
  public static final String E_CONTEXT_INFO_CHANGED = "contextInfoChanged";
  public static final String E_CONTEXT_CREATED = "contextCreated";
  public static final String E_CONTEXT_DESTROYED = "contextDestroyed";
  public static final String E_CONTEXT_RELOCATING = "contextRelocating";
  public static final String E_CONTEXT_RELOCATED = "contextRelocated";
  public static final String E_CONTEXT_ENTITY_ADDED = "contextEntityAdded";
  public static final String E_CONTEXT_ENTITY_REMOVED = "contextEntityRemoved";
  public static final String E_SERVER_STARTED = "serverStarted";
  public static final String E_SESSION_VARIABLE_UPDATED = "sessionVariableUpdated";
  public static final String E_EVENT_DELIVERY_FAILURE = "eventDeliveryFailure";
  
  public static final String E_LICENSE_REQUEST = "licenseRequest";
  public static final String E_LICENSE_REQUEST_STATUS = "licenseRequestStatus";
  public static final String E_LEASED_LICENSE_PROLONGATION = "leasedLicenseProlongation";
  public static final String E_LEASED_LICENSE_PROLONGATION_STATUS = "leasedLicenseProlongationStatus";
  public static final String E_LEASED_LICENSE_REVOCATION = "leasedLicenseRevocation";
  public static final String E_LEASED_LICENSE_REVOCATION_STATUS = "leasedLicenseRevocationStatus";
  
  public static final String E_ENCRYPTION_KEY_REQUEST = "licenseRequest";
  
  public static final String A_CREATE_RESOURCES = "createResources";
  public static final String A_DELETE_RESOURCES = "deleteResources";
  public static final String A_STOP_SERVER = "stop";
  public static final String A_RESTART_SERVER = "restart";
  public static final String A_START_MAINTENANCE_MODE = "startMaintenanceMode";
  public static final String A_STOP_MAINTENANCE_MODE = "stopMaintenanceMode";
  public static final String A_CHANGE_PASSWORD = "changePassword";
  public static final String A_DELETE_EVENTS = "deleteEvents";
  public static final String A_EVENT_HISTORY = "eventHistory";
  public static final String A_VARIABLE_HISTORY = "variableHistory";
  public static final String A_VIEW_STATISTICS = "viewStatistics";
  public static final String A_VIEW_RAW_STATISTICS = "viewRawStatistics";
  public static final String A_VIEW_CONTEXT_ENTITIES_STATISTICS = "viewContextEntitiesStatistics";
  public static final String A_VIEW_SERVER_INFO = "viewServerInfo";
  public static final String A_VIEW_CONTEXTS_INFO = "viewContextsInfo";
  public static final String A_VIEW_DATABASE_STATISTICS = "viewDatabaseStatistics";
  public static final String A_RUN_GARBAGE_COLLECTION = "runGarbageCollection";
  public static final String A_CONFIGURE_SERVER = "configureServer";
  public static final String A_EXECUTE = "execute";
  public static final String A_PURGE_STATISTICS = "purgeStatistics";
  public static final String A_FILL_STATISTICS_FROM_HISTORY = "fillStatisticsFromHistory";
  public static final String A_BROWSE = "browse";
  public static final String A_COMPARE = "compare";
  public static final String A_GENERATE_THREAD_DUMP = "generateThreadDump";
  public static final String A_GENERATE_HEAP_DUMP = "generateHeapDump";
  public static final String A_CONNECT_TO_STORE = "connectToStore";
  public static final String A_DELETE_MODULES = "deleteModules";
  public static final String A_OPEN_EXPRESSION_EDITOR = "openExpressionEditor";
  public static final String A_TERMINATE_CLIENT_CONNECTION = "terminateClientConnection";
  
  public static final String VF_VERSION_VERSION = "version";
  public static final String VF_VERSION_TIME = "time";
  public static final String VF_STATUS_NAME = "name";
  public static final String VF_STATUS_VERSION = "version";
  public static final String VF_STATUS_BUILD_NUMBER = "buildNumber";
  public static final String VF_STATUS_INSTALLATION_DATE = "installationDate";
  public static final String VF_STATUS_START_TIME = "startTime";
  public static final String VF_STATUS_STARTUP_DURATION = "startupDuration";
  public static final String VF_STATUS_UPTIME = "uptime";
  public static final String VF_STATUS_FREE_MEMORY = "freeMemory";
  public static final String VF_STATUS_MAX_MEMORY = "maxMemory";
  public static final String VF_STATUS_TOTAL_MEMORY = "totalMemory";
  public static final String VF_STATUS_MEMORY_USAGE = "memoryUsage";
  public static final String VF_STATUS_LOW_MEMORY_MODE = "lowMemoryMode";
  public static final String VF_STATUS_LAST_LOW_MEMORY_MODE_DURATION = "lastLowMemoryModeDuration";
  public static final String VF_STATUS_CURRENT_CACHE_ENTRY_COUNT = "currentCacheEntryCount";
  public static final String VF_STATUS_MAXIMUM_CACHE_ENTRY_COUNT = "maximumCacheEntryCount";
  public static final String VF_STATUS_ESTIMATED_CACHE_SIZE = "estimatedCacheSize";
  public static final String VF_STATUS_LAST_CLEARING_START = "lastClearingStart";
  public static final String VF_STATUS_LAST_CLEARING_DURATION = "lastClearingDuration";
  public static final String VF_STATUS_CLEARING_RUNS_COUNT = "clearingRunsCount";
  public static final String VF_STATUS_LAST_CLEANED_MEMORY_SIZE = "lastCleanedMemorySize";
  public static final String VF_STATUS_LAST_CLEANED_ENTITY_COUNT = "lastCleanedEntityCount";
  public static final String VF_STATUS_CPU_LOAD = "cpuLoad";
  public static final String VF_STATUS_CPU_LOAD_SYSTEM = "cpuLoadSystem";
  public static final String VF_STATUS_DISK_UTILIZATION = "diskUtilization";
  
  public static final String VF_EVENT_PROCESSING_DETAILS_QUEUE_LENGTH = "queueLength";
  public static final String VF_EVENT_PROCESSING_DETAILS_SCHEDULED = "scheduled";
  public static final String VF_EVENT_PROCESSING_DETAILS_PROCESSED = "processed";
  public static final String VF_PLUGINS_ID = "id";
  public static final String VF_PLUGINS_TYPE = "type";
  public static final String VF_PLUGINS_NAME = "name";
  public static final String VF_CONNECTIONS_USER = "user";
  public static final String VF_CONNECTIONS_LOGIN = "login";
  public static final String VF_CONNECTIONS_TYPE = "type";
  public static final String VF_CONNECTIONS_DATE = "date";
  public static final String VF_CONNECTIONS_ADDRESS = "address";
  public static final String VF_CONNECTIONS_EVENTS_QUEUED = "eventsQueued";
  public static final String VF_CONNECTIONS_EVENTS_DISCARDED = "eventsDiscarded";
  public static final String VF_CONNECTIONS_CONTEXT_LOCKS = "contextLocks";
  public static final String VF_THREADS_ID = "id";
  public static final String VF_THREADS_NAME = "name";
  public static final String VF_THREADS_GROUP = "group";
  public static final String VF_THREADS_PRIORITY = "priority";
  public static final String VF_THREADS_STATE = "state";
  public static final String VF_THREADS_DAEMON = "daemon";
  public static final String VF_THREADS_INTERRUPTED = "interrupted";
  public static final String VF_THREADS_CPU = "cpu";
  public static final String VF_THREADS_STACK = "stack";
  public static final String VF_THREAD_STATISTICS_LIVE = "live";
  public static final String VF_THREAD_STATISTICS_MAXIMUM_LIVE = "maximumLive";
  public static final String VF_THREAD_STATISTICS_TOTAL_STARTED = "totalStarted";
  public static final String VF_CLUSTER_ID = "id";
  public static final String VF_CLUSTER_ROLE = "role";
  public static final String VF_CLUSTER_TIME = "time";
  public static final String VF_DATABASE_MAX_QUERY_TIME = "maxQueryTime";
  public static final String VF_DATABASE_DELETED = "deleted";
  public static final String VF_DATABASE_INSERTED = "inserted";
  public static final String VF_DATABASE_UPDATED = "updated";
  public static final String VF_DATABASE_LOADED = "loaded";
  public static final String VF_DATABASE_TRANSACTIONS = "transactions";
  public static final String VF_DATABASE_QUERIES = "queries";
  public static final String VF_RELATIONAL_DATABASE_CONNECTIONS_UNCLOSED_CONNECTIONS = "unclosedConnections";
  public static final String VF_RELATIONAL_DATABASE_CONNECTIONS_BUSY_CONNECTIONS = "busyConnections";
  public static final String VF_RELATIONAL_DATABASE_CONNECTIONS_IDLE_CONNECTIONS = "idleConnections";
  public static final String VF_RELATIONAL_DATABASE_CONNECTIONS_CONNECTIONS = "connections";
  public static final String VF_DATABASE_CLUSTER_NODE = "node";
  public static final String VF_DATABASE_CLUSTER_ALIVE = "alive";
  public static final String VF_DATABASE_CLUSTER_ACTIVE = "active";
  public static final String VF_DATABASE_CLUSTER_SYNCHRONIZATION_DURATION = "synchronizationDuration";
  
  public static final String VF_SYSINFO_CONTEXT = "context";
  public static final String VF_SYSINFO_VARIABLE_COUNT = "variableCount";
  public static final String VF_SYSINFO_FUNCTION_COUNT = "functionCount";
  public static final String VF_SYSINFO_EVENT_COUNT = "eventCount";
  public static final String VF_SYSINFO_ACTION_COUNT = "actionCount";
  public static final String VF_SYSINFO_VARIABLES_READ = "variablesRead";
  public static final String VF_SYSINFO_VARIABLES_WRITTEN = "variablesWritten";
  public static final String VF_SYSINFO_FUNCTION_CALLED = "functionsCalled";
  public static final String VF_SYSINFO_EVENTS_FIRED = "eventsFired";
  public static final String VF_SYSINFO_EVENT_HANDLE_OFFERS = "eventHandleOffers";
  public static final String VF_SYSINFO_EVENT_HANDLE_EXECUTIONS = "eventHandleExecutions";
  public static final String VF_SYSINFO_EVENT_LISTENER_COUNT = "eventListenerCount";
  public static final String VF_SYSINFO_EVENT_QUEUES_LENGTH = "eventQueuesLength";
  public static final String VF_SYSINFO_MEMORY = "memory";
  
  public static final String VF_EXPRESSIONS_FUNCTION_NAME = "functionName";
  public static final String VF_EXPRESSIONS_FUNCTION_CALL_COUNT = "callCount";
  
  public static final String VF_EXPRESSIONS_PARSED = "parsed";
  public static final String VF_EXPRESSIONS_EVALUATED = "evaluated";
  public static final String VF_EXPRESSIONS_ERRORS_GENERATED = "errGenerated";
  public static final String VF_EXPRESSIONS_ERRORS_CAUGHT = "errCaught";
  public static final String VF_EXPRESSIONS_REFERENCES_PROCESSED = "refProcessed";
  public static final String VF_EXPRESSIONS_FUNCTIONS_CALLED = "funcCalled";
  
  public static final String VF_EVENT_QUEUE_STATISTICS_CONTEXT = "context";
  public static final String VF_EVENT_QUEUE_STATISTICS_EVENT_COUNT = "eventCount";
  
  public static final String VF_STATISTICS_VARIABLES_READ = "variablesRead";
  public static final String VF_STATISTICS_VARIABLES_WRITTEN = "variablesWritten";
  public static final String VF_STATISTICS_FUNCTION_CALLED = "functionsCalled";
  public static final String VF_STATISTICS_EVENTS_FIRED = "eventsFired";
  
  public static final String VF_EXTENDED_LICENSE_PLUGIN_GROUPS_CURRENT_VALUES = "currentValues";
  
  public static final String VF_SESSION_COOKIES_NAME = "name";
  public static final String VF_SESSION_COOKIES_VALUE = "value";

  public static final String VF_SESSION_HEADERS_NAME = "name";
  public static final String VF_SESSION_HEADERS_VALUE = "value";
  
  public static final String FIF_REGISTER_NAME = "name";
  public static final String FIF_REGISTER_PASSWORD = "password";
  public static final String FIF_REGISTER_PASSWORD_RE = "passwordRe";
  public static final String FIF_REGISTER_PERMISSIONS = "permissions";
  public static final String FIF_REGISTER_ADMIN_PERMISSIONS = "adminPermissions";
  public static final String FIF_REGISTER_GLOBAL_PERMISSIONS = "globalPermissions";
  public static final String FIF_LOGIN_USERNAME = "username";
  public static final String FIF_LOGIN_LOGIN = "login";
  public static final String FIF_LOGIN_PASSWORD = "password";
  public static final String FIF_LOGIN_CODE = "code";
  public static final String FIF_LOGIN_STATE = "state";
  public static final String FIF_LOGIN_PROVIDER = "provider";
  public static final String FIF_LOGIN_COUNT_ATTEMPTS = "countAttempts";
  public static final String FIF_LOGIN_TOKEN = "token";
  public static final String FIF_RESTART_INSTANTLY = "instantly";
  public static final String FOF_LOGIN_USERNAME = "username";
  public static final String FOF_LOGIN_LOGIN = "login";
  public static final String FIF_RESTART_ISTANTLY = "instantly";
  public static final String FIF_RESTART_DELAY = "delay";
  public static final String FIF_RESTART_REASON = "reason";
  public static final String FIF_STOP_INSTANTLY = "instantly";
  public static final String FIF_STOP_DELAY = "delay";
  public static final String FIF_STOP_REASON = "reason";
  public static final String FIF_EXECUTE_NATIVE_QUERY_UPDATE = "update";
  public static final String FIF_EXECUTE_NATIVE_QUERY_QUERY = "query";
  public static final String FIF_EXECUTE_QUERY_QUERY = "query";
  public static final String FIF_CHANGE_PASSWORD_OLD_PASSWORD = "oldPassword";
  public static final String FIF_CHANGE_PASSWORD_NEW_PASSWORD = "newPassword";
  public static final String FIF_CHANGE_PASSWORD_REPEAT_PASSWORD = "repeatPassword";
  public static final String FIF_GET_FORMAT_ID = "id";
  public static final String FIF_LOAD_ICON_CONTEXT = "context";
  public static final String FIF_LOAD_ICON_SCOPE = "scope";
  
  public static final String FOF_GET_FORMAT_DATA = "data";
  public static final String FOF_PROLONG_LEASED_LICENSE_STATUS = "status";

  public static final String FOF_LOAD_ICON_ICON = "icon";
  
  public static final String EF_CONTEXT_ADDED_CONTEXT = "context";
  public static final String EF_CONTEXT_REMOVED_CONTEXT = "context";
  public static final String EF_CONTEXT_CREATED_CONTEXT = "context";
  public static final String EF_CONTEXT_DESTROYED_CONTEXT = "context";
  public static final String EF_CONTEXT_INFO_CHANGED_CONTEXT = "context";
  public static final String EF_FEEDBACK_MESSAGE = "message";
  public static final String EF_CONTEXT_RELOCATING_OLD_PATH = "oldPath";
  public static final String EF_CONTEXT_RELOCATING_NEW_PATH = "newPath";
  public static final String EF_CONTEXT_RELOCATED_OLD_PATH = "oldPath";
  public static final String EF_CONTEXT_RELOCATED_NEW_PATH = "newPath";
  public static final String EF_CONTEXT_ENTITY_ADDED_CONTEXT = "context";
  public static final String EF_CONTEXT_ENTITY_ADDED_ENTITY = "entity";
  public static final String EF_CONTEXT_ENTITY_ADDED_ENTITY_TYPE = "entityType";
  public static final String EF_CONTEXT_ENTITY_REMOVED_CONTEXT = "context";
  public static final String EF_CONTEXT_ENTITY_REMOVED_ENTITY = "entity";
  public static final String EF_CONTEXT_ENTITY_REMOVED_ENTITY_TYPE = "entityType";
  public static final String EF_STARTUP_DURATION = "startupDuration";
}
