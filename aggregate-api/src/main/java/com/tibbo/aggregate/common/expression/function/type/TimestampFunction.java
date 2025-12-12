package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class TimestampFunction extends TypeConversionFunction
{
  public TimestampFunction()
  {
    super("timestamp", "Date", Cres.get().getString("fDescTimestamp"));
  }
  
  @Override
  public Object convert(Object parameter) throws EvaluationException
  {
    return parameter != null ? Util.convertToDate(parameter, false, false) : null;
  }
}
