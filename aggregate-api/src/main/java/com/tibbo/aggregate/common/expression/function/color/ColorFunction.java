package com.tibbo.aggregate.common.expression.function.color;

import java.awt.*;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class ColorFunction extends AbstractFunction
{
  public ColorFunction()
  {
    super("color", Function.GROUP_COLOR_PROCESSING, "Integer red, Integer green, Integer blue", "Color", Cres.get().getString("fDescColor"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    if (parameters.length != 3)
    {
      throw new EvaluationException("Invalid parameter count, need 3 but found " + parameters.length);
    }
    
    Number r = Util.convertToNumber(parameters[0], true, false);
    Number g = Util.convertToNumber(parameters[1], true, false);
    Number b = Util.convertToNumber(parameters[2], true, false);
    
    return new Color(r.intValue(), g.intValue(), b.intValue());
  }
}
