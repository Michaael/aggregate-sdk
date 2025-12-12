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
import com.tibbo.aggregate.common.util.Util;

public class AdjustRecordLimitsFunction extends AbstractFunction
{
  public AdjustRecordLimitsFunction()
  {
    super("adjustRecordLimits", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Integer minRecords, Integer maxRecords", "DataTable", Cres.get().getString("fDescAdjustRecordLimits"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = (DataTable) parameters[0];
    
    int minRecords = Util.convertToNumber(parameters[1], true, false).intValue();
    
    int maxRecords = Util.convertToNumber(parameters[2], true, false).intValue();
    
    TableFormat newFormat = table.getFormat().clone().setMinRecords(minRecords).setMaxRecords(maxRecords);
    
    DataTable result = new SimpleDataTable(newFormat);
    
    DataTableReplication.copy(table, result, true, true, true, true, true);
    
    return result;
  }
}
