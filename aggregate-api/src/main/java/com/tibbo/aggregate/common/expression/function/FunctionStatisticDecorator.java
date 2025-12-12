package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.expression.AttributedObject;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;

public class FunctionStatisticDecorator implements Function
{
  private final Function delegate;
  
  public FunctionStatisticDecorator(Function delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public String getName()
  {
    return delegate.getName();
  }

  @Override
  public String getCategory()
  {
    return delegate.getCategory();
  }
  
  @Override
  public String getReturnValue()
  {
    return delegate.getReturnValue();
  }
  
  @Override
  public String getParametersFootprint()
  {
    return delegate.getParametersFootprint();
  }
  
  @Override
  public String getDescription()
  {
    return delegate.getDescription();
  }
  
  @Override
  public AttributedObject executeAttributed(Evaluator evaluator, EvaluationEnvironment environment, AttributedObject... parameters) throws EvaluationException
  {
    Evaluator.EvaluationStatistics.onFunctionCall(delegate.getName());
    return delegate.executeAttributed(evaluator, environment, parameters);
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    Evaluator.EvaluationStatistics.onFunctionCall(delegate.getName());
    return delegate.execute(evaluator, environment, parameters);
  }
}
