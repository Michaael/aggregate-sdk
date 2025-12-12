package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.Util;

public class SetVariableFieldFunction extends AbstractFunction
{
  public SetVariableFieldFunction()
  {
    super("setVariableField", Function.GROUP_CONTEXT_RELATED, "String context, String variable, String field, Integer record, Object value", "Null", Cres.get().getString("fDescSetVariableField"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(5, true, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      int row = Util.convertToNumber(parameters[3], true, false).intValue();

      DefaultRequestController request = new DefaultRequestController();
      request.assignPinpoint(environment.obtainPinpoint());

      context.setVariableField(parameters[1].toString(), parameters[2].toString(), row, parameters[4],
          caller, request);
      
      return null;
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
