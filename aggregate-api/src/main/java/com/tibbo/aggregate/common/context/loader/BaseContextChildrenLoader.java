package com.tibbo.aggregate.common.context.loader;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;

@Immutable
public class BaseContextChildrenLoader extends BaseContextLoader implements ContextLoader
{
  public BaseContextChildrenLoader(@Nonnull ContextManager contextManager, @Nonnull CallerController callerController)
  {
    super(contextManager, callerController);
  }

  @Override
  public LoadedContexts load(String parent) throws ContextException
  {
    Context parentNode = contextManager.get(parent, callerController);
    if (parentNode == null)
    {
      throw new ContextException(Cres.get().getString("conNotAvail") + parentNode);
    }
    List<Context> result = parentNode.getVisibleChildren(callerController);
    return new PageableLoadedContexts(result, result.size());
  }
}
