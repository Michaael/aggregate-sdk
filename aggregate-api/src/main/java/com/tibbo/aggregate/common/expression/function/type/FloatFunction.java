package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class FloatFunction extends TypeConversionFunction
{
  public FloatFunction()
  {
    super("float", "Float", Cres.get().getString("fDescFloat"));
  }
  
  @Override
  public Object convert(Object parameter) throws EvaluationException
  {
    Number n = Util.convertToNumber(parameter, true, false);
    
    return n.floatValue();
  }
}
