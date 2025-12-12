package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleLong extends SimpleFormatConverter
{
  public FCSimpleLong()
  {
    super(long.class);
  }
  
  @Override
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.LONG_FIELD);
  }
  
  @Override
  public Object simpleToBean(Object value)
  {
    return value;
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return value;
  }
}
