package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCNumber extends SimpleFormatConverter<Number>
{
  public FCNumber()
  {
    super(Number.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.FLOAT_FIELD);
  }
  
  public Number simpleToBean(Object value)
  {
    return (Number) value;
  }
  
  public Object convertToTable(Number value, TableFormat format)
  {
    if (value == null)
    {
      return 0f;
    }
    return value.floatValue();
  }
}
