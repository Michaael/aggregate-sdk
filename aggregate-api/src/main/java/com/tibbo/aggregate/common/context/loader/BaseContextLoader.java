package com.tibbo.aggregate.common.context.loader;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.DefaultContextVisitor;

public class BaseContextLoader implements ContextLoader
{
  protected final ContextManager contextManager;
  protected final CallerController callerController;
  
  public BaseContextLoader(@Nonnull ContextManager contextManager, @Nonnull CallerController callerController)
  {
    this.contextManager = contextManager;
    this.callerController = callerController;
  }
  
  @Override
  public LoadedContexts load(String parent) throws ContextException
  {
    Context parentNode = contextManager.get(parent, callerController);
    if (parentNode == null)
    {
      throw new ContextException(Cres.get().getString("conNotAvail") + parentNode);
    }

    List<Context> result = new ArrayList<>();
    parentNode.accept(new DefaultContextVisitor<Context>()
    {
      @Override
      public void visit(Context context)
      {
        result.add(context);
      }
    });

    return new PageableLoadedContexts(result, result.size());
  }
}
