package com.tibbo.aggregate.common.expression.function.string;

import java.util.Arrays;
import java.util.Formatter;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class FormatFunction extends AbstractFunction
{
  public FormatFunction()
  {
    super("format", Function.GROUP_STRING_PROCESSING, "String pattern, Object parameter1, ...", "String", Cres.get().getString("fDescFormat"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, true, parameters);
    
    String pattern = parameters[0].toString();
    Object[] data = Arrays.copyOfRange(parameters, 1, parameters.length);
    
    Formatter formatter = new Formatter();
    
    String result = formatter.format(pattern, data).toString();
    
    formatter.close();
    
    return result;
  }
}
