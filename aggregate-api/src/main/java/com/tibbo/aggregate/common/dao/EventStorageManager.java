package com.tibbo.aggregate.common.dao;

import java.util.Collection;
import java.util.List;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableException;

public interface EventStorageManager
{
  /**
   * Add a dedicated event storage for events of a certain type.
   * 
   * @param context
   *          Context those events should be kept in the storage or null if the storage is valid for all contexts
   * @param def
   *          Type of events to keep in the storage
   * @return Newly created storage descriptor
   */
  EventStorage addStorage(Context context, EventDefinition def);
  
  /**
   * Get event storage to used to keep certain events.
   * 
   * @param context
   *          Path of context to find the storage for
   * @param event
   *          Name of event to find the storage for
   * @return Event storage descriptor
   */
  EventStorage getStorage(String context, String event);
  
  /**
   * Returns storage manager's DAO factory
   * 
   * @return The DAO factory being used
   */
  DaoFactory getFactory();
  
  /**
   * Get an event storage used to keep events that are not forwarded to any other custom storage.
   * 
   * @return Default event storage descriptor
   */
  EventStorage getDefaultStorage();
  
  /**
   * Returns a list of all event storages available on the server.
   * 
   * @return List of event storages
   */
  Collection<EventStorage> getStorages();
  
  /**
   * Returns a list of all event storages in form of a DataTable.
   * 
   * @return DataTable containing all event storages
   * @throws DataTableException
   *           If conversion to a DataTable has failed
   */
  DataTable getStoragesAsTable() throws DataTableException;
  
  /**
   * Returns an event storage descriptor by a specified table name.
   * 
   * @param table
   *          Name of storage table
   * @return Event storage descriptor
   */
  EventStorage getStorage(String table);
  
  /**
   * Add a new storage.
   * 
   * @param storage
   *          A storage to add
   */
  void addStorage(EventStorage storage);
  
  /**
   * Remove a storage
   * 
   * @param storage
   *          A storage to remove.
   */
  void removeStorage(EventStorage storage);
  
  /**
   * Remove all storages associated with a context.
   * 
   * @param context
   *          The context those storages should be removed
   * @throws DaoException
   *           If a removal has failed
   * @throws ContextException
   *           If any other error occurred
   */
  void removeStorages(String context) throws DaoException, ContextException;
  
  /**
   * Update event storages
   * 
   * @param oldStoragesTable
   *          Old storages table
   * @param newStoragesTable
   *          New storages table
   * @param create
   *          True if any newly added storage should be created
   * @throws DaoException
   *           If updating has failed
   */
  void updateStorages(DataTable oldStoragesTable, DataTable newStoragesTable, boolean create) throws DaoException;
  
  /**
   * Perform initialization of all storages. This normally happens during DAO system startup.
   * 
   * @param eventStorages
   *          A list of storages to initialize
   * @param create
   *          Defines whether storages should be created
   * @throws DaoException
   *           If initialization has failed
   */
  void initializeEventStorages(List<? extends EventStorage> eventStorages, boolean create) throws DaoException;
  
  /**
   * Returns a list of all event storages available on the server.
   * 
   * @return List of event storages
   */
  List<? extends EventStorage> fetchEventStorages() throws DaoException;
  
  /**
   * Returns a list of all event storages in form of a DataTable.
   * 
   * @return DataTable containing all event storages
   * @throws DataTableException
   *           If conversion to a DataTable has failed
   */
  DataTable fetchEventStoragesTable() throws DaoException, ContextException;
  
  /**
   * Persistently save information about currently configured event storages
   */
  void saveEventStorages() throws DaoException, ContextException;
  
  /**
   * Persistently save information about specified event storages
   */
  void saveEventStorages(Collection<EventStorage> eventStorages) throws DaoException, ContextException;
  
  /**
   * Setup a dedicated event storage for specific events.
   * 
   * @param context
   *          Context to configure the storage for or null if the storage should be valid for all contexts
   * @param def
   *          Event definition
   * @param create
   *          Defines whether the storage should be created
   * @throws DaoException
   *           If storage creation has failed
   * @throws ContextException
   *           If any other error occurred
   */
  void prepareDedicatedStorage(Context context, EventDefinition def, boolean create) throws DaoException, ContextException;
  
  /**
   * Returns an event storage descriptor by a specified context name.
   * 
   * @param context
   *          Name of storage context
   * @return Event storage descriptor
   */
  EventStorage getStorageByContext(String context);
  
}