package com.tibbo.aggregate.common.expression.function.table;

import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class CellFunction extends AbstractFunction
{
  public CellFunction()
  {
    super("cell", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table [, String field [, Integer row [, Boolean description]]]", "Object", Cres.get().getString("fDescCell"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    
    boolean svdesc = false;
    
    if (parameters.length >= 4)
    {
      svdesc = Util.convertToBoolean(parameters[3], false, false);
    }
    
    DataTable table = (DataTable) parameters[0];
    
    String field;
    
    if (parameters.length >= 2)
    {
      if (parameters[1] instanceof Number)
      {
        Number fieldIndex = (Number) parameters[1];
        
        if (table.getFieldCount() < fieldIndex.intValue())
        {
          throw new EvaluationException(Cres.get().getString("exprTableHasNoFieldIndex") + fieldIndex.intValue());
        }
        
        field = table.getFormat(fieldIndex.intValue()).getName();
      }
      else
      {
        field = parameters[1].toString();
      }
    }
    else
    {
      field = table.getFormat(0).getName();
    }
    
    if (!table.hasField(field))
    {
      throw new EvaluationException(Cres.get().getString("exprTableHasNoField") + field + " (" + table.getFormat() + ")");
    }
    
    Number record = (parameters.length > 2) ? Util.convertToNumber(parameters[2], false, false) : 0;
    
    if (table.getRecordCount() <= record.intValue())
    {
      throw new EvaluationException(Cres.get().getString("exprTableHasNoRecordIndex") + record.intValue());
    }
    
    DataRecord rec = table.getRecord(record.intValue());
    
    Object value = rec.getValue(field);
    
    if (svdesc)
    {
      Map<Object, String> selvals = rec.getFormat(field).getSelectionValues();
      String desc = selvals.get(value);
      return desc != null ? desc : value;
    }
    
    return value;
  }
}
