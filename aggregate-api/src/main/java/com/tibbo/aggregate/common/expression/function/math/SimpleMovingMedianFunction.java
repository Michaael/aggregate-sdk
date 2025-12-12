package com.tibbo.aggregate.common.expression.function.math;

import java.text.MessageFormat;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class SimpleMovingMedianFunction extends AbstractFunction
{
  public SimpleMovingMedianFunction()
  {
    super("smm", Function.GROUP_MATH, "DataTable table, String field, Long  deepness", "DataTable", Cres.get().getString("fDescSimpleMovingMedian"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    checkParameterType(0, parameters[0], DataTable.class);
    DataTable table = (DataTable) parameters[0];
    int deepness = ((Number) parameters[2]).intValue();
    if (deepness <= 0 && deepness > table.getRecordCount())
    {
      throw new EvaluationException("Deepness can't be 0");
    }
    if (deepness > table.getRecordCount())
    {
      throw new EvaluationException("Deepness can't be larger than the table size");
    }
    String field;
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
    
    TableFormat resultTableFormat = new TableFormat();
    resultTableFormat.addField(table.getFormat(field));
    resultTableFormat.addField(FieldFormat.create("average", FieldFormat.DOUBLE_FIELD, "Average").setNullable(true));
    DataTable resultTable = new SimpleDataTable(resultTableFormat);
    double[] seriesData = new double[deepness];
    Median median = new Median();
    int count = 0;
    for (DataRecord rec : table)
    {
      if ((rec.getValue(field) instanceof Number) && !(rec.getValue(field) instanceof Long))
      {
        
        System.arraycopy(seriesData, 1, seriesData, 0, seriesData.length - 1);
        seriesData[deepness - 1] = ((Number) rec.getValue(field)).doubleValue();
        if (deepness - 1 <= count)
        {
          resultTable.addRecord(rec.getValue(field), median.evaluate(seriesData));
        }
        else
        {
          resultTable.addRecord(rec.getValue(field), null);
        }
      }
      else
      {
        throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), field, Double.class.getSimpleName(), rec.getValue(field).getClass().getSimpleName()));
      }
      count++;
    }
    
    return resultTable;
  }
}
