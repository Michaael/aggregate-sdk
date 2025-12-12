package com.tibbo.aggregate.common.datatable.field;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.util.*;

public class LongFieldFormat extends FieldFormat<Long>
{
  public static final String EDITOR_PERIOD = "period";

  public LongFieldFormat(String name)
  {
    super(name);
  }
  
  @Override
  public char getType()
  {
    return FieldFormat.LONG_FIELD;
  }
  
  @Override
  public Class<Long> getFieldClass()
  {
    return long.class;
  }
  
  @Override
  public Class<Long> getFieldWrappedClass()
  {
    return Long.class;
  }
  
  @Override
  public Long getNotNullDefault()
  {
    return 0L;
  }
  
  @Override
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null && !(value instanceof Long))
    {
      value = Util.convertToNumber(value, true, false).longValue();
    }
    
    return value;
  }
  
  @Override
  public Long valueFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    try
    {
      return new Long(value);
    }
    catch (NumberFormatException ex)
    {
      return Util.convertToNumber(value, validate, false).longValue();
    }
  }
  
  @Override
  public String valueToString(Long value, ClassicEncodingSettings settings)
  {
    return value == null ? null : value.toString();
  }
  
  @Override
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(EDITOR_LIST, EDITOR_PERIOD, EDITOR_BAR, EDITOR_BYTES, EDITOR_INSTANCE, EDITOR_FOREIGN_INSTANCE, EDITOR_FORMAT_STRING);
  }
  
  public static String encodePeriodEditorOptions(int minUnit, int maxUnit)
  {
    return minUnit + " " + String.valueOf(maxUnit);
  }
}
