package com.tibbo.aggregate.common.context.loader;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;

/**
 * Validator which will accept the context if ANY of its children will pass the validation
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
@Immutable
public class RecursiveContextValidator implements ContextValidator
{
  
  private final ContextManager contextManager;
  private final CallerController callerController;
  
  private final ContextValidator contextValidator;
  
  public RecursiveContextValidator(@Nonnull ContextManager contextManager, @Nonnull CallerController callerController, @Nonnull ContextValidator contextValidator)
  {
    this.contextManager = contextManager;
    this.callerController = callerController;
    this.contextValidator = contextValidator;
  }
  
  @Override
  public boolean validate(String contextPath)
  {
    if (contextValidator.validate(contextPath))
    {
      return true;
    }
    
    Context context = contextManager.get(contextPath, callerController);
    
    if (context == null)
    {
      return false;
    }
    
    return checkChildrenRecursively(context);
  }
  
  private boolean checkChildrenRecursively(Context context)
  {
    List<Context> visibleChildren = context.getVisibleChildren(callerController);
    
    for (Context visibleChild : visibleChildren)
    {
      if (contextValidator.validate(visibleChild.getPath()))
      {
        return true;
      }
      if (checkChildrenRecursively(visibleChild))
      {
        return true;
      }
    }
    return false;
  }
}
