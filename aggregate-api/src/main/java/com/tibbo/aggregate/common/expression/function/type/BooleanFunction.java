package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class BooleanFunction extends TypeConversionFunction
{
  public BooleanFunction()
  {
    super("boolean", "Boolean", Cres.get().getString("fDescBoolean"));
  }
  
  @Override
  public Object convert(Object parameter) throws EvaluationException
  {
    return Util.convertToBoolean(parameter, true, false);
  }
}
