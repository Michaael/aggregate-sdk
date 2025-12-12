package com.tibbo.aggregate.common.expression.function.date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.DateUtils;
import com.tibbo.aggregate.common.util.Util;

public class DateCreateFunction extends AbstractFunction
{
  public DateCreateFunction()
  {
    super("date", Function.GROUP_DATE_TIME_PROCESSING, "Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second [, String timezone]", "Date",
        Cres.get().getString("fDescDate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(6, false, parameters);
    
    int year = Util.convertToNumber(parameters[0], true, false).intValue();
    int month = Util.convertToNumber(parameters[1], true, false).intValue();
    int day = Util.convertToNumber(parameters[2], true, false).intValue();
    int hour = Util.convertToNumber(parameters[3], true, false).intValue();
    int minute = Util.convertToNumber(parameters[4], true, false).intValue();
    int second = Util.convertToNumber(parameters[5], true, false).intValue();
    int millisecond = 0;
    
    TimeZone tz = DateUtils.UTC_TIME_ZONE;
    if (parameters.length > 6)
    {
      if (parameters[6] instanceof Long)
        millisecond = Util.convertToNumber(parameters[6], true, false).intValue();
      else if (parameters[6] instanceof String)
        tz = TimeZone.getTimeZone(parameters[6].toString());
    }
    if (parameters.length > 7)
    {
      tz = TimeZone.getTimeZone(parameters[7].toString());
    }
    
    GregorianCalendar gc = new GregorianCalendar(tz);
    gc.set(year, month, day, hour, minute, second);
    gc.set(Calendar.MILLISECOND, millisecond);
    
    return gc.getTime();
  }
}
