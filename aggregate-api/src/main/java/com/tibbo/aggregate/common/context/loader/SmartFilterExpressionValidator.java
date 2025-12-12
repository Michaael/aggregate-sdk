package com.tibbo.aggregate.common.context.loader;

import static com.tibbo.aggregate.common.server.EditableChildContextConstants.V_CHILD_INFO;
import static com.tibbo.aggregate.common.server.ServerContextConstants.V_VISIBLE_INFO;

import javax.annotation.Nonnull;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

/**
 * Validator which will accept the context if it matches the expression provided, uses {childInfo} as a default table
 *
 * @author Alexander Sidorov
 * @since 24.10.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
public class SmartFilterExpressionValidator extends ExpressionContextValidator implements ContextValidator
{
  public static final String NAME = "name";
  
  private final String[] variablesForEvaluation = { V_CHILD_INFO, V_VISIBLE_INFO };
  
  public SmartFilterExpressionValidator(@Nonnull ContextManager contextManager, @Nonnull CallerController caller, @Nonnull Expression smartFilterExpression, @Nonnull Reference reference)
  {
    super(contextManager, caller, smartFilterExpression, reference);
  }
  
  @Override
  protected boolean evaluate(String contextPath, Context context, Evaluator evaluator)
  {
    
    DataTable defaultTable = prepareDefaultTableForPath(context.getPath());
    
    evaluator.setDefaultTable(defaultTable);
    
    try
    {
      if (evaluator.evaluateToBoolean(validityExpression, context, reference))
      {
        return true;
      }
    }
    catch (SyntaxErrorException | EvaluationException e)
    {
      Log.CONTEXT_CHILDREN.debug("Unable to evaluate children filter expression fo the context: " + contextPath, e);
    }
    
    for (String variable : variablesForEvaluation)
    {
      try
      {
        defaultTable = context.getVariable(variable, caller).clone();
      }
      catch (ContextException e)
      {
        Log.CONTEXT_CHILDREN.debug("Unable to get defaultTable for the context: " + contextPath, e);
        continue;
      }
      
      evaluator.setDefaultTable(defaultTable);
      
      try
      {
        if (evaluator.evaluateToBoolean(validityExpression, context, reference))
        {
          return true;
        }
      }
      catch (Exception ex)
      {
        Log.CONTEXT_CHILDREN.debug("Unable to evaluate children filter expression fo the context: " + contextPath, ex);
        continue;
      }
    }
    return false;
  }
  
  private DataTable prepareDefaultTableForPath(String path)
  {
    
    TableFormat format = new TableFormat(1, 1);
    format.addField(FieldFormat.create(NAME, FieldFormat.STRING_FIELD));
    DataTable dataTable = new SimpleDataTable(format);
    dataTable.addRecord(path);
    return dataTable;
  }
}
