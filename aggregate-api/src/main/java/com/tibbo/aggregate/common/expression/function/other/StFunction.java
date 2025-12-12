package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class StFunction extends AbstractFunction
{
  public StFunction()
  {
    super("st", Function.GROUP_OTHER, "String variable, Object value", "Object", Cres.get().getString("fDescSt"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, true, parameters);
    
    String variable = parameters[0].toString();
    
    Object value = parameters[1];
    
    if (environment != null && environment.getEnvironment() != null)
    {
      environment.getEnvironment().put(variable, value);
    }
    
    return value;
  }
}
