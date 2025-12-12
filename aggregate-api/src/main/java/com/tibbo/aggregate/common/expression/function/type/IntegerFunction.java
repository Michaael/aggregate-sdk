package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class IntegerFunction extends AbstractFunction
{
  public IntegerFunction()
  {
    super("integer", Function.GROUP_TYPE_CONVERSION, "Object value [, Integer radix]", "Integer", Cres.get().getString("fDescInteger"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, true, parameters);
    
    if (parameters.length >= 2)
    {
      int radix = Util.convertToNumber(parameters[1], true, false).intValue();
      String source = parameters[0].toString();
      return Integer.parseInt(source, radix);
    }
    else
    {
      Number n = Util.convertToNumber(parameters[0], true, false);
      
      return n.intValue();
    }
  }
}
