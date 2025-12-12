package com.tibbo.aggregate.common.expression.function.number;

import java.text.DecimalFormat;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class FormatNumberFunction extends AbstractFunction
{
  public FormatNumberFunction()
  {
    super("formatNumber", Function.GROUP_NUMBER_PROCESSING, "Number number, String pattern", "String", Cres.get().getString("fDescFormatNumber"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, true, parameters);
    
    Number number = Util.convertToNumber(parameters[0], true, true);
    String pattern = parameters[1].toString();
    
    if (number == null)
    {
      return "";
    }
    
    return new DecimalFormat(pattern).format(number.doubleValue());
  }
}
