package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleDouble extends SimpleFormatConverter
{
  public FCSimpleDouble()
  {
    super(double.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.DOUBLE_FIELD);
  }
  
  public Object simpleToBean(Object value)
  {
    return ((Number) value).doubleValue();
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return value;
  }
}
