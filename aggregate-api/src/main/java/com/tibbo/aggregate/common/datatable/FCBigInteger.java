package com.tibbo.aggregate.common.datatable;

import java.math.*;

import com.tibbo.aggregate.common.datatable.converter.*;

public class FCBigInteger extends SimpleFormatConverter<BigInteger>
{
  public FCBigInteger()
  {
    super(BigInteger.class);
  }
  
  @Override
  public FieldFormat createFieldFormat(String name)
  {
    return FieldFormat.create(name, FieldFormat.STRING_FIELD);
  }
  
  @Override
  public BigInteger simpleToBean(Object value)
  {
    return new BigInteger(((String) value));
  }
  
  public Object convertToTable(BigInteger value, TableFormat format)
  {
    return value.toString();
  }
}
