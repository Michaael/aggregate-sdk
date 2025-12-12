package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DescribeFunction extends AbstractFunction
{
  public DescribeFunction()
  {
    super("describe", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String field1, String description1, String field2, String description2, ...", "DataTable", Cres.get().getString("fDescDescribe"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]).clone();
    
    TableFormat clone = table.getFormat().clone();
    
    for (int i = 1; i < parameters.length - 1; i = i + 2)
    {
      String name = parameters[i].toString();
      String description = parameters[i + 1].toString();
      FieldFormat ff = clone.getField(name);
      if (ff != null)
      {
        ff.setDescription(description);
      }
    }
    
    table.setFormat(clone);
    
    return table;
  }
}
