package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;

public class EventAvailableFunction extends EntityAvailableFunction
{
  public EventAvailableFunction(String name, String group)
  {
    super(name, group, "String context, String event [, String schema]", Cres.get().getString("fDescEventAvailable"));
  }
  
  @Override
  protected boolean hasEntity(Evaluator evaluator, Context con, Object... parameters)
  {
    EventDefinition def = con.getEventDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null;
  }
}
