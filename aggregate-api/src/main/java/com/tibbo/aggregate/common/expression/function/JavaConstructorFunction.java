package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.expression.*;
import org.apache.commons.beanutils.*;

public class JavaConstructorFunction extends AbstractFunction
{
  private String clazz;

  
  public JavaConstructorFunction(String name, String clazz, String category, String returnValue, String description)
  {
    super(name, category, "", returnValue, description);
    this.clazz = clazz;
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    try
    {
      return ConstructorUtils.invokeConstructor(Class.forName(clazz), parameters);
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
