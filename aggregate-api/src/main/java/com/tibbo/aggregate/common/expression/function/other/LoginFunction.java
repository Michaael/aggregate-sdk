package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;

public class LoginFunction extends AbstractFunction
{
  public LoginFunction()
  {
    super("login", Function.GROUP_OTHER, "", "String", Cres.get().getString("fDescLogin"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    CallerController cc = evaluator.getDefaultResolver().getCallerController();
    return cc != null ? cc.getLogin() : null;
  }
}
