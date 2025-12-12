package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCDouble extends SimpleFormatConverter<Double>
{
  public FCDouble()
  {
    super(Double.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.DOUBLE_FIELD);
  }
  
  public Double simpleToBean(Object value)
  {
    return ((Number) value).doubleValue();
  }
  
  public Object convertToTable(Double value, TableFormat format)
  {
    return value;
  }
}
