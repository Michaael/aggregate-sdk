package com.tibbo.aggregate.common.dao;

import java.sql.SQLException;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.view.StorageException;
import com.tibbo.aggregate.common.view.StorageSession;

public interface StorageImplementation
{
  DataTable open(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable close(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable get(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable update(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable delete(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable insert(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable tables(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable columns(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable filter(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable sorting(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable operations(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable linkInstances(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable unlinkInstances(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable linkedInstancesFilter(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  DataTable getFormat(Context storageContext, CallerController caller, DataTable parameters) throws StorageException;
  
  StorageSession createViewSession() throws SQLException;
  
  DaoFactory getDaoFactory();
  
  boolean isTableAllowed(String value);
}
