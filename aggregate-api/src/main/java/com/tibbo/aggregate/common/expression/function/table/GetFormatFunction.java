package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class GetFormatFunction extends AbstractFunction
{
  public GetFormatFunction()
  {
    super("getFormat", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table [, Boolean useVisibleSeparators]", "String", Cres.get().getString("fDescGetFormat"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, true, parameters);
    
    if (parameters[0] == null)
      return null;
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = (DataTable) parameters[0];
    
    boolean useVisibleSeparators = parameters.length > 1 ? Util.convertToBoolean(parameters[1], false, false) : false;
    
    return table.getFormat().encode(useVisibleSeparators);
  }
}
