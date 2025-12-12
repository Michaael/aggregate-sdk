package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleFloat extends SimpleFormatConverter
{
  public FCSimpleFloat()
  {
    super(float.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.FLOAT_FIELD);
  }
  
  public Object simpleToBean(Object value)
  {
    return value;
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return value;
  }
}
