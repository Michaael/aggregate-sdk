package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public class FCSimpleInteger extends SimpleFormatConverter
{
  public FCSimpleInteger()
  {
    super(int.class);
  }
  
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.INTEGER_FIELD);
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
