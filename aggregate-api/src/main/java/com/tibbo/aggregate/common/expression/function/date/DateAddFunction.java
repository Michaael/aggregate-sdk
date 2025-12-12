package com.tibbo.aggregate.common.expression.function.date;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.DateUtils;
import com.tibbo.aggregate.common.util.TimeHelper;
import com.tibbo.aggregate.common.util.TimeUnit;
import com.tibbo.aggregate.common.util.Util;

public class DateAddFunction extends AbstractFunction
{
  public DateAddFunction()
  {
    super("dateAdd", Function.GROUP_DATE_TIME_PROCESSING, "Date date, Integer count, String unit [, String timezone]", "Date", Cres.get().getString("fDescDateAdd"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    Date date = Util.convertToDate(parameters[0], true, false);
    int count = Util.convertToNumber(parameters[1], false, false).intValue();
    TimeUnit unit = TimeHelper.getTimeUnit(parameters[2].toString());
    
    TimeZone tz = DateUtils.UTC_TIME_ZONE;
    if (parameters.length > 3)
    {
      tz = TimeZone.getTimeZone(parameters[3].toString());
    }
    
    Calendar cal = Calendar.getInstance(tz);
    cal.setTime(date);
    cal.add(unit.getCalendarField(), count);
    
    return cal.getTime();
  }
}
