package com.tibbo.aggregate.common.expression.function.date;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.TimeHelper;
import com.tibbo.aggregate.common.util.TimeUnit;
import com.tibbo.aggregate.common.util.Util;

public class DateDiffFunction extends AbstractFunction
{
  public DateDiffFunction()
  {
    super("dateDiff", Function.GROUP_DATE_TIME_PROCESSING, "Date first, Date second, String unit", "Long", Cres.get().getString("fDescDateDiff"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    TimeUnit unit = TimeHelper.getTimeUnit(parameters[2].toString());
    
    Date first = Util.convertToDate(parameters[0], true, false);
    Date second = Util.convertToDate(parameters[1], true, false);
    
    if (unit.getChronoUnit() != null)
    {
      LocalDateTime firstDateTime = LocalDateTime.ofInstant(first.toInstant(), ZoneId.systemDefault());
      LocalDateTime secondDateTime = LocalDateTime.ofInstant(second.toInstant(), ZoneId.systemDefault());
      return Math.abs(unit.getChronoUnit().between(firstDateTime, secondDateTime));
    }
    
    return (second.getTime() - first.getTime()) / unit.getLength();
  }
}
