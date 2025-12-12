package com.tibbo.aggregate.common.server;

public class ClusterCoordinatorContextConstants
{
  public static final String ID = "com.tibbo.linkserver.plugin.context.cluster-coordinator";
  
  public static final int SKIP = 0;
  public static final int REPLACE = 1;
  public static final int UPDATE = 2;
  
  public static final int CONTEXT_CREATED = 1;
  public static final int CONTEXT_DELETED = 2;
  public static final int CONTEXT_SKIPPED = 3;
  public static final int CONTEXT_REPLACED = 4;
  public static final int CONTEXT_UPDATED = 5;
  public static final int VARIABLE_SET = 6;
  
  public static final String ADDRESS = "address";
  public static final String PORT = "port";

  public static final String DEFAULT_STORAGE_PATH = "ignite";
  
  public static final String A_STATUS = "status";
  
  public static final String V_VALUE = "value";
  public static final String V_NAME = "name";
  public static final String V_ROUND_ROBIN = "roundRobin";
  
  public static final String FIELD_TARGET = "target";
  public static final String FIELD_SUCCESSFULLY = "successfully";
  public static final String FIELD_CONTEXT_TYPE = "contextType";
  public static final String FIELD_MODIFICATION_TYPE = "modificationType";
  public static final String FIELD_STATUS_OR_ERROR = "status";
  public static final String FIELD_ERROR = "error";
  
  public static final Integer V_NONE = 0;
  public static final Integer V_SESSION_RESOURCE = 1;
  public static final Integer V_TEMPLATE = 2;
  public static final String V_RESOURCE_TYPE = "resourceType";
  public static final String V_ENABLED = "enabled";
  
  public static final String V_KEY = "key";
  public static final String V_PATH = "path";
  public static final String V_NODE_UID = "nodeUID";
  
  public static final String V_COORDINATOR = "clusterCoordinator";
  public static final String V_PRIMARY_NODES = "primaryNodes";
  public static final String V_APPLICATION_SERVERS = "applicationServers";
  public static final String V_COMMON_CONTEXTS = "commonContexts";
  public static final String V_NODE_CONFIGURATION = "nodeConfiguration";
  public static final String V_ALLOCATION_RULES = "allocationRules";
  
  public static final String F_ADD_TENANT = "addTenant";
  public static final String F_REMOVE_TENANT = "removeTenant";
  public static final String F_ADD_RESOURCE = "addResource";
  public static final String F_ALLOCATE_NODE = "allocateNode";
  public static final String F_CHECK_RESOURCE_NAME_AVAILABILITY = "checkResourceNameAvailability";
  public static final String F_REMOVE_RESOURCE = "removeResource";
  public static final String F_GET_RESOURCES = "getResources";
  public static final String F_GET_PERMISSIONS = "getPermissions";
  public static final String F_REGISTER_APPLICATION = "registerApplication";
  public static final String F_UNREGISTER_APPLICATION = "unregisterApplication";
  public static final String F_UPDATE_APPLICATION = "updateApplication";
  public static final String F_DEPLOY_APPLICATION = "deployApplication";
  public static final String F_UNDEPLOY_APPLICATION = "undeployApplication";
  public static final String F_ADD_PRIMARY_NODE = "addPrimaryNode";
  public static final String F_REMOVE_PRIMARY_NODE = "removePrimaryNode";
  public static final String F_SWITCH_APPLICATION = "switchApplication";
  public static final String F_GET_CONFIGURATION = "getConfiguration";
  public static final String F_DROP_TABLE = "dropTable";
  public static final String F_MIGRATE_RESOURCES = "migrateResources";

  public static final String F_SCAN_RESOURCES = "scanResources";
  public static final String F_GET_NODE = "getNode";
  
  public static final String VF_COORDINATOR_ENABLED = "coordinatorEnabled";
  public static final String VF_COORDINATOR_NODE_NAME = "coordinatorNodeName";
  public static final String VF_COORDINATOR_STORAGE_PATH = "storagePath";
  public static final String VF_COORDINATOR_BACKUPS = "backups";
  public static final String VF_COORDINATOR_NODE_ADDRESSES = "coordinatorNodeAddresses";
  public static final String VF_COORDINATOR_NODE_ADDRESSES_ADDRESS = "coordinatorNodeAddress";
  
  public static final String VF_COORDINATOR_DATABASE_URL = "databaseURL";
  public static final String VF_COORDINATOR_JDBC_DRIVER_CLASS = "jdbcDriverClass";
  public static final String VF_COORDINATOR_POOL_MIN_SIZE = "poolMinSize";
  public static final String VF_COORDINATOR_POOL_MAX_SIZE = "poolMaxSize";
  public static final String VF_COORDINATOR_POOL_INCREMENT = "poolIncrement";
  public static final String VF_COORDINATOR_ADDITIONAL_CONNECTION_PROPS = "additionalConnectionProperties";
  public static final String VF_COORDINATOR_ADDITIONAL_CONNECTION_PROPS_PROPERTY = "property";
  public static final String VF_COORDINATOR_ADDITIONAL_CONNECTION_PROPS_VALUE = "value";
  
  public static final String VF_COORDINATOR_CACHE_PROPERTIES = "cacheProperties";
  public static final String VF_CACHE_TYPE = "cacheType";
  public static final String VF_CACHE_DESCRIPTION = "cacheDescription";
  public static final String VF_CACHE_TABLE = "cacheTable";
  public static final String VF_STORAGE_TYPE = "storageType";
  public static final String VF_STORAGE_DESCRIPTION = "storageDescription";
  public static final String VF_STORAGE_PROPERTIES = "storageProperties";
  public static final String VF_STORAGE_TABLE = "storageTable";
  
  public static final String VF_PRIMARY_NODES_UID = "uid";
  public static final String VF_PRIMARY_NODES_IP_ADDRESS = "ipAddress";
  public static final String VF_PRIMARY_NODES_PORT = "port";
  public static final String VF_PRIMARY_NODES_LOGIN = "login";
  public static final String VF_PRIMARY_NODES_PASSWORD = "password";
  public static final String V_PRIMARY_NODES_METRICS = "metrics";
  public static final String VF_PRIMARY_NODES_WEIGHT = "weight";
  public static final String VF_NODES_METRICS_PING = "ping";
  public static final String VF_NODES_METRICS_STATUS = "status";
  public static final String VF_NODES_METRICS_STATUS_DESCRIPTION = "statusDescription";
  public static final String VF_NODES_METRICS_LAST_CONNECTION_TIME = "lastConnectionTime";
  public static final String VF_APPLICATION_SERVER_UID = "uid";
  public static final String VF_APPLICATION_SERVERS_IP_ADDRESS = "ipAddress";
  public static final String VF_APPLICATION_SERVERS_PORT = "port";
  public static final String VF_APPLICATION_SERVERS_LOGIN = "login";
  public static final String VF_APPLICATION_SERVERS_PASSWORD = "password";
  
  public static final String VF_COMMON_CONTEXT_NAME = "commonContextName";
  public static final String VF_COMMON_CONTEXT_DESCRIPTION = "commonContextDescription";
  
  public static final String VF_APPLICATION_RESOURCE = "resource";
  public static final String VF_APPLICATION_DEPENDENCIES = "dependencies";
  public static final String VF_APPLICATION_DEPENDENCY = "dependency";
  
  public static final String VF_ALLOCATION_RULES_EXPRESSION = "expression";
  public static final String VF_ALLOCATION_RULES_CONDITION = "condition";
  public static final String VF_ALLOCATION_RULES_COMMENT = "comment";
  
  public static final String VF_MIGRATE_RESOURCES_SERVER_TO = "serverIdTo";
  
  public static final String FIF_ADD_TENANT_UID = "tenantID";
  public static final String FIF_ADD_TENANT_DESCRIPTION = "tenantDescription";
  
  public static final String FIF_REMOVE_TENANT_UID = FIF_ADD_TENANT_UID;
  
  public static final String FIF_ADD_RESOURCE_TENANT_UID = "tenantID";
  public static final String FIF_ADD_RESOURCE_ORG_UNIT_UID = "orgUnitUID";
  public static final String FIF_ADD_RESOURCE_USER_UID = "userUID";
  public static final String FIF_ADD_RESOURCE_PATH = "resourcePath";
  
  public static final String FIF_ALLOCATE_NODE_DEFAULT_TABLE = "defaultTable";

  public static final String FIF_ADD_PRIMARY_NODE_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;

  public static final String FIF_MIGRATE_RESOURCES_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIF_MIGRATE_RESOURCES_PRIMARY_NODE_UID = VF_PRIMARY_NODES_UID;
  public static final String FIF_MIGRATE_RESOURCES_LIST = "resourcesToMigrate";

  public static final String FIF_SCAN_RESOURCES_PRIMARY_NODE_UID = VF_PRIMARY_NODES_UID;
  public static final String FIF_SCAN_RESOURCES_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIF_SCAN_RESOURCES_FILTERS = "resourceFilters";

  public static final String VF_SCAN_RESOURCES_FILTER_EXPRESSION = "filterExpression";
  public static final String VF_SCAN_RESOURCES_FILTER_MASK = "filterMask";

  public static final String FIF_CHECK_RESOURCE_NAME_AVAILABILITY_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIF_CHECK_RESOURCE_NAME_AVAILABILITY_RESOURCE_PATH = FIF_ADD_RESOURCE_PATH;
  
  public static final String FIF_REMOVE_RESOURCE_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIF_REMOVE_RESOURCE_PATH = FIF_ADD_RESOURCE_PATH;

  public static final String FIF_REMOVE_RESOURCE_NODE_UID = VF_PRIMARY_NODES_UID;
  
  public static final String FIF_GET_RESOURCES_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIF_GET_RESOURCES_ORG_UNIT_UID = FIF_ADD_RESOURCE_ORG_UNIT_UID;
  public static final String FIF_GET_RESOURCES_USER_UID = FIF_ADD_RESOURCE_USER_UID;
  
  public static final String FIF_DROP_TABLE_TABLE_NAME = "tableName";
  public static final String FIF_DROP_TABLE_INDEX_NAME = "indexName";
  
  public static final String FIFT_APPLICATION_NAME = "applicationName";
  public static final String FIFT_APPLICATION_SERVER_TENANT_UID = FIF_ADD_RESOURCE_TENANT_UID;
  public static final String FIFT_APPLICATION_PATH = "applicationPath";
  public static final String FIFT_APPLICATION_USERNAME = "applicationUsername";
  public static final String FIFT_APPLICATION_PASSWORD = "applicationPassword";
  public static final String FIFT_APPLICATION_PARAMETERS = "applicationParameters";
  
  public static final String FOF_CHECK_RESOURCE_NAME_AVAILABILITY_AVAILABLE = "nameAvailable";
  
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_UID = "primaryNodeServerID";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_IP_ADDRESS = "primaryNodeIpAddress";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_PORT = "primaryNodePort";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_LOGIN = "primaryNodeLogin";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_PASSWORD = "primaryNodePassword";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_RESOURCES = "primaryNodeResources";
  
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_RESOURCES_CONTEXT_PATH = "contextPath";
  public static final String FOF_GET_RESOURCES_PRIMARY_NODE_RESOURCES_PERMISSION_LEVEL = "permissionLevel";
  
  public static final String FOF_PRIMARY_NODE_ADDED_STATUS = "creationStatus";
  public static final String FOF_PRIMARY_NODE_REMOVED_STATUS = "removalStatus";
  public static final String FIF_HANDLING_EXISTING_CONTEXTS = "handlingExistingContexts";
  
  public static final String FOF_APPLICATION_FUNCTION_RESULT = "result";
}
