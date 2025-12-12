package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class RemoveColumnsFunction extends AbstractFunction
{
  public RemoveColumnsFunction()
  {
    super("removeColumns", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String column1, String column2, ...", "DataTable", Cres.get().getString("fDescRemoveColumns"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = ((DataTable) parameters[0]).clone();
    
    TableFormat newFormat = table.getFormat().clone();
    
    for (int i = 1; i < parameters.length; i = i + 1)
    {
      String field = parameters[i].toString();
      newFormat.removeField(field);
    }
    
    DataTable newTable = new SimpleDataTable(newFormat);
    
    DataTableReplication.copy(table, newTable, true, true, true);
    
    return newTable;
  }
}
