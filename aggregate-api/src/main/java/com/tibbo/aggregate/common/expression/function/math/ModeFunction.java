package com.tibbo.aggregate.common.expression.function.math;

import java.text.MessageFormat;

import org.apache.commons.math3.stat.StatUtils;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class ModeFunction extends AbstractFunction
{
  public ModeFunction()
  {
    super("mode", Function.GROUP_MATH, "DataTable table [, String field]", "Double", Cres.get().getString("fDescMode"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    DataTable table = (DataTable) parameters[0];
    if (table.getRecordCount() == 0)
    {
      return (double) 0;
    }
    
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
    
    int numRecords = table.getRecordCount();
    double[] seriesData = new double[numRecords];
    int count = 0;
    for (DataRecord rec : table)
    {
      if ((rec.getValue(field) instanceof Number) && !(rec.getValue(field) instanceof Long))
      {
        seriesData[count] = ((Number) rec.getValue(field)).doubleValue();
      }
      else
      {
        throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), field, Double.class.getSimpleName(), rec.getValue(field).getClass().getSimpleName()));
      }
      count++;
    }
    return StatUtils.mode(seriesData)[0];
  }
}
