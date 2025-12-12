package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class IntersectFunction extends AbstractFunction
{
  public IntersectFunction()
  {
    super("intersect", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable sourceTable, String fieldInSourceTable, DataTable sampleTable, String fieldInSampleTable [, Boolean filterType]", "DataTable",
        Cres.get().getString("fDescIntersect"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(4, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    checkParameterType(1, parameters[1], String.class);
    checkParameterType(2, parameters[2], DataTable.class);
    checkParameterType(3, parameters[3], String.class);
    
    DataTable sourceTable = (DataTable) parameters[0];
    String fieldInSourceTable = (String) parameters[1];
    DataTable sampleTable = (DataTable) parameters[2];
    String fieldInSampleTable = (String) parameters[3];
    
    Boolean filterType = false;
    if (parameters.length > 4)
    {
      filterType = Util.convertToBoolean(parameters[4], false, false);
    }
    
    TableFormat resultTableFormat = sourceTable.getFormat().clone();
    resultTableFormat.setMinRecords(TableFormat.DEFAULT_MIN_RECORDS);
    resultTableFormat.setMaxRecords(TableFormat.DEFAULT_MAX_RECORDS);
    
    DataTable resultTable = new SimpleDataTable(resultTableFormat);
    
    for (DataRecord rec : sourceTable)
    {
      if ((sampleTable.findIndex(fieldInSampleTable, rec.getValue(fieldInSourceTable)) == null) == filterType)
      {
        resultTable.addRecord(rec);
      }
    }
    
    return resultTable;
  }
}
