package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class FloatFieldFormat extends FieldFormat<Float>
{
  public FloatFieldFormat(String name)
  {
    super(name);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.FLOAT_FIELD;
  }
  
  @Override
  public Class<Float> getFieldClass()
  {
    return float.class;
  }
  
  @Override
  public Class<Float> getFieldWrappedClass()
  {
    return Float.class;
  }
  
  @Override
  public Float getNotNullDefault()
  {
    return (float) 0;
  }
  
  @Override
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof Float))
    {
      value = Util.convertToNumber(value, true, false).floatValue();
    }
    
    return value;
  }
  
  @Override
  public Float valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if (value.length() == 0)
    {
      return 0f;
    }
    try
    {
      return new Float(value);
    }
    catch (NumberFormatException ex)
    {
      return Util.convertToNumber(value, validate, false).floatValue();
    }
  }
  
  @Override
  public String valueToString(Float value, ClassicEncodingSettings settings)
  {
    return value == null ? null : value.toString();
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_BAR, EDITOR_BYTES, EDITOR_INSTANCE, EDITOR_FORMAT_STRING);
  }
}
