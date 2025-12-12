package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SortFunction extends AbstractFunction
{
  public SortFunction()
  {
    super("sort", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String field, boolean ascending", "DataTable", Cres.get().getString("fDescSort"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    
    String field = parameters[1].toString();
    
    boolean ascending = Util.convertToBoolean(parameters[2], false, false);
    
    DataTable result = source.clone();
    
    result.sort(field, ascending);
    
    return result;
  }
}
