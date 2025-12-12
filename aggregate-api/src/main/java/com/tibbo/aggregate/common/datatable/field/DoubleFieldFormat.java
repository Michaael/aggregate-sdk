package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class DoubleFieldFormat extends FieldFormat<Double>
{
  public DoubleFieldFormat(String name)
  {
    super(name);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.DOUBLE_FIELD;
  }
  
  @Override
  public Class<Double> getFieldClass()
  {
    return double.class;
  }
  
  @Override
  public Class<Double> getFieldWrappedClass()
  {
    return Double.class;
  }
  
  @Override
  public Double getNotNullDefault()
  {
    return (double) 0;
  }
  
  @Override
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof Double))
    {
      value = Util.convertToNumber(value, true, false).doubleValue();
    }
    
    return value;
  }
  
  @Override
  public Double valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if (value.length() == 0)
    {
      return 0d;
    }
    try
    {
      return new Double(value);
    }
    catch (NumberFormatException ex)
    {
      return Util.convertToNumber(value, validate, false).doubleValue();
    }
  }
  
  @Override
  public String valueToString(Double value, ClassicEncodingSettings settings)
  {
    return value == null ? null : value.toString();
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_BAR, EDITOR_INSTANCE, EDITOR_FORMAT_STRING);
  }
}
