package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class IntFieldFormat extends FieldFormat<Integer>
{
  public static final String EDITOR_SPINNER = "spinner";
  public static final String EDITOR_EVENT_LEVEL = "eventLevel";

  public IntFieldFormat(String name)
  {
    super(name);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.INTEGER_FIELD;
  }
  
  @Override
  public Class<Integer> getFieldClass()
  {
    return int.class;
  }
  
  @Override
  public Class<Integer> getFieldWrappedClass()
  {
    return Integer.class;
  }
  
  @Override
  public Integer getNotNullDefault()
  {
    return 0;
  }
  
  @Override
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof Integer))
    {
      value = Util.convertToNumber(value, true, false).intValue();
    }
    
    return value;
  }
  
  @Override
  public Integer valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if (value.length() == 0)
    {
      return 0;
    }
    try
    {
      return new Integer(value);
    }
    catch (NumberFormatException ex)
    {
      return Util.convertToNumber(value, validate, false).intValue();
    }
  }
  
  @Override
  public String valueToString(Integer value, ClassicEncodingSettings settings)
  {
    return value == null ? null : value.toString();
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_BAR, EDITOR_BYTES, EDITOR_SPINNER, EDITOR_EVENT_LEVEL, EDITOR_INSTANCE, EDITOR_FORMAT_STRING);
  }
}
