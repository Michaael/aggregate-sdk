package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class SetFunction extends AbstractFunction
{
  public SetFunction()
  {
    super("set", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String field, Integer row, Object value", "Object", Cres.get().getString("fDescSet"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(4, true, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]).cloneIfImmutable();
    
    String field = parameters[1].toString();
    
    int row = Util.convertToNumber(parameters[2], true, false).intValue();
    
    table.getRecord(row).setValueSmart(field, parameters[3]);
    
    return table;
  }
}
