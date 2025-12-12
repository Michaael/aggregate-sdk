package com.tibbo.aggregate.common.expression.function.table;

import java.util.LinkedHashSet;
import java.util.Set;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableReplication;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DistinctFunction extends AbstractFunction
{
  public DistinctFunction()
  {
    super("distinct", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table", "DataTable", Cres.get().getString("fDescDistinct"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable source = (DataTable) parameters[0];
    
    TableFormat resultFormat = source.getFormat().clone();
    resultFormat.setMinRecords(TableFormat.DEFAULT_MIN_RECORDS);
    resultFormat.setMaxRecords(TableFormat.DEFAULT_MAX_RECORDS);
    
    DataTable result = new SimpleDataTable(resultFormat);
    
    Set<DataRecord> records = new LinkedHashSet<>();
    
    for (DataRecord rec : source)
    {
      records.add(rec);
    }
    
    for (DataRecord rec : records)
    {
      DataTableReplication.copyRecord(rec, result.addRecord(), true, true, false, true, null);
    }
    
    return result;
  }
}
