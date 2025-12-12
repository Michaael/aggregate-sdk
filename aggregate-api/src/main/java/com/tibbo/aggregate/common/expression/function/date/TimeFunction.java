package com.tibbo.aggregate.common.expression.function.date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class TimeFunction extends AbstractFunction
{
  public TimeFunction()
  {
    super("time", Function.GROUP_DATE_TIME_PROCESSING, "Date date", "Long", Cres.get().getString("fDescTime"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    return Util.convertToDate(parameters[0], true, false).getTime();
  }
}
