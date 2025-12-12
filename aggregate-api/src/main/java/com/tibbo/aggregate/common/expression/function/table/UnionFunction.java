package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
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

public class UnionFunction extends AbstractFunction
{
  public UnionFunction()
  {
    super("union", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable first, DataTable second [, DataTable third, ...]", "DataTable", Cres.get().getString("fDescUnion"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    checkParameterType(1, parameters[1], DataTable.class);
    
    DataTable union = null;
    
    for (int i = 0; i < parameters.length - 1; i++)
    {
      union = i == 0 ? (DataTable) parameters[i] : union;
      DataTable table = (DataTable) parameters[i + 1];
      union = union(union, table);
    }
    
    return union;
  }
  
  private DataTable union(DataTable leftTable, DataTable rightTable)
  {
    TableFormat newFormat;
    DataTable resultDataTable;
    newFormat = joinTableFormats(leftTable.getFormat(), rightTable.getFormat());
    resultDataTable = new SimpleDataTable(newFormat);
    DataTableReplication.copy(leftTable, resultDataTable, true, true);
    
    for (DataRecord rec : rightTable)
    {
      DataRecord newRec = resultDataTable.addRecord();
      DataTableReplication.copyRecord(rec, newRec, true, true);
    }
    return resultDataTable;
  }
  
  private TableFormat joinTableFormats(TableFormat newFormat, TableFormat tableFormat)
  {
    TableFormat result = newFormat.clone();
    
    long min1 = newFormat.getMinRecords();
    long max1 = newFormat.getMaxRecords();
    long min2 = tableFormat.getMinRecords();
    long max2 = tableFormat.getMaxRecords();
    long min = min1 + min2;
    long max = max1 + max2;
    if (min > Integer.MAX_VALUE)
      min = Integer.MAX_VALUE;
    if (max > Integer.MAX_VALUE)
      max = Integer.MAX_VALUE;
    
    result.setMinRecords((int) min);
    result.setMaxRecords((int) max);
    
    for (FieldFormat field : tableFormat.getFields())
    {
      if (!newFormat.hasField(field.getName()))
        result.addField(field.clone());
    }
    
    for (FieldFormat field : result)
    {
      field.setKeyField(false);
    }
    
    return result;
  }
}
