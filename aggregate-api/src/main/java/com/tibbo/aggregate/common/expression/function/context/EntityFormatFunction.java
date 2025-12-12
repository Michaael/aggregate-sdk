package com.tibbo.aggregate.common.expression.function.context;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTableBuilding;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public abstract class EntityFormatFunction extends AbstractFunction
{
  public EntityFormatFunction(String name, String parametersFootprint, String description)
  {
    super(name, Function.GROUP_CONTEXT_RELATED, parametersFootprint, "String", description);
  }

  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);

    ReferenceResolver resolver;
    if (parameters.length > 2) {
      resolver = parameters[2] instanceof Boolean ? evaluator.getDefaultResolver() : evaluator.getResolver(parameters[2].toString());
    } else {
      resolver = evaluator.getDefaultResolver();
    }

    ContextManager cm = resolver.getContextManager();
    
    Context con = cm != null ? cm.get(parameters[0].toString(), evaluator.getDefaultResolver().getCallerController()) : null;
    
    return con != null ? getFormat(evaluator, con, parameters) : null;
  }
  protected Object getFormatPresentation(TableFormat tf, boolean asTable)
  {
    if (tf == null)
    {
      return null;
    }

    return asTable ? DataTableBuilding.formatToTable(tf) : tf.encode(false);
  }

  protected abstract Object getFormat(Evaluator evaluator, Context con, Object... parameters);
}
