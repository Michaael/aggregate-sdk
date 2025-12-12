package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.expression.*;

public class StringFunction extends TypeConversionFunction
{
  public StringFunction()
  {
    super("string", "String", Cres.get().getString("fDescString"));
  }
  
  @Override
  public Object convert(Object parameter) throws EvaluationException
  {
    if (parameter == null)
      return null;
    
    return parameter instanceof Data ? ((Data) parameter).toCleanString() : parameter.toString();
  }
}
