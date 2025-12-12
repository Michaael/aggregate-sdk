package com.tibbo.aggregate.common;

import java.util.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class VariableValidator
{
  private final Map<String, String> expressionByMask = new HashMap<>();
  
  public void addExpressionByMask(String mask, String expression)
  {
    expressionByMask.put(mask, expression);
  }
  
  public String getExpressionForContext(String path)
  {
    // Оптимизация: итерация по entrySet() вместо keySet() + get()
    // Это избегает двойного доступа к Map и более эффективно
    for (Map.Entry<String, String> entry : expressionByMask.entrySet())
    {
      String mask = entry.getKey();
      if (!ContextUtils.matchesToMask(mask, path))
      {
        return entry.getValue();
      }
    }
    return null;
  }
  
  public Object validate(Context context, ContextManager contextManager, DataTable value) throws ContextException
  {
    String expression = getExpressionForContext(context.getPath());
    if (expression == null)
    {
      return null;
    }
    
    Evaluator evaluator = new Evaluator(contextManager, contextManager.getCallerController(), true);
    evaluator.setDefaultContext(context);
    evaluator.setDefaultTable(value);
    try
    {
      return evaluator.evaluate(new Expression(expression), new EvaluationEnvironment());
    }
    catch (SyntaxErrorException | EvaluationException e)
    {
      throw new ContextException(e);
    }
  }
}
