package com.tibbo.aggregate.common.expression.function.table;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.ReferenceResolver;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class PrintFunction extends AbstractFunction
{
  public PrintFunction()
  {
    super("print", Function.GROUP_DATA_TABLE_PROCESSING, "DataTable table, String expression, String separator", "String", Cres.get().getString("fDescPrint"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(3, false, parameters);
    
    checkParameterType(0, parameters[0], DataTable.class);
    
    DataTable table = (DataTable) parameters[0];
    
    Expression expression = new Expression(parameters[1].toString());
    
    String separator = parameters[2].toString();
    
    StringBuilder sb = new StringBuilder();
    
    ReferenceResolver resolver = new DefaultReferenceResolver();
    resolver.setContextManager(evaluator.getDefaultResolver().getContextManager());
    Evaluator localEvaluator = new Evaluator(resolver);
    
    localEvaluator.getDefaultResolver().setDefaultTable(table);
    
    Object result;
    boolean added = false;
    
    for (int i = 0; i < table.getRecordCount(); i++)
    {
      try
      {
        
        localEvaluator.getDefaultResolver().setDefaultRow(i);
        
        result = localEvaluator.evaluate(expression);
        
        if (result == null)
        {
          continue;
        }
        
        if (added && result != null)
        {
          sb.append(separator);
        }
        
        sb.append(result != null ? result.toString() : "");
        
        added = true;
      }
      catch (SyntaxErrorException ex)
      {
        throw new EvaluationException(ex);
      }
    }
    
    return sb.toString();
  }
}
