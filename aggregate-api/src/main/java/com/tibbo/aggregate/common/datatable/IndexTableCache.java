package com.tibbo.aggregate.common.datatable;

import java.lang.ref.*;
import java.util.*;

public class IndexTableCache<T>
{
  private SoftReference<List<T>> softIndexTable = new SoftReference<>(new ArrayList<>());
  
  private final DataTable dataTable;
  
  public IndexTableCache(DataTable dataTable)
  {
    this.dataTable = dataTable;
  }
  
  public List<T> getIndexTable()
  {
    final List<T> indexTable = softIndexTable.get();
    return indexTable != null ? indexTable : new ArrayList<>();
  }
  
  public void setSoftIndexTable(List<T> indexTable)
  {
    softIndexTable = new SoftReference<>(indexTable);
  }
  
  public boolean isIndexTableComplete(List<T> indexTable, Integer recordCount)
  {
    return recordCount != null && indexTable != null && indexTable.size() == recordCount;
  }
  
  public void ensureNotDefinitelyInvalid(int index)
  {
    final List<T> indexTable = softIndexTable.get();
    final Integer recordCount = dataTable.getRecordCount();
    
    if (isIndexTableComplete(indexTable, recordCount) && index >= recordCount)
      throw new IndexOutOfBoundsException("The given index: " + index + " exceeds the number of records in this Data Table: " + recordCount);
  }
  
  public DataRecord iterateThroughSourceUpToNumber(int number, List<T> indexTable)
  {
    int currentMaxIndexInThisDataTable = indexTable.size() - 1;
    final Iterator<DataRecord> iter = currentMaxIndexInThisDataTable != -1 ? dataTable.iterator(currentMaxIndexInThisDataTable + 1) : dataTable.iterator();
    while (iter.hasNext())
    {
      final DataRecord record = iter.next();
      currentMaxIndexInThisDataTable++;
      if (currentMaxIndexInThisDataTable == number)
        return record;
    }
    
    throw new IndexOutOfBoundsException("The required index is out of range");
  }
  
  public boolean isOneCellTable()
  {
    if (dataTable.getFieldCount() != 1)
      return false;
    
    final Integer recordCount = dataTable.getRecordCount();
    
    if (recordCount != null && recordCount == 1)
      return true;
    
    if (recordCount != null)
      return false;
    
    final List<T> indexTable = getIndexTable();
    int currentMaxIndexInThisDataTable = indexTable.size() - 1;
    if (currentMaxIndexInThisDataTable > 0)
    {
      return false;
    }
    else if (currentMaxIndexInThisDataTable == 0)
    {
      final Iterator<DataRecord> iter = dataTable.iterator(1);
      return !iter.hasNext();
    }
    else
    {
      final Iterator<DataRecord> iter = dataTable.iterator();
      if (!iter.hasNext())
        return false;
      iter.next();
      return !iter.hasNext();
    }
  }
}
