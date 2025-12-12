package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleBoolean extends SimpleFormatConverter
{
  public FCSimpleBoolean()
  {
    super(boolean.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.BOOLEAN_FIELD);
  }
  
  public Object simpleToBean(Object value)
  {
    return ((Boolean) value).booleanValue();
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return Boolean.valueOf(value.toString());
  }
}
