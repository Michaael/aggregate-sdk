package com.tibbo.aggregate.common.expression.function.number;

import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.JavaMethodFunction;

public class JavaMathFunction extends JavaMethodFunction
{
  public JavaMathFunction(String method, String parametersFootprint, String returnValue, String description)
  {
    super(Math.class.getName(), method, method, Function.GROUP_NUMBER_PROCESSING, parametersFootprint, returnValue, description);
  }
}
