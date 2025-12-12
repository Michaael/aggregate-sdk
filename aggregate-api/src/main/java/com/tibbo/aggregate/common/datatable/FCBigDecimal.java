package com.tibbo.aggregate.common.datatable;

import java.math.*;

import com.tibbo.aggregate.common.datatable.converter.*;

public class FCBigDecimal extends SimpleFormatConverter<BigDecimal>
{
  public FCBigDecimal()
  {
    super(BigDecimal.class);
  }
  
  @Override
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.DOUBLE_FIELD);
  }
  
  @Override
  public BigDecimal simpleToBean(Object value)
  {
    if (value == null)
    {
      return null;
    }
    return new BigDecimal(((Double) value).doubleValue());
  }
  
  public Object convertToTable(BigDecimal value, TableFormat format)
  {
    return value;
  }
}
