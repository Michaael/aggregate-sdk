package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.*;

public abstract class SimpleFormatConverter<T> extends AbstractFormatConverter<T>
{
  public SimpleFormatConverter(Class valueClass)
  {
    super(valueClass);
  }
  
  public T convertToBean(Object value, T originalValue)
  {
    if (value instanceof DataTable)
    {
      value = ((DataTable) value).get();
    }
    
    return simpleToBean(value);
  }
  
  protected abstract T simpleToBean(Object value);
}
