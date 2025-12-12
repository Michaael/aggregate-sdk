package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.Util;

public class FunctionInputFormatFunction extends EntityFormatFunction
{
  public FunctionInputFormatFunction()
  {
    super("functionInputFormat", "String context, String function [, String schema [, Boolean asTable]]", Cres.get().getString("fDescFunctionInputFormat"));
  }
  
  @Override
  protected Object getFormat(Evaluator evaluator, Context con, Object... parameters)
  {
    FunctionDefinition def = con.getFunctionDefinition(parameters[1].toString(), evaluator.getDefaultResolver().getCallerController());

    boolean asTable = parameters.length >= 4 ? Util.convertToBoolean(parameters[3], false, false) : false;

    return def != null ? getFormatPresentation(def.getInputFormat(), asTable) : null;

  }
}
