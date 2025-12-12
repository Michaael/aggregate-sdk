package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCByte extends SimpleFormatConverter<Byte>
{
  public FCByte()
  {
    super(Byte.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.INTEGER_FIELD);
  }
  
  public Byte simpleToBean(Object value)
  {
    return ((Integer) value).byteValue();
  }
  
  public Object convertToTable(Byte value, TableFormat format)
  {
    return new Integer(value);
  }
}
