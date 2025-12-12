package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;

public class VariableGroupFunction extends EntityGroupFunction
{
  public VariableGroupFunction()
  {
    super("variableGroup", Function.GROUP_CONTEXT_RELATED, "String context, String variable [, String schema]", Cres.get().getString("fDescVariableGroup"));
  }
  
  @Override
  protected String getGroup(Evaluator evaluator, Context con, Object... parameters)
  {
    VariableDefinition def = con.getVariableDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null ? def.getGroup() : null;
  }
}
