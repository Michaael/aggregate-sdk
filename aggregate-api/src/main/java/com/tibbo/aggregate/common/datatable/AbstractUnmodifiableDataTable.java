package com.tibbo.aggregate.common.datatable;

import java.util.*;

public abstract class AbstractUnmodifiableDataTable extends AbstractBigDataTable
{
  @Override
  protected DataRecord removeRecordImpl(int index)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataTable addRecord(DataRecord record)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataRecord addRecord(Object... fieldValues)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataTable addRecord(int index, DataRecord record)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataRecord addRecord()
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataTable setRecord(int index, DataRecord record)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void removeRecords(DataRecord rec)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void reorderRecord(DataRecord record, int index)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void sort(DataTableSorter sorter)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void sort(Comparator<DataRecord> comparator)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
}
