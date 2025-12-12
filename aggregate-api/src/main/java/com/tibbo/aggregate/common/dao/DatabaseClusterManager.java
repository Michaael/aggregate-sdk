package com.tibbo.aggregate.common.dao;

import java.sql.SQLException;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.datatable.DataTable;

// This interface allows a client to do some basic operation with
// a database cluster that manager is instantiated for. The interface is
// only for database cluster managing.
public interface DatabaseClusterManager
{
  
  boolean initializeCluster(String databaseUsername, String databasePassword, boolean testConnection) throws SQLException;
  
  DataTable getStatus() throws ContextException;
  
  void testDatabaseClusterConnection(String databaseUsername, String databasePassword) throws SQLException;
  
  DataTable getClusterDatabases(String databaseUsername, String databasePassword) throws ContextException;
  
  void setClusterDatabases(DataTable value, String username, String password, CallerController caller, boolean activate) throws ContextException;
}
