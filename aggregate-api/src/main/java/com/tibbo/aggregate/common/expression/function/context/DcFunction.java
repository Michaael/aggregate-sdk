package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class DcFunction extends AbstractFunction
{
  public DcFunction()
  {
    super("dc", Function.GROUP_CONTEXT_RELATED, "[String schema]", "String", Cres.get().getString("fDescDc"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    ReferenceResolver resolver = parameters.length > 0 ? evaluator.getResolver(parameters[0].toString()) : evaluator.getDefaultResolver();
    
    Context dc = resolver.getDefaultContext();
    
    return dc != null ? dc.getPath() : null;
  }
}
