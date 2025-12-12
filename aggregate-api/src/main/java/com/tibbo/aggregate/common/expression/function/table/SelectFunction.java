package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.AbstractEvaluatingVisitor;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class SelectFunction extends AbstractFunction
{
  public SelectFunction()
  {
    super("select", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String fieldToSelect, String fieldToCheck, Object value", "Object", Cres.get().getString("fDescSelect"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters); // First three parameters cannot be null
    checkParameters(4, true, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = (DataTable) parameters[0];
    
    if (parameters[1] == null || parameters[2] == null)
    {
      throw new EvaluationException("Field parameter may not be NULL");
    }
    
    String fieldToSelect = parameters[1].toString();
    
    if (!table.getFormat().hasField(fieldToSelect))
    {
      throw new EvaluationException(Cres.get().getString("exprTableHasNoField") + fieldToSelect);
    }
    
    String fieldToCheck = parameters[2].toString();
    
    if (!table.getFormat().hasField(fieldToCheck))
    {
      throw new EvaluationException(Cres.get().getString("exprTableHasNoField") + fieldToCheck);
    }
    
    Object value = parameters[3];
    
    for (DataRecord rec : table)
    {
      if (AbstractEvaluatingVisitor.equal(rec.getValue(fieldToCheck), value))
      {
        return rec.getValue(fieldToSelect);
      }
    }
    
    return null;
  }
}
