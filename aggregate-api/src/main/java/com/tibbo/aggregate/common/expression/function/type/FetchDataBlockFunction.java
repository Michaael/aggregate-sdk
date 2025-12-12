package com.tibbo.aggregate.common.expression.function.type;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class FetchDataBlockFunction extends AbstractFunction
{
  public FetchDataBlockFunction()
  {
    super("fetchDataBlock", Function.GROUP_OTHER, "DataBlock data", "DataBlock", Cres.get().getString("fDescFetchDataBlock"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    Data data = (Data) parameters[0];
    
    try
    {
      data.fetchData(evaluator.getDefaultResolver().getContextManager(), evaluator.getDefaultResolver().getCallerController());
      return data;
    }
    catch (ContextException e)
    {
      throw new EvaluationException(e.getMessage(), e);
    }
  }
}
