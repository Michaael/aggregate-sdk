package com.tibbo.aggregate.common.expression.function.other;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Pair;
import com.tibbo.aggregate.common.util.SyntaxErrorException;
import com.tibbo.aggregate.common.util.Util;

public class EvaluateFunction extends AbstractFunction
{
  public EvaluateFunction()
  {
    super("evaluate", Function.GROUP_OTHER, "String expression [, String defaultContext [, DataTable defaultTable [, Integer defaultRow]]]", "Object", Cres.get().getString("fDescEvaluate"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(1, false, parameters);
    
    Object previousBackup = evaluator.getPreviousResult();
    Context defaultContextBackup = evaluator.getDefaultResolver().getDefaultContext();
    DataTable defaultTableBackup = evaluator.getDefaultResolver().getDefaultTable();
    Integer defaultRowBackup = evaluator.getDefaultResolver().getDefaultRow();
    
    try
    {
      if (parameters.length >= 2 && parameters[1] != null && evaluator.getDefaultResolver().getContextManager() != null)
      {
        String contextPath = parameters[1].toString();
        Pair<Context, CallerController> contextAndCaller = resolveContext(contextPath, evaluator);
        Context<?> context = contextAndCaller.getFirst();
        CallerController caller = contextAndCaller.getSecond();
        
        evaluator.getDefaultResolver().setDefaultContext(context);
        evaluator.getDefaultResolver().setCallerController(caller);
      }
      
      if (parameters.length >= 3)
      {
        checkParameterType(2, parameters[2], DataTable.class);
        
        evaluator.getDefaultResolver().setDefaultTable((DataTable) parameters[2]);
      }
      
      if (parameters.length >= 4)
      {
        evaluator.getDefaultResolver().setDefaultRow(Util.convertToNumber(parameters[3], false, false).intValue());
      }
      
      return evaluator.evaluate(new Expression(parameters[0].toString()), environment.clone());
    }
    catch (SyntaxErrorException ex)
    {
      throw new EvaluationException(ex);
    }
    finally
    {
      evaluator.getDefaultResolver().setDefaultRow(defaultRowBackup);
      evaluator.getDefaultResolver().setDefaultTable(defaultTableBackup);
      evaluator.getDefaultResolver().setDefaultContext(defaultContextBackup);
      evaluator.restorePreviousResult(previousBackup);
    }
  }
}
