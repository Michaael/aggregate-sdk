package com.tibbo.aggregate.common.dao;

import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.DataTable;

public interface DataDao extends Dao
{
  /**
   * Load a persistently stored data block.
   * 
   * @param id
   *          A unique identified of a previously stored data block.
   * @return A loaded data block
   * @throws DaoException
   *           If loading has failed
   */
  Data getData(long id) throws DaoException;
  
  /**
   * Persistently save a data block. Block's unique ID is available in the the data block itself. If a block with the same ID already exists, its persistent content should be updated.
   * 
   * @param data
   *          A binary block to save or update
   * @throws DaoException
   *           If save/update operation has failed
   */
  void saveOrUpdateData(Data data) throws DaoException;
  
  /**
   * Delete a persistent data block. Normally, block's ID contained in the block itself if required to find and remove it.
   * 
   * @param data
   *          A data block to delete, including its unique ID
   * @throws DaoException
   *           If removal has failed
   */
  void deleteData(Data data) throws DaoException;

  /**
   * Delete a persistent data block.
   *
   * @param dataId
   *          An ID of data block to delete
   * @throws DaoException
   *           If removal has failed
   */
  void deleteData(long dataId) throws DaoException;

  /**
   * Returns usage statistics table.
   */
  DataTable getStatistics();
}