package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCShort extends SimpleFormatConverter<Short>
{
  public FCShort()
  {
    super(Short.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.INTEGER_FIELD);
  }
  
  public Short simpleToBean(Object value)
  {
    return ((Integer) value).shortValue();
  }
  
  public Object convertToTable(Short value, TableFormat format)
  {
    return new Integer(value);
  }
}
