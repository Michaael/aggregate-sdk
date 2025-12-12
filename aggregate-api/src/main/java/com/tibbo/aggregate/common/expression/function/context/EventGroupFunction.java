package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.EventDefinition;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;

public class EventGroupFunction extends EntityGroupFunction
{
  public EventGroupFunction()
  {
    super("eventGroup", Function.GROUP_CONTEXT_RELATED, "String context, String event [, String schema]", Cres.get().getString("fDescEventGroup"));
  }
  
  @Override
  protected String getGroup(Evaluator evaluator, Context con, Object... parameters)
  {
    EventDefinition def = con.getEventDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null ? def.getGroup() : null;
  }
}
