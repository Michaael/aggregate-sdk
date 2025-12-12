package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.Log;

public abstract class AbstractBigDataTable extends AbstractDataTable
{
  private Integer recordCount;
  
  @Override
  public Integer getRecordCount()
  {
    return recordCount;
  }
  
  protected void setRecordCount(Integer recordCount)
  {
    if (recordCount != null && recordCount < 0)
      throw new IllegalArgumentException("Record count cannot be negative");
    
    this.recordCount = recordCount;
  }
  
  protected void incrementRecordCount()
  {
    if (recordCount == null)
      return;
    
    if (recordCount == Integer.MAX_VALUE)
      throw new IllegalStateException("Cannot increment record count, as it is already equal to the maximum integer value");
    
    ++recordCount;
  }
  
  protected void decrementRecordCount()
  {
    if (recordCount == null)
      return;
    
    if (recordCount == 0)
      throw new IllegalStateException("Cannot decrement record count, as it is already equal to zero");
    
    --recordCount;
  }
  
  /**
   * Sets table ID (even if the table is immutable) if it has not been already set to a non-null Long.
   */
  @Override
  public void setId(Long id)
  {
    if (id == null)
    {
      throw new IllegalArgumentException("id must not be null");
    }
    if (this.id == null)
    {
      this.id = id;
    }
    else
    {
      Log.DATATABLE.warn("An attempt was made to reset table id " + this.id + " with " + id, new Exception());
    }
  }
  
  @Override
  public int compareTo(DataTable other)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  /**
   * Makes this table immutable. Note that subtables of this table are not made immutable.
   */
  @Override
  public DataTable makeImmutable()
  {
    if (immutable)
      return this;
    
    immutable = true;
    
    format.makeImmutable(this);
    
    return this;
  }
  
  /**
   * Creates a <code>SimpleDataTable</code> that contains clones of the records represented by this data table. Does NOT call <code>super.clone()</code>
   */
  @Override
  public DataTable clone()
  {
    final SimpleDataTable clone = toSimpleDataTable();
    
    clone.namingEvaluator = null;
    
    clone.immutable = false;
    
    if (Log.DATATABLE.isDebugEnabled())
      Log.DATATABLE.debug("The clone method was called; a SimpleDataTable copy of this Data Table was created: " + clone);
    
    return clone;
  }
  
  protected SimpleDataTable toSimpleDataTable()
  {
    final SimpleDataTable simpleDataTable = new SimpleDataTable(format);
    
    for (DataRecord record : this)
    {
      simpleDataTable.addRecord(record.clone());
    }
    
    if (getTimestamp() != null)
      simpleDataTable.setTimestamp(getTimestamp());
    
    if (getQuality() != null)
      simpleDataTable.setQuality(getQuality());
    
    return simpleDataTable;
  }
  
  @Override
  public boolean isSimple()
  {
    return false;
  }
}
