package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.encoding.JsonEncodingHelper;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class TableToJSONFunction extends AbstractFunction
{
  
  public TableToJSONFunction(String group)
  {
    super("tableToJSON", group, "DataTable table", "String", Cres.get().getString("fDescTableToJSON"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    try
    {
      return JsonEncodingHelper.tableToJson((DataTable) parameters[0], false);
    }
    catch (Exception e)
    {
      throw new EvaluationException(e.getMessage(), e);
    }
  }
}
