package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class JoinFunction extends AbstractFunction
{
  public JoinFunction()
  {
    super("join", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable left, DataTable right", "DataTable", Cres.get().getString("fDescJoin"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    checkParameterType(1, parameters[1], DataTable.class);
    
    DataTable leftTable = (DataTable) parameters[0];
    DataTable rightTable = (DataTable) parameters[1];
    
    TableFormat newFormat = joinTableFormats(leftTable.getFormat(), rightTable.getFormat());
    DataTable resultDataTable = new SimpleDataTable(newFormat);
    DataTableReplication.copy(rightTable, resultDataTable, true, true);
    DataTableReplication.copy(leftTable, resultDataTable, true, true);
    
    return resultDataTable;
  }
  
  private TableFormat joinTableFormats(TableFormat newFormat, TableFormat tableFormat)
  {
    TableFormat result = newFormat.clone();
    
    long min1 = newFormat.getMinRecords();
    long max1 = newFormat.getMaxRecords();
    long min2 = tableFormat.getMinRecords();
    long max2 = tableFormat.getMaxRecords();
    long min = Math.max(min1, min2);
    long max = Math.max(max1, max2);
    
    result.setMinRecords((int) min);
    result.setMaxRecords((int) max);
    
    for (FieldFormat field : tableFormat.getFields())
    {
      if (!newFormat.hasField(field.getName()))
        result.addField(field.clone());
    }
    return result;
  }
}
