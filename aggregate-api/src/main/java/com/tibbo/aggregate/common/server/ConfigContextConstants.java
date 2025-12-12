package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.util.TimeHelper;

public interface ConfigContextConstants
{
  String V_GENERAL = "general";
  String V_DATABASE = "database";
  String V_NOSQL = "nosql";
  String V_RELATIONAL = "relational";
  String V_KEYVALUE = "keyvalue";
  String V_CLUSTER = "cluster";
  String V_LICENSE_SERVER = "licenseGroup"; // Do not rename otherwise you have to take care to support backward compatibility!
  String V_CONFIGURATION_SERVER = "configurationServer";

  String V_STATISTICS = "statistics";
  String V_DEFAULT_SYNC_OPTIONS = "defaultSyncOptions";
  String V_EVENT_STORAGES = "eventStorages";
  String V_EVENT_STORAGES_CONFIGURATION = "eventStoragesConfiguration";
  String V_EVENT_PROCESSING_RULES = "eventProcessingRules";
  String V_EVENT_WRITERS = "eventWriters";
  String V_PASSWORD_SETTINGS = "passwordSettings";
  String V_DEFAULT_PERMISSIONS = "defaultPermissions";
  String V_ADDITIONAL_PERMISSIONS = "additionalPermissions";
  String V_PLUGINS = "plugins";
  String V_STORES = "stores";
  String V_CONFIG = "config";
  String V_AUTHENTICATION = "authentication";
  String V_AUTO_REGISTRATION_SETTINGS = "autoRegistrationSettings";
  String V_VIRTUAL_NETWORKS = "virtualNetworks";
  String V_CERTIFICATES = "certificates";
  String V_ENCRYPTION = "encryption";
  String V_ANONYMOUS_CONFIG = "anonymousConfig";
  
  String V_MASTER_INSTALLATION_DATE = "masterInstallationDate";
  String V_SLAVE_INSTALLATION_DATE = "slaveInstallationDate";

  String VF_LICENSE_SERVER_ENABLED = "licenseServerEnabled";
  String VF_LICENSE_SERVER_ADDRESS = "licenseServerAddress";
  String VF_LICENSE_SERVER_PORT = "licenseServerPort";
  String VF_LICENSE_SERVER_LOGIN = "licenseServerLogin";
  String VF_LICENSE_SERVER_PASSWORD = "licenseServerPassword";
  String VF_LICENSE_SERVER_ROLE = "licenseServerRole";
  
  String VF_CERTIFICATES_PREFER_CRLS = "certificatesPreferCrls";
  String VF_CERTIFICATES_ONLY_END_ENTITY = "certificatesOnlyEndEntity";
  String VF_CERTIFICATES_NO_FALLBACK = "certificatesNoFallback";
  String VF_CERTIFICATES_SOFT_FAIL = "certificatesSoftFail";
  
  String VF_INTERNAL_ENCRYPTION_ENABLED = "internalEncryptionEnabled";
  
  String VF_GENERAL_SERVER_DESCRIPTION = "serverDescription";
  String VF_GENERAL_TIMEZONE = "timezone";
  String VF_GENERAL_SERVER_IP = "serverIp";
  String VF_GENERAL_SERVER_ID = "serverId";
  String VF_GENERAL_SERVER_HOST_NAME = "serverHostName";
  String VF_GENERAL_USERS_SELF_REGISTRATION = "usersSelfRegistration";
  String VF_GENERAL_CONFIG_GUI_MODE = "configGuiMode";
  String VF_GENERAL_MAINTENANCE_MODE = "maintenanceMode";
  String VF_GENERAL_CLIENT_PORT = "clientPort";
  String VF_GENERAL_CLIENT_EVENT_QUEUE_LENGTH = "clientEventQueueLength";
  String VF_GENERAL_HTTP_SESSION_TIMEOUT = "httpSessionTimeout";
  String VF_GENERAL_NON_SECURE_CLIENT_COMMUNICATION = "nonSecureClientCommunicationEnabled";
  String VF_GENERAL_NON_SECURE_CLIENT_PORT = "nonSecureClientPort";
  String VF_GENERAL_TWO_WAY_AUTH_CLIENT_PORT = "twoWayAuthClientPort";
  String VF_GENERAL_STATISTICS_FOLDER = "statisticsFolder";
  
  String VF_DATABASE_CLUSTER = "databaseCluster";
  String VF_DATABASE_KV_CLUSTER_ROLE = "databaseKvClusterRole";
  String VF_DATABASE_KV_CLUSTER_PRIMARY_DB_PORT = "databaseKvClusterPrimaryDbPort";
  String VF_DATABASE_KV_CLUSTER_HELPER_DB_URL = "databaseKvClusterHelperUrl";
  String VF_DATABASE_KV_CLUSTER_PRIORITY = "databaseKvClusterPriority";
  String VF_DATABASE_KV_CACHE_SIZE = "databaseKvCacheSize";
  String VF_DATABASE_KV_MIN_UTILIZATION = "databaseKvMinUtilization";
  
  String VF_DATABASE_KV_CLEANER_THREADS = "databaseKvCleanerThreads";
  String VF_DATABASE_KV_CLEANER_ACTIVATOR = "databaseKvCleanerActivator";
  String VF_DATABASE_KV_CLEANER_BYTE_THRESHOLD = "databaseKvCleanerByteThreshold";
  String VF_DATABASE_KV_CLEANER_WAKEUP_INTERVAL = "databaseKvCleanerWakeupInterval";
  
  String VF_DATABASE_KV_TRANSACTIONS_COMMIT_INTERVAL = "databaseKvTransactionCommitInterval";
  String VF_DATABASE_KV_TRANSACTIONS_COMMIT_THRESHOLD = "databaseKvTransactionsCommitThreshold";
  
  String VF_DATABASE_KV_CHECPOINTER_ACTIVATOR = "databaseKvCheckpointerActivator";
  String VF_DATABASE_KV_CHECPOINTER_WRITE_INTERVAL = "databaseKvCheckpointerWriteInterval";
  String VF_DATABASE_KV_CHECPOINTER_WAKEUP_INTERVAL = "databaseKvCheckpointerWakeupInterval";
  
  String VF_DATABASE_KV_MAX_REPLICATION_MESSAGE_SIZE = "databaseKvMaxReplicationMessageSize";
  String VF_DATABASE_KV_MAX_DISK = "databaseKvMaxDisk";
  String VF_DATABASE_KV_FREE_DISK = "databaseKvFreeDisk";
  
  String VF_DATABASE_DRIVER = "databaseDriver";
  String VF_DATABASE_URL = "databaseUrl";
  String VF_DATABASE_CLUSTER_DATABASES = "databaseClusterDatabases";
  String VF_DATABASE_USERNAME = "databaseUsername";
  String VF_DATABASE_PASSWORD = "databasePassword";
  String VF_DATABASE_SQL_DIALECT = "databaseSqlDialect";
  String VF_DATABASE_MINIMUM_POOL_SIZE = "databaseMinimumPoolSize";
  String VF_DATABASE_MAXIMUM_POOL_SIZE = "databaseMaximumPoolSize";
  String VF_DATABASE_BATCH_SIZE = "databaseBatchSize";
  String VF_DATABASE_CHECKOUT_TIMEOUT = "databaseCheckoutTimeout";
  String VF_DATABASE_UNRETURNED_CONNECTION_TIMEOUT = "databaseUnreturnedConnectionTimeout";
  String VF_DATABASE_DISABLE_POOLING = "databaseDisablePooling";
  String VF_DATABASE_CASSANDRA_USE_EMBEDDED_SERVICE = "databaseCassandraUseEmbeddedService";
  String VF_DATABASE_CASSANDRA_USE_YAML_CONFIGURATION = "databaseCassandraUseYamlConfiguration";
  String VF_DATABASE_CASSANDRA_HOST = "databaseCassandraHost";
  String VF_DATABASE_CASSANDRA_NATIVE_PORT = "databaseCassandraNativePort";
  String VF_DATABASE_CASSANDRA_CONFIGURATION_KEYSPACE = "databaseCassandraConfigurationKeyspace";
  String VF_DATABASE_CASSANDRA_EVENT_HISTORY_KEYSPACE = "databaseCassandraEventHistoryKeyspace";
  String VF_DATABASE_CASSANDRA_BINARY_DATA_KEYSPACE = "databaseCassandraBinaryDataKeyspace";
  String VF_DATABASE_CASSANDRA_STATISTICS_KEYSPACE = "databaseCassandraStatisticsKeyspace";
  String VF_DATABASE_CASSANDRA_INTERNAL_KEYSPACE = "databaseCassandraInternalKeyspace";
  String VF_DATABASE_REPLICATION_FACTOR = "databaseReplicationFactor";
  String VF_DATABASE_CASSANDRA_COMMITLOG_DIRECTORY = "databaseCassandraCommitlogDirectory";
  String VF_DATABASE_CASSANDRA_COMMITLOG_SIZE = "databaseCassandraCommitlogSize";
  String VF_DATABASE_CASSANDRA_CACHES_DIRECTORY = "databaseCassandraCachesDirectory";
  String VF_DATABASE_CASSANDRA_STORAGE_DIRECTORY = "databaseCassandraStorageDirectory";
  String VF_DATABASE_CASSANDRA_USE_AUTHENTICATION = "databaseCassandraUseAuthentication";
  String VF_DATABASE_CASSANDRA_LOGIN = "databaseCassandraLogin";
  String VF_DATABASE_CASSANDRA_PASSWORD = "databaseCassandraPassword";
  String VF_DATABASE_CASSANDRA_CONSISTENCY_LEVEL = "databaseCassandraConsistencyLevel";
  String VF_DATABASE_CASSANDRA_CONTACT_POINTS = "databaseCassandraContactPoints";
  String VF_DATABASE_CASSANDRA_LOAD_BALANCING = "databaseCassandraLoadBalancing";
  String VF_DATABASE_CASSANDRA_RECONNECTION_POLICY = "databaseCassandraReconnectionPolicy";
  String VF_DATABASE_CASSANDRA_READ_REQUEST_TIMEOUT = "databaseCassandraReadRequestTimeout";
  
  String VF_DATABASE_SEEDS = "databaseSeeds";
  String VF_DATABASE_CASSANDRA_BATCH_SIZE = "databaseCassandraBatchSize";
  String VF_DATABASE_CASSANDRA_BATCH_SIZE_THRESHOLD = "databaseCassandraBatchSizeThreshold";
  String VF_DATABASE_CONFIGURATION_STORAGE = "databaseConfigurationStorage";
  String VF_DATABASE_EVENT_HISTORY_STORAGE = "databaseEventHistoryStorage";
  String VF_DATABASE_BINARY_DATA_STORAGE = "databaseBinaryDataStorage";
  String VF_DATABASE_TEST_CONNECTION = "databaseTestConnection";
  String VF_DATABASE_TEST_CONNECTION_RESULT = "databaseTestConnectionResult";
  
  String VF_DATABASES_ID = "id";
  String VF_DATABASES_URL = "url";
  String VF_DATABASES_WEIGHT = "weight";
  String VF_DATABASES_LOCAL = "local";
  
  String VF_LOGGER_ENABLED = "enabled";
  String VF_LOGGER_EXPRESSION = "expression";
  String VF_LOGGER_CONCURRENT = "normalLoggerConcurrentWorkers";
  String VF_LOGGER_MAXIMUM_CONCURRENT = "maximumLoggerConcurrentWorkers";
  String VF_LOGGER_MAXIMUM_QUEUE_LENGTH = "maximumLoggerQueueLength";
  
  String VF_VARIABLE_VALIDATORS_VARIABLE_NAME = "variableName";
  String VF_VARIABLE_VALIDATORS_CONTEXT_MASK = "contextMask";
  String VF_VARIABLE_VALIDATORS_EXPRESSION = "expression";
  
  String VF_CLUSTER_FAILURE_DETECTION_TIME = "clusterFailureDetectionTime";
  String VF_CLUSTER_FAILOVER_READONLY = "clusterFailoverReadonly";
  String VF_CLUSTER_ROLE = "clusterRole";
  String VF_CLUSTER_HEARTBEAT_PORT = "clusterHeartbeatPort";
  String VF_CLUSTER_HEARTBEAT_HELPER_URLS = "clusterHeartbeatHelperUrls";
  String VF_CLUSTER_HEARTBEAT_INTERFACE_ADDRESS = "clusterHeartbeatInterfaceAddress";
  
  String VF_PLUGINS_ID = "id";
  String VF_PLUGINS_NAME = "name";
  String VF_PLUGINS_ENABLED = "enabled";
  String VF_CONFIGURATION_SERVER_ENABLED = "configurationServerEnabled";
  String VF_CONFIGURATION_SERVER_ADDRESS = "configurationServerAddress";
  String VF_CONFIGURATION_SERVER_PORT = "configurationServerPort";
  String VF_CONFIGURATION_SERVER_LOGIN = "configurationServerLogin";
  String VF_CONFIGURATION_SERVER_PASSWORD = "configurationServerPassword";

  String VF_STORES_ADDRESS = "address";
  String VF_STORES_PORT = "port";
  String VF_STORES_DESCRIPTION = "description";
  String VF_STORES_LOGIN = "login";
  String VF_STORES_PASSWORD = "password";
  String VF_SHOW_STORE_ON_STARTUP = "showStoreOnStartup";
  
  String VF_ADDITIONAL_PERMISSIONS_TYPE = "type";
  String VF_ADDITIONAL_PERMISSIONS_MASK = "mask";
  
  String VF_AUTHENTICATION_EXTERNAL_AUTHENTICATION = "externalAuthentication";
  String VF_AUTHENTICATION_EXTERNAL_REGISTER_LINK = "externalRegisterLink";
  String VF_AUTHENTICATION_USER_CONNECTION_MODE = "userConnectionMode";
  String VF_AUTHENTICATION_NEW_USER_ACTIVATION_EXPRESSION = "newUserActivationExpression";
  String VF_NUMBER_LOGIN_ATTEMPTS = "numberLoginAttempts";
  String VF_ACCOUNT_LOCKOUT_DURATION = "accountLockoutDuration";
  String VF_TIMEOUT_BEFORE_NEXT_LOGIN_ATTEMPT = "timeoutBeforeNextLoginAttempt";
  
  String VF_PASSWORD_MIN_LENGTH = "passwordMinLength";
  
  String VF_PASSWORD_EXPIRATION_PERIOD = "passwordExpirationPeriod";
  String VF_PASSWORD_REQUIRE_CAPITAL_LETTERS = "requireCapitalLetters";
  String VF_PASSWORD_REQUIRE_NUMBERS = "requireNumbers";
  String VF_PASSWORD_REQUIRE_SPECIAL_CHARACTERS = "requireSpecialCharacters";
  String VF_PASSWORDS_AMOUNT = "passwordsAmount";



  String VF_INSTALLATION_DATE_DATE = "date";
  
  String VF_ANONYMOUS_ENABLED = "anonymousEnabled";
  String VF_ANONYMOUS_PERMISSIONS = "anonymousPermissions";
  String VF_DEFAULT_DASHBOARD_FOR_ANONYMOUS = "defaultDashboardForAnonymous";
  
  String A_DATABASE_TEST_CONNECTION = "databaseTestConnection";
  String F_DATABASE_TEST_CONNECTION = "databaseTestConnection";

  Integer AUTHENTICATION_ALLOW_CONCURRENT = 0;
  Integer AUTHENTICATION_DISCONNECT_LAST = 1;
  Integer AUTHENTICATION_RESTRICT_CONCURRENT = 2;
  
  Integer DEFAULT_COUNT_ATTEMPT_LOGIN = 3;
  Long DEFAULT_LOCKOUT_DURATION = TimeHelper.SECOND_IN_MS * 30L;
  Long DEFAULT_TIMEOUT_BEFORE_NEXT_LOGIN_ATTEMPT = TimeHelper.SECOND_IN_MS;
}
