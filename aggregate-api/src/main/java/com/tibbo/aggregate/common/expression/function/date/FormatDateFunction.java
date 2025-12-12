package com.tibbo.aggregate.common.expression.function.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.DateUtils;
import com.tibbo.aggregate.common.util.Util;

public class FormatDateFunction extends AbstractFunction
{
  public FormatDateFunction()
  {
    super("formatDate", Function.GROUP_DATE_TIME_PROCESSING, "Date date, String pattern [, String timezone]", "String", Cres.get().getString("fDescFormatDate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    Date date = Util.convertToDate(parameters[0], true, false);
    String pattern = parameters[1].toString();
    
    if (date == null)
    {
      return "";
    }
    
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
    if (parameters.length > 2)
    {
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone(parameters[2].toString()));
    }
    else
    {
      simpleDateFormat.setTimeZone(DateUtils.UTC_TIME_ZONE);
    }
    
    return simpleDateFormat.format(date);
  }
}
