package com.tibbo.aggregate.common.expression.function.math;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.math3.stat.Frequency;

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

public class FrequencyFunction extends AbstractFunction
{
  final static String FOF_VALUE = "value";
  final static String FOF_FREQUENCY = "frequency";
  
  public FrequencyFunction()
  {
    super("frequency", Function.GROUP_MATH, "DataTable table [, String field]", "DataTable", Cres.get().getString("fDescFrequency"));
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
    Frequency frequency = new Frequency();
    
    for (DataRecord rec : table)
    {
      if ((rec.getValue(field) instanceof Number))
      {
        frequency.addValue(((Number) rec.getValue(field)).longValue());
      }
      else
      {
        throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), field, Long.class.getSimpleName(), rec.getValue(field).getClass().getSimpleName()));
      }
    }
    TableFormat resultTableFormat = new TableFormat();
    resultTableFormat.addField(FieldFormat.create(FOF_VALUE, FieldFormat.LONG_FIELD, Cres.get().getString("value")));
    resultTableFormat.addField(FieldFormat.create(FOF_FREQUENCY, FieldFormat.LONG_FIELD, Cres.get().getString("frequency")));
    DataTable resultTable = new SimpleDataTable(resultTableFormat);
    Iterator<Map.Entry<Comparable<?>, Long>> entryIterator = frequency.entrySetIterator();
    while (entryIterator.hasNext())
    {
      Map.Entry<Comparable<?>, Long> next = entryIterator.next();
      resultTable.addRecord(next.getKey(), next.getValue());
      
    }
    
    return resultTable;
  }
}
