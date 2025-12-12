package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;

public class FunctionAvailableFunction extends EntityAvailableFunction
{
  public FunctionAvailableFunction(String name, String group)
  {
    super(name, group, "String context, String function [, String schema]", Cres.get().getString("fDescFunctionAvailable"));
  }
  
  @Override
  protected boolean hasEntity(Evaluator evaluator, Context con, Object... parameters)
  {
    FunctionDefinition def = con.getFunctionDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null;
  }
}
