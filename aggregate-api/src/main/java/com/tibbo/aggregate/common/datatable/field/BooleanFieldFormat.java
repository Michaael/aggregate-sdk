package com.tibbo.aggregate.common.datatable.field;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class BooleanFieldFormat extends FieldFormat<Boolean>
{
  public BooleanFieldFormat(String name)
  {
    super(name);
  }
  
  public char getType()
  {
    return FieldFormat.BOOLEAN_FIELD;
  }
  
  public Class getFieldClass()
  {
    return boolean.class;
  }
  
  public Class getFieldWrappedClass()
  {
    return Boolean.class;
  }
  
  public Boolean getNotNullDefault()
  {
    return Boolean.valueOf(false);
  }
  
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof Boolean))
    {
      value = Util.convertToBoolean(value, true, false);
    }
    
    return value;
  }
  
  public Boolean valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    return (value.equals("1") || value.equalsIgnoreCase("true")) ? Boolean.valueOf(true) : Boolean.valueOf(false);
  }
  
  public String valueToString(Boolean value, ClassicEncodingSettings settings)
  {
    return value == null ? null : ((Boolean) value) ? "1" : "0";
  }
}
