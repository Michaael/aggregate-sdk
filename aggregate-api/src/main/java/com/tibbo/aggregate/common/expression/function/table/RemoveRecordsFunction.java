package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.expression.AbstractEvaluatingVisitor;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class RemoveRecordsFunction extends AbstractFunction
{
  public RemoveRecordsFunction()
  {
    super("removeRecords", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String fieldToCheck, Object value", "DataTable", Cres.get().getString("fDescRemoveRecords"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    checkParameters(3, true, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    if (parameters[0] == null || parameters[1] == null)
    {
      throw new EvaluationException("Field parameter may not be NULL");
    }
    
    DataTable sourceTable = ((DataTable) parameters[0]);
    
    String fieldToCheck = parameters[1].toString();
    
    if (!sourceTable.getFormat().hasField(fieldToCheck))
    {
      throw new EvaluationException(Cres.get().getString("exprTableHasNoField") + fieldToCheck);
    }
    
    Object value = parameters[2];
    
    DataTable resultTable = new SimpleDataTable(sourceTable.getFormat());
    
    for (DataRecord rec : sourceTable)
    {
      if (AbstractEvaluatingVisitor.equal(rec.getValue(fieldToCheck), value))
      {
        continue;
      }
      resultTable.addRecord(rec.clone());
    }
    
    return resultTable;
  }
  
}
