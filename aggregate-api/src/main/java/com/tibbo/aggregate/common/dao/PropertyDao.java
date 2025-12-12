package com.tibbo.aggregate.common.dao;

import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.EntityReference;
import com.tibbo.aggregate.common.datatable.DataTable;

public interface PropertyDao extends Dao
{

  /**
   * Persistently save value of a variable.
   *
   * @param key
   *          Reference to a variable represented by context path and variable name
   * @param table
   *          Variable value to be stored persistently
   * @throws DaoException
   *           If storage has failed
   * @throws ContextException
   *           If other error occurred
   */
  void saveDataTable(EntityReference key, DataTable table) throws DaoException, ContextException;
  
  /**
   * Persistently save value of a variable.
   *
   * @param key
   *          Reference to a variable represented by context path and variable name
   * @param table
   *          Variable value to be stored persistently
   * @param oldTable
   *          Previous value of the variable or null if not available
   * @throws DaoException
   *           If storage has failed
   * @throws ContextException
   *           If other error occurred
   */
  void saveDataTable(EntityReference key, DataTable table, DataTable oldTable) throws DaoException, ContextException;

  /**
   * Load a previously stored value of a variable.
   * 
   * @param key
   *          Reference to a variable represented by context path and variable name
   * @return Variable's value or null if this value does not exist in the storage
   * @throws DaoException
   *           If loading has failed
   * @throws ContextException
   *           If value was corrupted and cannot be properly converted to a DataTable
   */
  DataTable getDataTable(EntityReference key) throws DaoException, ContextException;
  
  /**
   * Delete persistent variable values that match a certain context path.
   * 
   * @param context
   *          Path of context those persistent variable values must be deleted
   * @throws DaoException
   *           If removal has failed
   * @throws ContextException
   *           If any other error occurred
   * @return Number of data tables deleted
   */
  long deleteDataTables(String context) throws DaoException, ContextException;
  
  /**
   * Delete persistent value of a variable. Removing a variable's value assumes removing referred data blocks. Therefore, a previous value of this variable is normally provided for this method by the
   * system. If previous value is not provided, the DAO implementation should normally read the previous value via {@link #getDataTable(EntityReference)}.
   * 
   * @param key
   *          Reference to a variable represented by context path and variable name
   * @param value
   *          Current value of a variable to use for finding and deleting referred data blocks
   * @throws DaoException
   *           If removal of variable's value or referred data blocks has failed
   * @throws ContextException
   *           If any other error occurred
   */
  void removeDataTable(EntityReference key, DataTable value) throws DaoException, ContextException;
  
  /**
   * Reassociate a set of persistent variable values with a different context path.
   * 
   * @param oldContext
   *          Context path of persistent value to reassociate
   * @param newContext
   *          New context path for selected values
   * @throws DaoException
   *           If reassociation process has failed
   * @return Number of data tables moved
   */
  long moveDataTables(String oldContext, String newContext) throws DaoException;

  /**
   * Checks whether moveDataTables() operation is allowed.
   *
   * @param oldContext
   *          Source context pack
   * @param newContext
   *          Target context path
   * @throws ContextException
   *           If move operation is not currently allowed for provided paths
   */
  void checkMoveDataTablesIsPossible(String oldContext, String newContext) throws ContextException;
  
  /**
   * Reassociate a persistent value with a differently names variable.
   * 
   * @param context
   *          Context path of persistent value to reassociate
   * @param oldName
   *          Old name of a variable pointing to the persistent value
   * @param newName
   *          New name of a variable pointing to the persistent value
   * @throws DaoException
   *           If reassociation has failed
   */
  void renameDataTable(String context, String oldName, String newName) throws DaoException;
  
  /**
   * Returns usage statistics table.
   */
  DataTable getStatistics();

  long changeEncryptionKey(byte[] oldEncryptionKey, byte[] newEncryptionKey, boolean encryptionDryRun);

  /**
   * Migrate a resource from one node of a cluster to another
   *
   * @param sourceServerId
   *          ID of server to migrate resource from
   * @param targetServerId
   *          ID of server to migrate resource to
   * @param resourceName
   *           Path of the resource to be migrated
   * @throws DaoException
   *           If migration has failed
   */
  void migrateResource(String sourceServerId, String targetServerId, String resourceName) throws DaoException;

  void checkIfContextCanBeMoved(String oldPath, String newPath) throws ContextException;
}