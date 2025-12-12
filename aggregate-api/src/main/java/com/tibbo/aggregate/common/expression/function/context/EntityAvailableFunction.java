package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.function.*;

public abstract class EntityAvailableFunction extends AbstractFunction
{
  public EntityAvailableFunction(String name, String group, String parametersFootprint, String description)
  {
    super(name, group, parametersFootprint, "Boolean", description);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    ReferenceResolver resolver = parameters.length > 2 ? evaluator.getResolver(parameters[2].toString()) : evaluator.getDefaultResolver();
    
    ContextManager cm = resolver.getContextManager();
    
    Context con = cm != null ? cm.get(parameters[0].toString(), resolver.getCallerController()) : null;
    
    return con != null && hasEntity(evaluator, con, parameters);
  }
  
  protected abstract boolean hasEntity(Evaluator evaluator, Context con, Object... parameters);
}
