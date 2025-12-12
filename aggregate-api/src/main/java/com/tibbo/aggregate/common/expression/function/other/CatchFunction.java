package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class CatchFunction extends AbstractFunction
{
  public CatchFunction()
  {
    super("catch", Function.GROUP_OTHER, "Object normalResult [, Object errorResult]", "Object", Cres.get().getString("fDescCatch"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    // Never called, that's a special function those body is actually in AbstractEvaluatingVisitor.visit(ASTFunctionNode)
    return null;
  }
}
