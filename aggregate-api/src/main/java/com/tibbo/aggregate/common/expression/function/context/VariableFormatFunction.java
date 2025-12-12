package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.VariableDefinition;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.util.Util;

public class VariableFormatFunction extends EntityFormatFunction
{
  public VariableFormatFunction()
  {
    super("variableFormat", "String context, String variable [, String schema [, Boolean asTable]]", Cres.get().getString("fDescVariableFormat"));
  }
  
  @Override
  protected Object getFormat(Evaluator evaluator, Context con, Object... parameters)
  {
    VariableDefinition def = con.getVariableDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());
    boolean asTable;
    if(parameters.length == 3 &&
            (parameters[2] instanceof Boolean)) {
      asTable = Util.convertToBoolean(parameters[2], false, false);
    } else {
      asTable = parameters.length >= 4 ? Util.convertToBoolean(parameters[3], false, false) : false;
    }

    return def != null ? getFormatPresentation(def.getFormat(), asTable) : null;
  }

}
