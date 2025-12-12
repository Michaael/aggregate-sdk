package com.tibbo.aggregate.common.datatable;

/**
 * WARNING:
 *
 * In the inherited classes, do not assign field values in the declaration!
 *
 * Field values will be assigned by one of the AggreGateBean constructor.
 *
 * Default field values must be defined in the format.
 *
 * Successors must override AggreGateBean(DataRecord) constructor!
 */
public abstract class AggreGateBean
{
  private final TableFormat format;
  
  public AggreGateBean(TableFormat format)
  {
    this.format = format;
    if (format != null)
    {
      try
      {
        DataTableConversion.populateBeanFromRecord(this, new DataRecord(format), format, true);
      }
      catch (DataTableException ex)
      {
        throw new IllegalStateException(ex);
      }
    }
  }
  
  public AggreGateBean(TableFormat format, DataRecord data)
  {
    this.format = format;
    try
    {
      DataTableConversion.populateBeanFromRecord(this, data, format, true);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public DataTable toDataTable()
  {
    try
    {
      return DataTableConversion.beanToTable(this, format, true, true);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public DataRecord toDataRecord()
  {
    try
    {
      return DataTableConversion.beanToRecord(this, format, true, true);
    }
    catch (DataTableException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
  
  public TableFormat getFormat()
  {
    return format;
  }
}
