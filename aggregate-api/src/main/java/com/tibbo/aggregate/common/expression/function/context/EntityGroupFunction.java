package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.expression.function.*;

public abstract class EntityGroupFunction extends AbstractFunction
{

  public EntityGroupFunction(String name, String group, String parametersFootprint, String description)
  {
    super(name, group, parametersFootprint, "String", description);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    ReferenceResolver resolver = parameters.length > 2 ? evaluator.getResolver(parameters[2].toString()) : evaluator.getDefaultResolver();
    
    ContextManager cm = resolver.getContextManager();
    
    Context con = cm != null ? cm.get(parameters[0].toString(), resolver.getCallerController()) : null;
    
    return con != null ? getGroup(evaluator, con, parameters) : null;
  }
  
  protected abstract String getGroup(Evaluator evaluator, Context con, Object... parameters);
}
