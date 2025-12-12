package com.tibbo.aggregate.common.dao;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.server.ServerConfig;

public interface DaoFactory
{
  void start() throws DaoException;
  
  void shutdown() throws DaoException;

  DaoFactory getMasterFactory();

  EventStorageManager getStorageManager();

  EventDao getEventDao();
  
  PropertyDao getPropertyDao();
  
  DataDao getDataDao();
  
  DatabaseClusterManager getDatabaseClusterManager();
  
  GenericDao getGenericDao();
  
  DataTable getStatistics();
  
  DataTable getTableStatistics();
  
  DataTable getConnectionStatistics();

  ServerConfig getServerConfig();
}