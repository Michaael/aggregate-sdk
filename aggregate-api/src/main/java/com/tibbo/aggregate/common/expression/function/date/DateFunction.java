package com.tibbo.aggregate.common.expression.function.date;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.DateUtils;
import com.tibbo.aggregate.common.util.Util;

public abstract class DateFunction extends AbstractFunction
{
  public DateFunction(String name, String returnValue, String description)
  {
    super(name, Function.GROUP_DATE_TIME_PROCESSING, "Date date [, String timezone]", returnValue, description);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    TimeZone tz = DateUtils.UTC_TIME_ZONE;
    if (parameters.length > 1)
    {
      String zone = parameters[1].toString();
      
      tz = TimeZone.getTimeZone(zone);
      // See TimeZone.getTimeZone(String zone, Boolean fallback)
      final boolean fallbackResult = !tz.getID().equals(zone) && "GMT".equals(tz.getID());
      if (fallbackResult)
      {
        throw new EvaluationException("Invalid timezone code: " + zone);
      }
    }
    
    GregorianCalendar calendar = new GregorianCalendar(tz);
    
    calendar.setTime(Util.convertToDate(parameters[0], true, false));
    
    return execute(calendar, parameters);
  }
  
  public abstract Object execute(GregorianCalendar calendar, Object... parameters) throws EvaluationException;
}
