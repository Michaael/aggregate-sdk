package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.expression.util.Tracer;
import com.tibbo.aggregate.common.expression.util.TracerManager;

public class TraceFunction extends AbstractFunction
{
  public TraceFunction()
  {
    super("trace", Function.GROUP_OTHER, "Object value, String message", "Object", Cres.get().getString("fDescTrace"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, true, parameters);
    
    Object value = parameters[0];
    
    String message = parameters.length > 1 && parameters[1] != null ? parameters[1].toString() : null;
    
    Tracer tracer = evaluator.getTracer();
    
    if (tracer == null)
    {
      tracer = TracerManager.getDefaultTracer();
    }
    
    tracer.trace(value, message);
    
    return value;
  }
}
