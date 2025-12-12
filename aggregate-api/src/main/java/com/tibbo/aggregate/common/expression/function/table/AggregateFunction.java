package com.tibbo.aggregate.common.expression.function.table;

import java.text.MessageFormat;
import java.util.List;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class AggregateFunction extends AbstractFunction
{
  public AggregateFunction()
  {
    super("aggregate", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable|String source, String expression, Object initialValue", "Object", Cres.get().getString("fDescAggregate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    for (int i = 0; i < 2; i++)
    {
      if (parameters[i] == null)
      {
        throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprParamCantBeNull"), i));
      }
    }
    
    Object source = parameters[0];
    
    Expression expression = new Expression(parameters[1].toString());
    
    Object aggregate = parameters[2];
    
    ReferenceResolver resolver = evaluator.getDefaultResolver();
    
    Evaluator localEvaluator = new Evaluator(resolver.getContextManager(), resolver.getDefaultContext(), resolver.getDefaultTable(), resolver.getCallerController());
    localEvaluator.getEnvironmentResolver().setEnvironment(evaluator.getEnvironmentResolver().getEnvironment());
    localEvaluator.setTracer(evaluator.getTracer());
    localEvaluator.setPreviousResult(aggregate);
    
    try
    {
      EvaluationEnvironment envClone = environment != null ? environment.clone() : null;
      if (envClone != null && envClone.getCause() != null)
        envClone.getCause().setRow(null);
      if (source instanceof DataTable)
      {
        DataTable table = (DataTable) source;
        
        localEvaluator.getDefaultResolver().setDefaultTable(table);
        
        for (int i = 0; i < table.getRecordCount(); i++)
        {
          localEvaluator.getDefaultResolver().setDefaultRow(i);
          
          aggregate = localEvaluator.evaluate(expression, envClone);
        }
      }
      else
      {
        String mask = source.toString();
        
        List<Context> contexts = ContextUtils.expandMaskToContexts(mask, resolver.getContextManager(), resolver.getCallerController());
        
        for (Context context : contexts)
        {
          localEvaluator.getDefaultResolver().setDefaultContext(context);
          
          aggregate = localEvaluator.evaluate(expression, envClone);
        }
      }
    }
    catch (SyntaxErrorException ex)
    {
      throw new EvaluationException(ex);
    }
    
    return aggregate;
  }
}
