package com.tibbo.aggregate.common.datatable.converter;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public interface FormatConverter<T>
{
  Class<T> getValueClass();
  
  TableFormat getFormat();
  
  FieldFormat<T> createFieldFormat(String name);
  
  T instantiate(DataRecord source) throws InstantiationException;
  
  T clone(T value, boolean useConversion);
  
  Object convertToTable(T value);
  
  Object convertToTable(T value, TableFormat format);
  
  T convertToBean(Object value, T originalValue);
}
