package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.datatable.DataTable;

public interface ServerConfig
{
  String getServerDescription();

  boolean isDatabaseCluster();
  
  int getDatabaseMinimumPoolSize();
  
  int getDatabaseMaximumPoolSize();
  
  int getDatabaseBatchSize();
  
  long getDatabaseCheckoutTimeout();
  
  boolean isDatabaseDisablePooling();
  
  int getDatabaseKvClusterRole();
  
  int getClusterRole();
  
  boolean isClusterFailoverReadonly();
  
  long getClusterFailureDetectionTime();
  
  String getDatabaseDriver();
  
  String getDatabasePassword();
  
  String getDatabaseSqlDialect();
  
  String getDatabaseHbm2ddlAuto();
  
  String getDatabaseUrl();
  
  String getDatabaseUsername();
  
  boolean isFirstLaunch();
  
  boolean isUsersSelfRegistration();
  
  String getConfigGuiMode();
  
  boolean isMaintenanceMode();
  
  String getPluginsAdditionalDirs();
  
  int getPreviousVersion();
  
  String getTimezone();
  
  String getServerIp();
  
  String getServerHostName();
  
  String getStatisticsFolder();
  
  int getClientPort();

  long getHttpSessionTimeout();

  int getClientEventQueueLength();
  
  boolean isNonSecureClientCommunicationEnabled();
  
  int getNonSecureClientPort();
  
  boolean isExtBatchPropertyLoading();
  
  int getExtSyncTimers();
  
  int getExtCoreSyncThreads();
  
  int getExtMaxSyncThreads();
  
  int getExtCoreContextOperationThreads();
  
  int getExtMaxClientCommandThreads();
  
  int getExtTrackerTimers();
  
  int getExtAlertTimers();
  
  int getExtMaxEventQueueLength();
  
  int getExtEventQueuesLength();
  
  int getExtQueryDataSources();
  
  String getExtCustomDatabaseClusterConfig();
  
  boolean isClusterReadOnlyFailover();
  
  int getDatabaseKvClusterPriority();
  
  String getDatabaseKvClusterHelperUrl();
  
  Integer getDatabaseKvClusterPrimaryDbPort();
  
  Integer getDatabaseKvCacheSize();
  
  Integer getDatabaseKvMinUtilization();
  
  Integer getDatabaseKvCleanerThreads();
  
  Integer getDatabaseKvCleanerByteThreshold();
  
  Long getDatabaseKvCleanerWakeupInterval();
  
  Long getDatabaseKvTransactionsCommitInterval();
  
  Integer getDatabaseKvTransactionsCommitThreshold();
  
  Long getDatabaseKvMaxReplicationMessageSize();
  
  Long getDatabaseKvCheckpointerWakeupInterval();
  
  Long getDatabaseKvCheckpointerWriteInterval();
  
  Long getDatabaseKvFreeDisk();
  
  Long getDatabaseKvMaxDisk();
  
  String getClusterHeartbeatHelperUrls();
  
  Integer getClusterHeartbeatPort();
  
  String getClusterHeartbeatInterfaceAddress();
  
  Integer getDatabaseReplicationFactor();
  
  String getDatabaseSeeds();
  
  Integer getDatabaseConfigurationStorage();
  
  Integer getDatabaseEventHistoryStorage();
  
  Integer getDatabaseBinaryDataStorage();
  
  Integer getDatabaseCassandraNativePort();
  
  String getDatabaseCassandraConfigurationKeyspace();
  
  String getDatabaseCassandraEventHistoryKeyspace();
  
  String getDatabaseCassandraBinaryDataKeyspace();
  
  String getDatabaseCassandraStatisticsKeyspace();
  
  String getDatabaseCassandraInternalKeyspace();
  
  String getDatabaseCassandraHost();
  
  String getDatabaseCassandraStorageDirectory();
  
  Integer getDatabaseCassandraBatchSize();
  
  Integer getDatabaseCassandraBatchSizeThreshold();

  String getDatabaseCassandraCommitlogDirectory();
  
  int getDatabaseCassandraCommitlogSize();
  
  int getDatabaseCassandraMutationSize();
  
  String getDatabaseCassandraCachesDirectory();
  
  Boolean getDatabaseCassandraUseAuthentication();
  
  Integer getDatabaseCassandraReadRequestTimeout();
  
  String getDatabaseCassandraLogin();
  
  String getDatabaseCassandraPassword();
  
  int getExtCoreConcurrentDispatcherThreads();

  int getExtMaxConcurrentDispatcherThreads();

  int getExtConcurrentDispatcherQueueLength();

  int getExtLowMemoryModeInThreshold();

  int getExtLowMemoryModeOutThreshold();

  boolean isDatabaseCassandraUseEmbeddedService();

  boolean isDatabaseCassandraUseYamlConfiguration();

  String getDatabaseCassandraConsistencyLevel();

  String getDatabaseCassandraContactPoints();

  DataTable getDatabaseCassandraLoadBalancing();

  DataTable getDatabaseCassandraReconnectionPolicy();

  Long getDatabaseUnreturnedConnectionTimeout();

  String getDatabaseKvCheckpointerActivator();

  String getDatabaseKvCleanerActivator();

  Integer getDatabaseEventHistoryStoragePolicy();

  String getServerId();

  String getCurrentVersion();

  boolean getExtSyncDelay();

  boolean isExtWebSocketKeepAliveDisabled();
}