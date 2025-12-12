package com.tibbo.aggregate.common.expression.function.date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.TimeUnitsManager;
import com.tibbo.aggregate.common.util.Util;

public class PrintPeriodFunction extends AbstractFunction
{
  public PrintPeriodFunction()
  {
    super("printPeriod", Function.GROUP_DATE_TIME_PROCESSING, "Long period", "String", Cres.get().getString("fDescPrintPeriod"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    Number period = Util.convertToNumber(parameters[0], false, false);
    
    TimeUnitsManager tum = new TimeUnitsManager();
    
    if (parameters.length > 1)
    {
      tum.setMinUnit(Util.convertToNumber(parameters[1], true, false).intValue());
    }
    
    if (parameters.length > 2)
    {
      tum.setMaxUnit(Util.convertToNumber(parameters[2], true, false).intValue());
    }
    
    return tum.createTimeString(period.longValue());
  }
}
