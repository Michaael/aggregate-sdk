package com.tibbo.aggregate.common.expression.function.string;

import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.JavaMethodFunction;

public class CharacterFunction extends JavaMethodFunction
{
  public CharacterFunction(String method)
  {
    this(method, null);
  }
  
  public CharacterFunction(String method, String description)
  {
    super(Character.class.getName(), method, method,true, Function.GROUP_STRING_PROCESSING, "String character", "Boolean", description);
  }
  
  @Override
  protected Object convertParameter(int i, Object value)
  {
    if (value instanceof String)
    {
      return value.toString().charAt(0);
    }
    
    return super.convertParameter(i, value);
  }
}
