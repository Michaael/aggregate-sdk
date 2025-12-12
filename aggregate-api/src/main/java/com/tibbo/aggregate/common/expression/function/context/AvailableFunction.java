package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class AvailableFunction extends AbstractFunction
{
  public AvailableFunction()
  {
    super("available", Function.GROUP_CONTEXT_RELATED, "String context [, String schema]", "Boolean", Cres.get().getString("fDescAvailable"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    ReferenceResolver resolver = parameters.length > 1 ? evaluator.getResolver(parameters[1].toString()) : evaluator.getDefaultResolver();
    
    ContextManager cm = resolver.getContextManager();
    
    Context con = cm != null ? cm.get(parameters[0].toString(), evaluator.getDefaultResolver().getCallerController()) : null;
    
    return con != null;
  }
}
