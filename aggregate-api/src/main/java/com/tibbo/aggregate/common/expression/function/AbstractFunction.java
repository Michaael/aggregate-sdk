package com.tibbo.aggregate.common.expression.function;

import static java.lang.String.format;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.expression.AttributedObject;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.util.Pair;

public abstract class AbstractFunction implements Function
{
  private final String name;
  private final String parametersFootprint;
  private final String category;
  private final String returnValue;
  private final String description;

  public AbstractFunction(String name, String category, String parametersFootprint, String returnValue, String description)
  {
    this.name = name;
    this.category = category;
    this.parametersFootprint = parametersFootprint;
    this.returnValue = returnValue;
    this.description = description;
  }

  public String getName() {
    return name;
  }
  @Override
  public String getCategory()
  {
    return category;
  }
  
  @Override
  public String getReturnValue()
  {
    return returnValue;
  }
  
  @Override
  public String getParametersFootprint()
  {
    return parametersFootprint;
  }
  
  @Override
  public String getDescription()
  {
    return description;
  }
  
  protected void checkParameters(int minimalCount, boolean allowNulls, Object... parameters) throws EvaluationException
  {
    if (parameters.length < minimalCount)
    {
      throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParamCount"), minimalCount, parameters.length));
    }
    
    if (!allowNulls)
    {
      for (int i = 0; i < minimalCount; i++)
      {
        if (parameters[i] == null)
        {
          throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprParamCantBeNull"), i));
        }
      }
    }
  }
  
  protected void checkParameterType(int num, Object value, Class requiredType) throws EvaluationException
  {
    if (value == null)
    {
      throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), num, requiredType.getSimpleName(), "NULL"));
    }

    if (!(requiredType.isAssignableFrom(value.getClass())))
    {
      throw new EvaluationException(MessageFormat.format(Cres.get().getString("exprInvalidParameterType"), num, requiredType.getSimpleName(), value.getClass().getSimpleName()));
    }
  }
  
  @Override
  public AttributedObject executeAttributed(Evaluator evaluator, EvaluationEnvironment environment, AttributedObject... parameters) throws EvaluationException
  {
    Object[] convertedParameters = new Object[parameters.length];
    
    for (int i = 0; i < parameters.length; i++)
    {
      convertedParameters[i] = ExpressionUtils.getValue(parameters[i]);
    }
    
    return ExpressionUtils.toAttributed(execute(evaluator, environment, convertedParameters));
  }
  
  protected void executeTasks(List<Callable<Void>> tasks, ExecutorService executorService, boolean executeConcurrent) throws EvaluationException
  {
    try
    {
      if (executeConcurrent && executorService != null)
      {
        try
        {
          List<Future<Void>> futures = executorService.invokeAll(tasks);
          for (Future<Void> future : futures)
          {
            future.get();
          }
        }
        catch (InterruptedException | ExecutionException e)
        {
          throw new EvaluationException(e);
        }
      }
      else
      {
        for (Callable task : tasks)
        {
          task.call();
        }
      }
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex);
    }
  }

  /**
   * Tries to find the instance of context denoted by given path. For this, iterates over {@code evaluator}'s resolvers 
   * and "asks" each of them about the context. This allows to correctly resolve specific contexts without explicit 
   * schema, e.g. refer to a dashboard component without {@code form:} prefix. 
   * @param contextPath path of context to resolve
   * @param evaluator current evaluator with resolvers
   * @return a pair of context and corresponding caller controller
   * @throws EvaluationException if no context with given path found among resolvers
   */
  protected Pair<Context, CallerController> resolveContext(String contextPath, Evaluator evaluator) throws EvaluationException {
    Context con = null;
    CallerController callerController = null;

    for (ReferenceResolver resolver : evaluator.getResolvers().values())
    {
      ContextManager cm = resolver.getContextManager();
      if (cm == null)
      {
        continue;
      }

      callerController = resolver.getCallerController();

      con = cm.get(contextPath, callerController);

      if (con != null)
      {
        break;
      }
    }

    if (con == null)
    {
      throw new EvaluationException(format("%s %s", Cres.get().getString("conNotAvail"), contextPath));
    }
    
    return new Pair<>(con, callerController);
  }
  
}
