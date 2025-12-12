package com.tibbo.aggregate.common.expression.function.color;

import java.awt.*;

import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public abstract class RgbFunction extends AbstractFunction
{
  public RgbFunction(String name, String description)
  {
    super(name, Function.GROUP_COLOR_PROCESSING, "Color color", "Integer", description);
  }
  
  protected Color color(Object[] parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    checkParameterType(0, parameters[0], Color.class);
    return (Color) parameters[0];
  }
}
