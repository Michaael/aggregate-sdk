package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public abstract class TypeConversionFunction extends AbstractFunction
{
  public TypeConversionFunction(String name, String returnValue, String description)
  {
    super(name, Function.GROUP_TYPE_CONVERSION, "Object value", returnValue, description);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, true, parameters);
    
    return convert(parameters[0]);
  }
  
  public abstract Object convert(Object parameter) throws EvaluationException;
}
