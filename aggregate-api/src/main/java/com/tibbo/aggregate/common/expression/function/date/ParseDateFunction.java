package com.tibbo.aggregate.common.expression.function.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.DateUtils;

public class ParseDateFunction extends AbstractFunction
{
  public ParseDateFunction()
  {
    super("parseDate", Function.GROUP_DATE_TIME_PROCESSING, "String source, String pattern [, String timezone]", "String", Cres.get().getString("fDescParseDate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    String source = parameters[0].toString();
    
    String pattern = parameters[1].toString();
    
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
    if (parameters.length > 2)
    {
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone(parameters[2].toString()));
    }
    else
    {
      simpleDateFormat.setTimeZone(DateUtils.UTC_TIME_ZONE);
    }
    
    try
    {
      return simpleDateFormat.parse(source);
    }
    catch (ParseException ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
  }
}
