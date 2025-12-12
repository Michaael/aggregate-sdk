package com.tibbo.aggregate.common.expression.function.table;

import java.util.Arrays;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTableConstruction;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class TableFunction extends AbstractFunction
{
  public TableFunction()
  {
    super("table", Function.GROUP_DATA_TABLE_PROCESSING, "[String format [, Object field1, Object field2, ...]]", "DataTable", Cres.get().getString("fDescTable"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    try
    {
      if (parameters.length == 0)
      {
        return new SimpleDataTable();
      }
      
      checkParameters(1, false, parameters);
      
      String formatString = parameters[0].toString();
      
      TableFormat format = ExpressionUtils.readFormat(formatString);
      
      Object[] data = Arrays.copyOfRange(parameters, 1, parameters.length);
      
      return DataTableConstruction.constructTable(Arrays.asList(data), format, evaluator, null);
    }
    catch (DataTableException | SyntaxErrorException ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
