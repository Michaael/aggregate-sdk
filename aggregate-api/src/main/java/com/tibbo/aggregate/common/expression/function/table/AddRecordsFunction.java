package com.tibbo.aggregate.common.expression.function.table;

import java.util.Arrays;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConstruction;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class AddRecordsFunction extends AbstractFunction
{
  public AddRecordsFunction()
  {
    super("addRecords", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, Object field1, Object field2, ...", "DataTable", Cres.get().getString("fDescAddRecords"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    try
    {
      checkParameters(1, true, parameters);
      
      checkParameterType(0, parameters[0], DataTable.class);
      
      DataTable table = ((DataTable) parameters[0]).clone();
      
      Object[] data = Arrays.copyOfRange(parameters, 1, parameters.length);
      
      DataTable additionalRecordsTable = DataTableConstruction.constructTable(Arrays.asList(data), table.getFormat(), evaluator, null).clone();
      
      for (DataRecord rec : additionalRecordsTable)
      {
        table.addRecord(rec);
      }
      
      return table;
    }
    catch (DataTableException | SyntaxErrorException ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
