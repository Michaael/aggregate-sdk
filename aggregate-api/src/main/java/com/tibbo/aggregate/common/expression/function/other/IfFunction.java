package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class IfFunction extends AbstractFunction
{
  public IfFunction()
  {
    super("if", Function.GROUP_OTHER, "Boolean condition, Object resultIfTrue, Object resultIfFalse", "Object", Cres.get().getString("fDescIf"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);

    return Util.convertToBoolean(parameters[0], true, false) ? parameters[1] : parameters[2];
  }
}