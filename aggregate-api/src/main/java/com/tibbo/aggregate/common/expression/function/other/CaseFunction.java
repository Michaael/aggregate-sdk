package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class CaseFunction extends AbstractFunction
{
  public CaseFunction()
  {
    super("case", Function.GROUP_OTHER, "Object defaultResult, Boolean condition1, Object result1 [, Boolean condition2, Object result2, ...]", "Object", Cres.get().getString("fDescCase"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, true, parameters);
    
    for (int caseNum = 1; caseNum <= (parameters.length - 1) / 2; caseNum++)
    {
      boolean condition = Util.convertToBoolean(parameters[1 + (caseNum - 1) * 2], true, false);
      
      if (condition)
      {
        return parameters[1 + (caseNum - 1) * 2 + 1];
      }
    }
    
    return parameters[0];
  }
}
