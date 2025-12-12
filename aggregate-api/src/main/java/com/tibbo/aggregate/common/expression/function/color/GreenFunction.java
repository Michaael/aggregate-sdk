package com.tibbo.aggregate.common.expression.function.color;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;

public class GreenFunction extends RgbFunction
{
  public GreenFunction()
  {
    super("green", Cres.get().getString("fDescGreen"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    return color(parameters).getGreen();
  }
}
