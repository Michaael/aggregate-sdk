package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.encoding.JsonEncodingHelper;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class TableFromJSONFunction extends AbstractFunction
{

  public TableFromJSONFunction()
  {
    this("tableFromJSON", Function.GROUP_DATA_TABLE_PROCESSING);
  }

  public TableFromJSONFunction(String name, String group)
  {
    super(name, group, "String json [, Boolean convertUnequalFieldTypesToString]", " DataTable ", Cres.get().getString("fDescTableFromJSON"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    boolean convertUnequalFieldTypesToString = false;
    if (parameters.length > 1)
    {
      checkParameterType(1, parameters[1], Boolean.class);
      convertUnequalFieldTypesToString = (boolean) parameters[1];
    }
    
    try
    {
      return JsonEncodingHelper.tableFromJson(parameters[0].toString(), convertUnequalFieldTypesToString).get();
    }
    catch (Exception e)
    {
      throw new EvaluationException(e.getMessage(), e);
    }
  }
}
