package com.tibbo.aggregate.common.expression.function.string;

import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.JavaMethodFunction;

public class JavaStringFunction extends JavaMethodFunction
{
  public JavaStringFunction(String method, String parametersFootprint, String returnValue, String description)
  {
    this(method, method, parametersFootprint, returnValue, description);
  }

  public JavaStringFunction(String name, String method, String parametersFootprint, String returnValue, String description)
  {
    super(String.class.getName(), name, method, false, Function.GROUP_STRING_PROCESSING, parametersFootprint, returnValue, description);
  }
}
