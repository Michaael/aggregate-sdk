package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;

public class VariableReadableFunction extends EntityAvailableFunction
{
  public VariableReadableFunction(String name, String group)
  {
    super(name, group, "String context, String variable [, String schema]", Cres.get().getString("fDescVariableReadable"));
  }
  
  @Override
  protected boolean hasEntity(Evaluator evaluator, Context con, Object... parameters)
  {
    VariableDefinition def = con.getVariableDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    
    return def != null && def.isReadable();
  }
}
