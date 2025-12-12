package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;

public class FloatConstantFunction extends AbstractFunction
{
  private final float constant;
  
  public FloatConstantFunction(String name, float constant, String description)
  {
    super(name, Function.GROUP_NUMBER_PROCESSING, "", "Float", description);
    this.constant = constant;
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    return constant;
  }
}
