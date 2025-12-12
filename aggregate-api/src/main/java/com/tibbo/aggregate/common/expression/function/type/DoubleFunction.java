package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class DoubleFunction extends TypeConversionFunction
{
  public DoubleFunction()
  {
    super("double","Double", Cres.get().getString("fDescDouble"));
  }
  
  @Override
  public Object convert(Object parameter) throws EvaluationException
  {
    Number n = Util.convertToNumber(parameter, true, false);
    
    return n.doubleValue();
  }
}
