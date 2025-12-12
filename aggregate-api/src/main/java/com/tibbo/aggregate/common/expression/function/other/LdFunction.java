package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class LdFunction extends AbstractFunction
{
  public LdFunction()
  {
    super("ld", Function.GROUP_OTHER, "String variable", "Object", Cres.get().getString("fDescLd"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    String variable = parameters[0].toString();
    
    if (environment != null && environment.getEnvironment() != null && environment.getEnvironment().containsKey(variable))
    {
      return environment.getEnvironment().get(variable);
    }
    
    throw new EvaluationException(Cres.get().getString("exprEnvVarNotFound") + variable);
  }
}
