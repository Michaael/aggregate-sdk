package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.Util;

public class EventFormatFunction extends EntityFormatFunction
{
  public EventFormatFunction()
  {
    super("eventFormat", "String context, String event [, String schema [, Boolean asTable]]", Cres.get().getString("fDescEventFormat"));
  }
  
  @Override
  protected Object getFormat(Evaluator evaluator, Context con, Object... parameters)
  {
    EventDefinition def = con.getEventDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());

    boolean asTable = parameters.length >= 4 ? Util.convertToBoolean(parameters[3], false, false) : false;

    return def != null ? getFormatPresentation(def.getFormat(), asTable) : null;

  }
}
