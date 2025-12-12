package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class ValidateFunction extends AbstractFunction
{
  public ValidateFunction()
  {
    super("validate", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table [, Boolean throwErrors]", "String", Cres.get().getString("fDescValidate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]);
    
    boolean throwErrors = false;
    if (parameters.length > 1)
    {
      throwErrors = Util.convertToBoolean(parameters[1], false, false);
    }
    
    try
    {
      table.validate(evaluator.getDefaultResolver().getDefaultContext(), evaluator.getDefaultResolver().getContextManager(), evaluator.getDefaultResolver().getCallerController());
      
      return throwErrors ? table : null;
    }
    catch (Exception ex)
    {
      if (throwErrors)
      {
        throw new EvaluationException(ex.getMessage(), ex);
      }
      else
      {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
      }
    }
  }
}