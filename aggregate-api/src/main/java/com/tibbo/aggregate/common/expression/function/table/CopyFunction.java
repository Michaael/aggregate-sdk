package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class CopyFunction extends AbstractFunction
{
  public CopyFunction()
  {
    super("copy", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable source, DataTable target", "DataTable", Cres.get().getString("fDescCopy"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    checkParameterType(1, parameters[1], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    DataTable target = ((DataTable) parameters[1]).cloneIfImmutable();
    
    DataTableReplication.copy(source, target, true, true);
    
    return target;
  }
}
