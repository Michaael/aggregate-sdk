package com.tibbo.aggregate.common.expression.function.number;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.AbstractEvaluatingVisitor;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class GeFunction extends AbstractFunction
{
  public GeFunction()
  {
    super("ge", Function.GROUP_NUMBER_PROCESSING, "Long number1, Long number2", "Boolean", Cres.get().getString("fDescGe"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    return AbstractEvaluatingVisitor.compare(parameters[0], parameters[1]) >= 0;
  }
}
