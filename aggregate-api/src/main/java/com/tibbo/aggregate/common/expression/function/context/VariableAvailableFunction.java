package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;

public class VariableAvailableFunction extends EntityAvailableFunction
{
  public VariableAvailableFunction()
  {
    super("variableAvailable", Function.GROUP_CONTEXT_RELATED, "String context, String variable [, String schema]", Cres.get().getString("fDescVariableAvailable"));
  }
  
  @Override
  protected boolean hasEntity(Evaluator evaluator, Context con, Object... parameters)
  {
    VariableDefinition def = con.getVariableDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null;
  }
}
