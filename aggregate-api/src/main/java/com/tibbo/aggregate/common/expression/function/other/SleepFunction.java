package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SleepFunction extends AbstractFunction
{
  public SleepFunction()
  {
    super("sleep", Function.GROUP_OTHER, "Integer milliseconds", "Null", Cres.get().getString("fDescSleep"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    Number n = Util.convertToNumber(parameters[0], true, false);
    
    try
    {
      Thread.currentThread().sleep(n.intValue());
    }
    catch (InterruptedException ex)
    {
      throw new EvaluationException(ex);
    }
    
    return null;
  }
}
