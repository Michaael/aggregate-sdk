package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DrFunction extends AbstractFunction
{
  public DrFunction()
  {
    super("dr", Function.GROUP_CONTEXT_RELATED, "", "Integer", Cres.get().getString("fDescDr"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    return evaluator.getDefaultResolver().getDefaultRow();
  }
}
