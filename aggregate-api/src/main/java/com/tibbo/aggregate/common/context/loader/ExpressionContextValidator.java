package com.tibbo.aggregate.common.context.loader;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.ReferenceResolver;

/**
 * Validator which will accept the context if it matches the expression provided
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
@Immutable
public class ExpressionContextValidator implements ContextValidator
{
  
  protected final ContextManager contextManager;
  protected final CallerController caller;
  protected final Expression validityExpression;
  protected final Reference reference;
  
  public ExpressionContextValidator(@Nonnull ContextManager contextManager, @Nonnull CallerController caller, @Nonnull Expression validityExpression, @Nonnull Reference reference)
  {
    this.contextManager = contextManager;
    this.caller = caller;
    this.validityExpression = validityExpression;
    this.reference = reference;
  }
  
  protected DataTable getDefaultTable(Context context) throws ContextException
  {
    return null;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    final Evaluator evaluator = new Evaluator(new DefaultReferenceResolver());
    evaluator.getDefaultResolver().setContextManager(contextManager);
    evaluator.getDefaultResolver().setCallerController(caller);
    
    if (validityExpression.getText().isEmpty())
    {
      return true;
    }
    ReferenceResolver defaultResolver = evaluator.getDefaultResolver();
    
    CallerController caller = defaultResolver.getCallerController();
    
    Context context = defaultResolver.getContextManager().get(contextPath, caller);
    
    if (context == null)
    {
      return false;
    }
    
    evaluator.setDefaultContext(context);
    
    return evaluate(contextPath, context, evaluator);
  }

  protected boolean evaluate(String contextPath, Context context, Evaluator evaluator)
  {
    DataTable defaultTable = null;
    try
    {
      defaultTable = getDefaultTable(context);
    }
    catch (ContextException e)
    {
      Log.CONTEXT_CHILDREN.debug("Unable to get defaultTable for the context: " + contextPath, e);
    }
    
    if (defaultTable != null)
    {
      evaluator.setDefaultTable(defaultTable);
    }
    
    try
    {
      return evaluator.evaluateToBoolean(validityExpression, context, reference);
    }
    catch (Exception ex)
    {
      Log.CONTEXT_CHILDREN.debug("Unable to evaluate children filter expression fo the context: " + contextPath, ex);
      return false;
    }
  }
}
