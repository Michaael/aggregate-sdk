package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.FunctionDefinition;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;

public class FunctionGroupFunction extends EntityGroupFunction
{
  public FunctionGroupFunction()
  {
    super("functionGroup", Function.GROUP_CONTEXT_RELATED, "String context, String function [, String schema]", Cres.get().getString("fDescFunctionGroup"));
  }
  
  @Override 
  protected String getGroup(Evaluator evaluator, Context con, Object... parameters)
  {
    FunctionDefinition def = con.getFunctionDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null ? def.getGroup() : null;
  }
}
