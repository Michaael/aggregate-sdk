package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleShort extends SimpleFormatConverter
{
  public FCSimpleShort()
  {
    super(short.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.INTEGER_FIELD);
  }
  
  public Object simpleToBean(Object value)
  {
    return ((Integer) value).shortValue();
  }
  
  public Object convertToTable(Object value, TableFormat format)
  {
    return new Integer((Short) value);
  }
}
