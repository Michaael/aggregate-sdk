package com.tibbo.aggregate.common.context.loader;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;


public class FilteredContextLoader implements ContextLoader
{
  private final ContextLoader origin;
  private final ContextValidator validator;

  public FilteredContextLoader(@Nonnull ContextLoader origin, @Nonnull ContextValidator validator)
  {
    this.origin = origin;
    this.validator = validator;
  }
  
  @Override
  public LoadedContexts load(String parent) throws ContextException
  {
    LoadedContexts loadedContexts = origin.load(parent);

    List<Context> result = new ArrayList<>();

    for (Context context : loadedContexts.getContexts())
    {
      String contextPath = context.getPath();
      if (validator.validate(contextPath))
      {
        result.add(context);
      }
    }
    return new PageableLoadedContexts(result, result.size());
  }
}
