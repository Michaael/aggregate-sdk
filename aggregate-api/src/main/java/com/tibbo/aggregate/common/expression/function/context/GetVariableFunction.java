package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.DefaultRequestController;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;

public class GetVariableFunction extends AbstractFunction
{
  public GetVariableFunction()
  {
    super("getVariable", Function.GROUP_CONTEXT_RELATED, "String context, String variable", "DataTable", Cres.get().getString("fDescGetVariable"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    CallerController caller = contextAndCaller.getSecond();
    
    try
    {
      DefaultRequestController request = new DefaultRequestController(evaluator);
      request.assignPinpoint(environment.obtainPinpoint());

      return context.getVariable(parameters[1].toString(), caller, request);
    }
    catch (ContextException ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
