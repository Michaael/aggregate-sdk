package com.tibbo.aggregate.common.expression.function.color;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;

public class RedFunction extends RgbFunction
{
  public RedFunction()
  {
    super("red", Cres.get().getString("fDescRed"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    return color(parameters).getRed();
  }
}
