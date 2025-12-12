package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.DataTable;

public class TableFieldsCompatibilityConverter implements CompatibilityConverter
{
  
  private final String oldFieldName;
  private final String newFieldName;
  
  public TableFieldsCompatibilityConverter(String oldFieldName, String newFieldName)
  {
    this.oldFieldName = oldFieldName;
    this.newFieldName = newFieldName;
  }
  
  @Override
  public DataTable convert(DataTable oldValue, DataTable newValue)
  {
    if (!oldValue.getFormat().hasField(oldFieldName))
    {
      return newValue;
    }
    
    for (int i = 0; i < Math.min(oldValue.getRecordCount(), newValue.getRecordCount()); i++)
    {
      Object expression = oldValue.getRecord(i).getValue(oldFieldName);
      newValue.getRecord(i).setValue(newFieldName, expression);
    }
    
    return newValue;
  }
}
