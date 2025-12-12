package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class FailFunction extends AbstractFunction
{
  public FailFunction()
  {
    super("fail", Function.GROUP_OTHER, "Boolean condition, String message[, Object result]", "Object", Cres.get().getString("fDescFail"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    boolean condition = Util.convertToBoolean(parameters[0], true, false);
    
    if (condition)
    {
      throw new EvaluationException(Util.convertToString(parameters[1], true, false));
    }
    
    if (parameters.length > 2)
    {
      return parameters[2];
    }
    else
    {
      return null;
    }
  }
}