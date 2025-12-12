package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleByte extends SimpleFormatConverter
{
  public FCSimpleByte()
  {
    super(byte.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.INTEGER_FIELD);
  }
  
  public Object simpleToBean(Object value)
  {
    return ((Integer) value).byteValue();
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return new Byte(value.toString());
  }
}
