package com.tibbo.aggregate.common.expression.function.context;

import java.util.Stack;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;

public class FullDescriptionFunction extends AbstractFunction
{
  public FullDescriptionFunction()
  {
    super("fullDescription", Function.GROUP_CONTEXT_RELATED, "String context, String delimiter", "String", Cres.get().getString("fDescFullDescription"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);

    String contextPath = parameters[0].toString();
    Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
    Context<?> context = contextAndCaller.getFirst();
    
    String delimiter = "-";
    if (parameters.length > 1)
    {
      delimiter = parameters[1].toString();
    }
    
    try
    {
      Stack<Context> path = new Stack<>();
      
      do
      {
        path.push(context);
        context = context.getParent();
      }
      while (context.getParent() != null);
      
      StringBuilder sb = new StringBuilder();
      while (!path.isEmpty())
      {
        if (sb.length() != 0)
        {
          sb.append(delimiter);
        }
        context = path.pop();
        sb.append(context.getDescription());
      }
      
      return sb.toString();
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }
}
