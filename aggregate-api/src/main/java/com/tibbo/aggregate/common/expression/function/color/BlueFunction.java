package com.tibbo.aggregate.common.expression.function.color;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;

public class BlueFunction extends RgbFunction
{
  public BlueFunction()
  {
    super("blue", Cres.get().getString("fDescBlue"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    return color(parameters).getBlue();
  }
}
