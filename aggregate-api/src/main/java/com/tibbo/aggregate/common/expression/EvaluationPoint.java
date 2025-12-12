package com.tibbo.aggregate.common.expression;

import com.tibbo.aggregate.common.expression.parser.*;

public interface EvaluationPoint
{
  boolean isDebuggingEvaluations();
  
  void processEvaluation(Evaluator evaluator, Expression expression, Reference holder, Object result, NodeEvaluationDetails details);
  
  void processEvaluationError(Evaluator evaluator, Expression expression, Reference holder, Exception error, NodeEvaluationDetails details);
  
}
